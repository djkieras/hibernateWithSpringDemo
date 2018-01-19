/*******************************************************************************
 * Copyright (c) 2018 David Kieras.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD 3-clause license
 * which accompanies this distribution.
 *******************************************************************************/
package net.davekieras.hwsd.persistence.facade.impl;

import org.springframework.stereotype.Service;

import net.davekieras.hwsd.persistence.entity.impl.Dog;
import net.davekieras.hwsd.persistence.facade.AbstractFacade;

@Service
public class DogFacade extends AbstractFacade<Dog> {

	@Override
	public Class<Dog> getType() {
		return Dog.class;
	}

}
