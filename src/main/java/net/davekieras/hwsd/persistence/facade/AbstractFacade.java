/*******************************************************************************
 * Copyright (c) 2018 David Kieras.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD 3-clause license
 * which accompanies this distribution.
 *******************************************************************************/
package net.davekieras.hwsd.persistence.facade;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import net.davekieras.hwsd.persistence.entity.AbstractEntity;

//Note here that we do not use an interface since Spring expects an instance of the interface when wiring if there is one
public abstract class AbstractFacade<T extends AbstractEntity<T>> {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractFacade.class);
	
	@PersistenceContext
	private EntityManager entityManager;

	public abstract Class<T> getType();

	protected EntityManager getEntityManager() {
		return entityManager;
	}

	@Transactional(readOnly = true)
	public T retrieveById(final Long id) throws Throwable {
		return (T) entityManager.find(getType(), id);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void save(final T entity) throws Throwable {
		entityManager.persist(entity);
	}

	@Transactional(rollbackFor = Throwable.class)
	public void delete(T entity) throws Throwable {
		entityManager.remove(entity);
	}

}
