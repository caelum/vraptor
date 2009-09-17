package br.com.caelum.vraptor.mydvds.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

/**
 * Dvd entity.
 *
 * Represents the table DVD from the database.
 *
 * A persisted object of this class represents a record in the database.
 */
@Entity
public class Dvd {

	/*
	 * Primary key.
	 */
	@Id
	@GeneratedValue
	private Long id;

	private String title;

	private String description;

	// dvd to user mapping
	@ManyToMany(mappedBy = "dvds")
	private Set<User> users;

	@Enumerated(EnumType.STRING)
	private DvdType type;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public DvdType getType() {
		return type;
	}

	public void setType(DvdType type) {
		this.type = type;
	}

	/**
	 * @return the users
	 */
	public Set<User> getUsers() {
		return users;
	}

	/**
	 * @param users
	 *            the users to set
	 */
	public void setUsers(Set<User> users) {
		this.users = users;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Dvd
		&& getId().equals(((Dvd) obj).getId());
	}

	@Override
	public int hashCode() {
		if (this.id == null) {
			return 0;
		}
		return this.id.hashCode() * 31;
	}

}
