package net.davekieras.hwsd.test;
/*******************************************************************************
 * Copyright (c) 2018 David Kieras.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD 3-clause license
 * which accompanies this distribution.
 *******************************************************************************/


import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import net.davekieras.hwsd.persistence.entity.impl.Dog;
import net.davekieras.hwsd.persistence.facade.impl.DogFacade;
import net.davekieras.hwsd.test.config.TestConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
public class TestJunit extends AbstractDaoTest {

	private static final Logger LOG = LoggerFactory.getLogger(TestJunit.class);
	
	@Autowired
	private DogFacade facade;
	
	@Test
	public void testLogger() throws Throwable {
		Dog dog = facade.retrieveById(1L);
		LOG.info("DOG IS " + dog);
	}
	
}
