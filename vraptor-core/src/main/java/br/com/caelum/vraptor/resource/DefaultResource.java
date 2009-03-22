package br.com.caelum.vraptor.resource;

public class DefaultResource implements Resource {

	private final Class<?> type;

	public DefaultResource(Class<?> type) {
		this.type = type;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof DefaultResource)) {
			return false;
		}
		DefaultResource resource = (DefaultResource) obj;
		return this.type.equals(resource.type);
	}

	public String toString() {
		return "{DefaultResource " + type.getName() + "}";
	}

	public Class<?> getType() {
		return type;
	}

}
