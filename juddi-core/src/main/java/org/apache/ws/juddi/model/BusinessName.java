package org.apache.ws.juddi.model;
/*
 * Copyright 2001-2008 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 */
@Entity
@Table(name = "business_name")
public class BusinessName implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private BusinessNameId id;
	private BusinessEntity businessEntity;
	private String langCode;
	private String name;

	public BusinessName() {
	}

	public BusinessName(BusinessNameId id, BusinessEntity businessEntity,
			String name) {
		this.id = id;
		this.businessEntity = businessEntity;
		this.name = name;
	}
	public BusinessName(BusinessNameId id, BusinessEntity businessEntity,
			String langCode, String name) {
		this.id = id;
		this.businessEntity = businessEntity;
		this.langCode = langCode;
		this.name = name;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "businessKey", column = @Column(name = "business_key", nullable = false, length = 41)),
			@AttributeOverride(name = "businessNameId", column = @Column(name = "business_name_id", nullable = false))})

	public BusinessNameId getId() {
		return this.id;
	}

	public void setId(BusinessNameId id) {
		this.id = id;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "business_key", nullable = false, insertable = false, updatable = false)

	public BusinessEntity getBusinessEntity() {
		return this.businessEntity;
	}

	public void setBusinessEntity(BusinessEntity businessEntity) {
		this.businessEntity = businessEntity;
	}

	@Column(name = "lang_code", length = 5)
	public String getLangCode() {
		return this.langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	@Column(name = "name", nullable = false)

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}