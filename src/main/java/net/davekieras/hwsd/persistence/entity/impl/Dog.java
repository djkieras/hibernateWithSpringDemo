/*******************************************************************************
 * Copyright (c) 2018 David Kieras.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD 3-clause license
 * which accompanies this distribution.
 *******************************************************************************/
package net.davekieras.hwsd.persistence.entity.impl;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import net.davekieras.hwsd.persistence.entity.AbstractEntity;

@Entity
@Table(name = "DOG")
public class Dog extends AbstractEntity<Dog> {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "DOG_ID")
	private Long id;
	
	@Column(name = "UUID", length = 1024, insertable = true, unique = true)
	private String uuid = UUID.randomUUID().toString();

	@Column(name = "NAME", length = 256)
	private String name;
	
	@Column(name = "BREED", length = 256)
	private String breed;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this).toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(13, 31).append(this.uuid).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Dog rhs = (Dog) obj;
		return new EqualsBuilder().append(this.uuid, rhs.getUuid()).isEquals();
	}

	@Override
	public Long getId() {
		return this.id;
	}
	
	public String getUuid() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBreed() {
		return breed;
	}

	public void setBreed(String breed) {
		this.breed = breed;
	}

}
