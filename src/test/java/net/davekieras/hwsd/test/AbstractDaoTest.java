package net.davekieras.hwsd.test;
/*******************************************************************************
 * Copyright (c) 2018 David Kieras.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD 3-clause license
 * which accompanies this distribution.
 *******************************************************************************/

import java.io.InputStream;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.DefaultOperationListener;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

public class AbstractDaoTest extends AbstractTransactionalJUnit4SpringContextTests {

	private Logger LOG = LoggerFactory.getLogger(AbstractDaoTest.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private IDataTypeFactory datatypeFactory;

	private IDatabaseTester databaseTester;

	private String dataSetFileName = "dataset.xml";

	/**
	 * Override to use a different dataset than the default. Set before the
	 * setUp() method is executed.
	 * 
	 * @param dataSetFileName
	 */
	protected void setDataSetFileName(String dataSetFileName) {
		this.dataSetFileName = dataSetFileName;
	}

	protected String getDataSetFileName() {
		return dataSetFileName;
	}

	@Before
	public void setUp() throws Exception {
		try {
			databaseTester = new DataSourceDatabaseTester(getDataSource()) {
				@Override
				public IDatabaseConnection getConnection() throws Exception {
					IDatabaseConnection dbC = super.getConnection();
					dbC.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, getDatatypeFactory());
					dbC.getConfig().setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, true);
					dbC.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);
					return dbC;
				}
			};
			InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(getDataSetFileName());
			IDataSet dataSet = new FlatXmlDataSetBuilder().build(inputStream);
			databaseTester.setDataSet(dataSet);
			databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
			databaseTester.setTearDownOperation(DatabaseOperation.NONE);
			databaseTester.setOperationListener(new DefaultOperationListener() {
				public void commit(IDatabaseConnection connection) {
					try {
						if (!connection.getConnection().getAutoCommit()) {
						    LOG.debug("committing(connection={}) - committing transaction", connection);
							connection.getConnection().commit();
						}
					} catch (SQLException e) {
						LOG.error("committing(connection={}) - failed to commit transaction; attempting rollback", e);
						try {
							connection.getConnection().rollback();
						} catch (SQLException ee) {
							LOG.error("committing(connection={}) - failed to rollback transaction", ee);
						}
					}
				}
				
				@Override
				//override this as a simple way to deal with autocommit=false issues
				public void operationTearDownFinished(IDatabaseConnection connection) {
					commit(connection);
					super.operationTearDownFinished(connection);
				}

				@Override
				//override this as a simple way to deal with autocommit=false issues
				public void operationSetUpFinished(IDatabaseConnection connection) {
					commit(connection);
					super.operationSetUpFinished(connection);
				}
			});
			databaseTester.onSetup();
		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
		}
	}

	@After
	public void tearDown() throws Exception {
		if (databaseTester != null) {
			databaseTester.onTearDown();
		}
	}

	protected EntityManager getEntityManager() {
		return entityManager;
	}

	protected DataSource getDataSource() {
		return dataSource;
	}

	protected IDataTypeFactory getDatatypeFactory() {
		return datatypeFactory;
	}

}
