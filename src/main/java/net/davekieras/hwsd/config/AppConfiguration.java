/*******************************************************************************
 * Copyright (c) 2018 David Kieras.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD 3-clause license
 * which accompanies this distribution.
 *******************************************************************************/
package net.davekieras.hwsd.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@Profile({"prod", "qa"})
@EnableTransactionManagement
@ComponentScan("net.davekieras.hwsd")
@PropertySource(value = { "classpath:application.properties" }, ignoreResourceNotFound = true)
public class AppConfiguration {

	@Autowired
	private Environment env;
	
	@Value("${datasource.url}")
	private String datasourceUrl;

	@Value("${datasource.user}")
	private String datasourceUser;

	@Value("${datasource.password}")
	private String datasourcePassword;
	
	@Value("${datasource.className}")
	private String datasourceClassName;

	/**
	 * Read why this is required:
	 * http://www.baeldung.com/2012/02/06/properties-with-spring/#java It is
	 * important to be static:
	 * http://www.java-allandsundry.com/2013/07/spring-bean-and-propertyplaceholderconf.html
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);
		return transactionManager;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setDataSource(dataSource());
		emf.setPackagesToScan(new String[] { "net.davekieras.hwsd.persistence.entity" });
		emf.setJpaVendorAdapter(jpaVendorAdapter());
		emf.setJpaDialect(jpaDialect());
		emf.setJpaProperties(hibernateProperties());
		return emf;
	}

	/**
	 * DataSource for HikariCP connection pool
	 */
	@Bean
	public DataSource dataSource() {

		Properties props = new Properties();
		props.setProperty("dataSource.url", datasourceUrl);
		props.setProperty("dataSource.user", datasourceUser);
		props.setProperty("dataSource.password", datasourcePassword);
		props.setProperty("dataSourceClassName", datasourceClassName);
		props.putAll(hikariProperties());
		HikariConfig config = new HikariConfig(props);
		HikariDataSource ds = new HikariDataSource(config);
		return ds;
	}
	
	/**
	 * unpooled dataSource
	 * 
	 * @return
	 */
	/*
	@Bean
	public DataSource dataSource() {
		JdbcDataSource dataSource = new JdbcDataSource();
		dataSource.setUrl(datasourceUrl);
		dataSource.setUser(datasourceUser);
		dataSource.setPassword(datasourcePassword);
		return dataSource;
	}
	*/
	
	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		return new HibernateJpaVendorAdapter();
	}

	@Bean
	public JpaDialect jpaDialect() {
		return new HibernateJpaDialect();
	}
	
	private Properties hibernateProperties() {
		/*
		 * uses the EnumerablePropertySources of the Environment to iterate
		 * through the known property names, but then reads the actual value out
		 * of the real Spring environment. This guarantees that the value is the
		 * one actually resolved by Spring, including any overriding values.
		 * 
		 * https://stackoverflow.com/questions/23506471/spring-access-all-environment-properties-as-a-map-or-properties-object
		 */
        MutablePropertySources propertySources = ((AbstractEnvironment) env).getPropertySources();
        Properties props = new Properties();
        for (org.springframework.core.env.PropertySource<?> ps : propertySources) {
        	if (ps instanceof EnumerablePropertySource<?>) {
        		for (String propName : ((EnumerablePropertySource<?>) ps).getPropertyNames()) {
        			if (propName.startsWith("hibernate.")) {
        				props.setProperty(propName, env.getProperty(propName));
        			}
        		}
        	}
        	
        }
        /* using lambdas
        StreamSupport.stream(propertySources.spliterator(), false)
        	.filter(ps -> ps instanceof EnumerablePropertySource)
        	.map(ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames())
        	.flatMap(Arrays::<String>stream)
        	.forEach(propName -> {
        		if (propName.startsWith("hibernate.")) {
        			props.setProperty(propName, env.getProperty(propName));
        		}
        	});
        */
		return props;
	}
	
	private Properties hikariProperties() {
		MutablePropertySources propertySources = ((AbstractEnvironment) env).getPropertySources();
        Properties props = new Properties();
        for (org.springframework.core.env.PropertySource<?> ps : propertySources) {
        	if (ps instanceof EnumerablePropertySource<?>) {
        		for (String propName : ((EnumerablePropertySource<?>) ps).getPropertyNames()) {
        			if (propName.startsWith("hikari.")) {
        				String newPropName = propName.split("\\.")[1];
        				props.setProperty(newPropName, env.getProperty(propName));
        			}
        		}
        	}
        }
        return props;
	}
	
}
