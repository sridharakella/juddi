package org.apache.juddi.model;
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
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 */
@Entity
@Table(name = "phone")
public class Phone implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private PhoneId id;
	private Contact contact;
	private String useType;
	private String phoneNumber;

	public Phone() {
	}

	public Phone(PhoneId id, Contact contact, String phoneNumber) {
		this.id = id;
		this.contact = contact;
		this.phoneNumber = phoneNumber;
	}
	public Phone(PhoneId id, Contact contact, String useType, String phoneNumber) {
		this.id = id;
		this.contact = contact;
		this.useType = useType;
		this.phoneNumber = phoneNumber;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "businessKey", column = @Column(name = "business_key", nullable = false, length = 255)),
			@AttributeOverride(name = "contactId", column = @Column(name = "contact_id", nullable = false)),
			@AttributeOverride(name = "phoneId", column = @Column(name = "phone_id", nullable = false))})

	public PhoneId getId() {
		return this.id;
	}

	public void setId(PhoneId id) {
		this.id = id;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "business_key", referencedColumnName = "business_key", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "contact_id", referencedColumnName = "contact_id", nullable = false, insertable = false, updatable = false)})

	public Contact getContact() {
		return this.contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	@Column(name = "use_type")
	public String getUseType() {
		return this.useType;
	}

	public void setUseType(String useType) {
		this.useType = useType;
	}

	@Column(name = "phone_number", nullable = false, length = 50)
	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

}