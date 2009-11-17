/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.caelum.vraptor.mydvds.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.SessionScoped;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

/**
 * User entity.
 *
 * This class represents the User table from the database.
 *
 * A persisted object of this class represents a record in the database.
 *
 * The class is annotated with <code>@Component</code> and <code>@SessionScoped</code>,
 * thus its instances can be injected to other classes who depend on Users.
 */
@Entity
@Component
@SessionScoped
public class User {

	// Hibernate validator's annnotations/rules
	@Id
	@NotNull
	@Length(min = 3, max = 20)
	private String login;

	// Hibernate validator's annnotations/rules
	@NotNull
	@Length(min = 6, max = 20)
	private String password;

	// Hibernate validator's annnotations/rules
	@NotNull
	@Length(min = 3, max = 100)
	private String name;

	// user to dvd mapping,
	@OneToMany(mappedBy="owner")
	private Set<DvdRental> rents;

	public Set<DvdRental> getRents() {
		if (rents == null) {
			rents = new HashSet<DvdRental>();
		}
		return rents;
	}

	public void setRents(Set<DvdRental> dvds) {
		this.rents = dvds;
	}


	public Set<Dvd> getDvds() {
		return new HashSet<Dvd>(Collections2.transform(getRents(), new Function<DvdRental, Dvd>() {
			public Dvd apply(DvdRental copy) {
				return copy.getDvd();
			}
		}));
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
