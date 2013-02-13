/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.benerator;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import org.databene.benerator.engine.DescriptorRunner;
import org.databene.benerator.engine.PagedCreateEntityTask;
import org.databene.commons.BeanUtil;
import org.databene.commons.CollectionUtil;
import org.databene.model.consumer.AbstractConsumer;
import org.databene.model.consumer.ConsumerChain;
import org.databene.model.data.ComponentDescriptor;
import org.databene.model.data.Entity;
import org.w3c.dom.Element;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.mgmtp.jfunk.common.config.ScriptScoped;
import com.mgmtp.jfunk.common.exception.JFunkException;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.data.DataSet;
import com.mgmtp.jfunk.data.DefaultDataSet;
import com.mgmtp.jfunk.data.source.BaseDataSource;
import com.mgmtp.jfunk.data.source.DataSource;

/**
 * {@link DataSource} implementation for Benerator.
 * 
 * @version $Id$
 */
@ScriptScoped
public class BeneratorDataSource extends BaseDataSource {

	@Inject
	public BeneratorDataSource(final Configuration configuration) {
		super(configuration);
	}

	@Override
	protected DataSet getNextDataSetImpl(final String key) {
		try {
			List<DataSetConsumer> consumersList = runBenerator();
			for (DataSetConsumer consumer : consumersList) {
				if (key.equals(consumer.getName())) {
					return consumer.getDataSet();
				}
			}
			return null;
		} catch (IOException ex) {
			throw new JFunkException("Error running Benerator", ex);
		}
	}

	private List<DataSetConsumer> runBenerator() throws IOException {
		String configFileName = configuration.get("dataSource." + getName() + ".file.name");
		BeneratorRunner benerator = new BeneratorRunner(configFileName);
		benerator.run();
		return benerator.getConsumersList();
	}

	/**
	 * @return Always {@code true} because the data source is always able to generate new data.
	 */
	@Override
	public boolean hasMoreData(final String dataSetKey) {
		return true;
	}

	@Override
	protected void doReset() {
		// nothing to be done here
	}

	private static class BeneratorRunner extends DescriptorRunner {

		public BeneratorRunner(final String uri) {
			super(uri);
		}

		private final List<DataSetConsumer> consumersList = Lists.newArrayList();

		@Override
		public PagedCreateEntityTask parseCreateEntities(final Element element, final boolean isSubTask) {
			try {
				// We need to make internals accessible in order to get a hold of our consumer.

				PagedCreateEntityTask task = super.parseCreateEntities(element, isSubTask);

				Field field = task.getClass().getDeclaredField("consumer");
				field.setAccessible(true);

				ConsumerChain<?> consumers = (ConsumerChain<?>) field.get(task);

				field = consumers.getClass().getDeclaredField("components");
				field.setAccessible(true);

				DataSetConsumer consumer = (DataSetConsumer) ((List<?>) field.get(consumers)).get(0);
				consumersList.add(consumer);

				return task;
			} catch (Exception ex) {
				throw new JFunkException(ex);
			}
		}

		public List<DataSetConsumer> getConsumersList() {
			return consumersList;
		}
	}

	/**
	 * Consumer implementation which stores created values in a {@link DataSet}.
	 */
	public static class DataSetConsumer extends AbstractConsumer<Entity> {

		private String name;
		private DataSet dataSet;

		/**
		 * Called by Benerator. After this method has been called, the properties {@code name} and
		 * {@code dataSet} are available.
		 */
		@Override
		public void startConsuming(final Entity entity) {
			Collection<ComponentDescriptor> componentDescriptors = entity.getDescriptor().getComponents();
			List<String> componentNames = BeanUtil.extractProperties(componentDescriptors, "name");
			String[] propertyNames = CollectionUtil.toArray(componentNames, String.class);

			name = entity.getName();
			dataSet = new DefaultDataSet();
			for (String property : propertyNames) {
				String value = entity.getComponent(property).toString();
				dataSet.setValue(property, value);
			}
		}

		public String getName() {
			return name;
		}

		public DataSet getDataSet() {
			return dataSet;
		}
	}
}
