package br.com.caelum.vraptor.mydvds.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.SessionScoped;

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

	// primary key
	@Id
	@GeneratedValue
	private Long id;

	// Hibernate validator's annnotations/rules
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
	// strong side
	@ManyToMany
	private Set<Dvd> dvds;

	public Set<Dvd> getDvds() {
		if (dvds == null) {
			dvds = new HashSet<Dvd>();
		}
		return dvds;
	}

	public void setDvds(Set<Dvd> dvds) {
		this.dvds = dvds;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
