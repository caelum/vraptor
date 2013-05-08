package br.com.caelum.vraptor.serialization.gson;

import java.util.List;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.serialization.gson.adapters.HibernateProxySerializer;

import com.google.common.collect.Lists;
import com.google.gson.JsonSerializer;

@Component
public class DefaultJsonSerializers implements JsonSerializers {

	private static boolean isHibernateProxyPresent;
	static {
		try {
			Class.forName("org.hibernate.proxy.HibernateProxy");
			isHibernateProxyPresent = true;
		} catch (ClassNotFoundException e) {
			isHibernateProxyPresent = false;
		}
	}
	private List<JsonSerializer> serializers;

	@SuppressWarnings("rawtypes")
	public DefaultJsonSerializers(List<JsonSerializer> serializers) {
		this.serializers = Lists.newArrayList(serializers);
		if (isHibernateProxyPresent) {
			this.serializers.add(new HibernateProxySerializer());
		}
	}

	public List<JsonSerializer> getSerializers() {
		return serializers;
	}

}
