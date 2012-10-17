package com.mgmtp.jfunk.data.excel;

import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static com.google.common.io.Closeables.closeQuietly;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.mgmtp.jfunk.common.config.ScriptScoped;
import com.mgmtp.jfunk.common.exception.JFunkException;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.data.DataSet;
import com.mgmtp.jfunk.data.DefaultDataSet;
import com.mgmtp.jfunk.data.excel.ExcelDataSource.ExcelFile.DataOrientation;
import com.mgmtp.jfunk.data.source.BaseDataSource;

/**
 * @author rnaegele
 * @version $Id: $
 */
@ScriptScoped
public class ExcelDataSource extends BaseDataSource {

	private List<ExcelFile> excelFiles;
	private final Map<String, MutableInt> dataSetIndices = newHashMap();

	@Inject
	public ExcelDataSource(final Configuration configuration) {
		super(configuration);
	}

	/**
	 * CSV files are not loaded here, this is done on-access (lazy loading).
	 */
	protected List<ExcelFile> getExcelFiles() {
		if (excelFiles == null) {
			excelFiles = newArrayListWithCapacity(3);

			for (int i = 0;; ++i) {
				String baseKey = "dataSource." + getName() + "." + i;
				String path = configuration.get(baseKey + ".path");
				if (path == null) {
					break;
				}
				String doString = configuration.get(baseKey + ".dataOrientation", "rowbased");
				DataOrientation dataOrientation = DataOrientation.valueOf(doString);

				log.info("Opening Excel file: {}", path);
				ExcelFile file = new ExcelFile(new File(path), dataOrientation);

				try {
					file.open();
				} catch (InvalidFormatException ex) {
					throw new JFunkException(ex.getMessage(), ex);
				} catch (IOException ex) {
					throw new JFunkException(ex.getMessage(), ex);
				}

				excelFiles.add(file);
			}
		}
		return excelFiles;
	}

	/**
	 * Goes through all configured Excel files until data for the specified key is found. All sheets
	 * of a file a check before the next file is considered.
	 */
	@Override
	protected DataSet getNextDataSetImpl(final String dataSetKey) {
		for (ExcelFile excelFile : getExcelFiles()) {
			Map<String, List<Map<String, String>>> data = excelFile.getData();
			List<Map<String, String>> dataList = data.get(dataSetKey);
			if (dataList != null) {
				MutableInt counter = dataSetIndices.get(dataSetKey);
				if (counter == null) {
					counter = new MutableInt(0);
					dataSetIndices.put(dataSetKey, counter);
				}

				if (counter.getValue() >= dataList.size()) {
					// no more data available
					return null;
				}

				Map<String, String> dataMap = dataList.get(counter.getValue());
				counter.increment();
				return new DefaultDataSet(dataMap);
			}
		}
		return null;
	}

	@Override
	public boolean hasMoreData(final String dataSetKey) {
		for (ExcelFile excelFile : getExcelFiles()) {
			Map<String, List<Map<String, String>>> data = excelFile.getData();
			List<Map<String, String>> dataList = data.get(dataSetKey);
			if (dataList != null) {
				MutableInt counter = dataSetIndices.get(dataSetKey);
				int size = dataList.size();
				return counter == null && size > 0 || size > counter.getValue();
			}
		}
		return false;
	}

	@Override
	public void doReset() {
		dataSetIndices.clear();
	}

	/**
	 * Loads an Excel file.
	 */
	static class ExcelFile {

		private final File file;
		private final DataOrientation dataOrientation;
		private Map<String, List<Map<String, String>>> data;

		public ExcelFile(final File file, final DataOrientation dataOrientation) {
			this.file = file;
			this.dataOrientation = dataOrientation;
		}

		public final void open() throws IOException, InvalidFormatException {
			FileInputStream fis = null;
			Workbook excelWorkbook;
			try {
				fis = new FileInputStream(file);
				excelWorkbook = WorkbookFactory.create(fis);
				excelWorkbook.setMissingCellPolicy(Row.RETURN_NULL_AND_BLANK);
			} finally {
				closeQuietly(fis);
			}

			final int sheetCount = excelWorkbook.getNumberOfSheets();

			Map<String, List<Map<String, String>>> dataMapListMap = newHashMapWithExpectedSize(sheetCount);

			for (int sheetIndex = 0; sheetIndex < sheetCount; ++sheetIndex) {
				List<String> headers = null;
				List<Map<String, String>> dataMapList = null;

				Sheet currentSheet = excelWorkbook.getSheetAt(sheetIndex);
				String sheetName = currentSheet.getSheetName();

				int rowCount = currentSheet.getLastRowNum() + 1;

				for (int rowIndex = 0; rowIndex < rowCount; ++rowIndex) {
					Row currentRow = currentSheet.getRow(rowIndex);
					short cellCount = currentRow.getLastCellNum();

					for (short cellIndex = 0; cellIndex < cellCount; ++cellIndex) {
						Cell currentCell = currentRow.getCell(cellIndex);

						String value = currentCell.getStringCellValue();

						switch (dataOrientation) {
							case rowbased:
								if (rowIndex == 0) {
									if (dataMapList == null) {
										dataMapList = newArrayListWithCapacity(cellCount);
									}
									if (headers == null) {
										headers = newArrayListWithCapacity(cellCount);
									}
									headers.add(value);
								} else {
									Map<String, String> dataMap;
									if (dataMapList.size() < rowIndex) {
										dataMap = newHashMapWithExpectedSize(cellCount);
										dataMapList.add(dataMap);
									} else {
										dataMap = dataMapList.get(rowIndex - 1);
									}

									dataMap.put(headers.get(cellIndex), value);
								}
								break;
							case columnbased:
								if (cellIndex == 0) {
									if (dataMapList == null) {
										dataMapList = newArrayListWithCapacity(rowCount);
									}
									if (headers == null) {
										headers = newArrayListWithCapacity(rowCount);
									}
									headers.add(value);
								} else {
									Map<String, String> dataMap;
									if (dataMapList.size() < cellIndex) {
										dataMap = newHashMapWithExpectedSize(rowCount);
										dataMapList.add(dataMap);
									} else {
										dataMap = dataMapList.get(cellIndex - 1);
									}
									dataMap.put(headers.get(rowIndex), value);
								}
								break;
							default:
								throw new IllegalStateException("Invalid data orientation type.");
						}
					}
				}

				dataMapListMap.put(sheetName, dataMapList);
			}

			data = dataMapListMap;
		}

		/**
		 * @return the data
		 */
		public Map<String, List<Map<String, String>>> getData() {
			return data;
		}

		public static enum DataOrientation {
			rowbased,
			columnbased
		}
	}
}
