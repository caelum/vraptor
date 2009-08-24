package br.com.caelum.vraptor.util.migration;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Migration info helper for hibernate's implementation of Migration support.
 *
 * @author guilherme silveira
 */
@Entity
public class MigrationInfo {

	@Id
	@Column(length = 255)
	private String id;

	public MigrationInfo(String id) {
		this.id = id;
	}

	public MigrationInfo() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
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
		MigrationInfo other = (MigrationInfo) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}


}
