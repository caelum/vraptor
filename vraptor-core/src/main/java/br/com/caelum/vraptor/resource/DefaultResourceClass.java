
package br.com.caelum.vraptor.resource;

public class DefaultResourceClass implements ResourceClass {

	private final Class<?> type;

	public DefaultResourceClass(Class<?> type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DefaultResourceClass)) {
			return false;
		}
		DefaultResourceClass resource = (DefaultResourceClass) obj;
		return this.type.equals(resource.type);
	}

	@Override
	public int hashCode() {
		return type == null ? 0 : type.hashCode();
	}

	public String toString() {
		return "{ResourceClass " + type.getName() + "}";
	}

	public Class<?> getType() {
		return type;
	}

}
