package com.mgmtp.jfunk.data.excel;

import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.data.excel.ExcelDataSource.ExcelFile;
import com.mgmtp.jfunk.data.excel.ExcelDataSource.ExcelFile.DataOrientation;

/**
 * @author rnaegele
 * @version $Id: $
 */
public class ExcelDataSourceTest {

	private Map<String, List<Map<String, String>>> expectedData;

	@BeforeTest
	public void setUp() {
		expectedData = newHashMapWithExpectedSize(3);

		for (int sheetIndex = 0; sheetIndex < 2; ++sheetIndex) {
			List<Map<String, String>> dataList = newArrayListWithCapacity(3);
			expectedData.put("sheet_" + sheetIndex, dataList);

			for (int rowIndex = 0; rowIndex < 3; ++rowIndex) {
				Map<String, String> dataMap = newHashMapWithExpectedSize(3);
				dataList.add(dataMap);

				for (int cellIndex = 0; cellIndex < 3; ++cellIndex) {
					String key = String.format("sheet_%d_header_%d", sheetIndex, cellIndex);
					String value = String.format("sheet_%d_value_%d_%d", sheetIndex, rowIndex, cellIndex);
					dataMap.put(key, value);
				}
			}
		}
	}

	private void testExcelFile(final String path, final DataOrientation dataOrientation) throws InvalidFormatException, IOException {
		ExcelFile excelFile = new ExcelFile(new File(path), dataOrientation, new DataFormatter());
		excelFile.load();
		Map<String, List<Map<String, String>>> actualData = excelFile.getData();
		assertThat("data does not match expected data", actualData, equalTo(expectedData));
	}

	private void testGetNextDataSet(final String path, final DataOrientation dataOrientation) {
		ExcelDataSource ds = createDataSource(path, dataOrientation);

		Map<String, List<Map<String, String>>> actualData = newHashMapWithExpectedSize(3);

		for (int sheetIndex = 0; sheetIndex < 2; ++sheetIndex) {
			List<Map<String, String>> dataList = newArrayListWithCapacity(3);

			String dataSetKey = "sheet_" + sheetIndex;
			actualData.put(dataSetKey, dataList);

			while (ds.hasMoreData(dataSetKey)) {
				dataList.add(ds.getNextDataSet(dataSetKey).getDataView());
			}
		}

		assertThat("data does not match expected data", actualData, equalTo(expectedData));
	}

	private ExcelDataSource createDataSource(final String path, final DataOrientation dataOrientation) {
		Configuration config = new Configuration(Charsets.UTF_8);
		config.put("dataSource.excel.0.path", path);
		config.put("dataSource.excel.0.dataOrientation", dataOrientation.name());

		return new ExcelDataSource(config, new DataFormatter());
	}

	@Test
	public void testRowBasedExcelFile() throws InvalidFormatException, IOException {
		testExcelFile("src/test/resources/rowbased.xlsx", DataOrientation.rowbased);
	}

	@Test
	public void testColBasedExcelFile() throws InvalidFormatException, IOException {
		testExcelFile("src/test/resources/columnbased.xlsx", DataOrientation.columnbased);
	}

	@Test
	public void testGetNextDataSetRowBased() {
		testGetNextDataSet("src/test/resources/rowbased.xlsx", DataOrientation.rowbased);
	}

	@Test
	public void testGetNextDataSetColumnBased() {
		testGetNextDataSet("src/test/resources/columnbased.xlsx", DataOrientation.columnbased);
	}

	@Test
	public void testHasMoreData() {
		ExcelDataSource dataSource = createDataSource("src/test/resources/rowbased.xlsx", DataOrientation.rowbased);
		String dataSetKey = "sheet_0";

		for (int i = 0; i < 3; ++i) {
			if (dataSource.hasMoreData(dataSetKey)) {
				dataSource.getNextDataSet(dataSetKey);
			}
		}

		assertThat("no more data should be available", dataSource.hasMoreData(dataSetKey), is(false));
	}
}
