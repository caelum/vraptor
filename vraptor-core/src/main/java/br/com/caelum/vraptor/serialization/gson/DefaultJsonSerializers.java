package br.com.caelum.vraptor.serialization.gson;

import java.util.List;

import javax.servlet.ServletContext;

import br.com.caelum.vraptor.config.BasicConfiguration;
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
	public DefaultJsonSerializers(List<JsonSerializer> serializers, ServletContext servletContext) {
		this.serializers = Lists.newArrayList(serializers);
		if (isHibernateProxyPresent) 
			this.serializers.add(new HibernateProxySerializer());
		
		String packagesParam = servletContext.getInitParameter(BasicConfiguration.BASE_PACKAGES_PARAMETER_NAME);
		if ((packagesParam != null) && (packagesParam.contains("br.com.caelum.vraptor.serialization.gson.adapters.iso8601"))) {
			this.serializers.add(new br.com.caelum.vraptor.serialization.gson.adapters.iso8601.CalendarSerializer());
			this.serializers.add(new br.com.caelum.vraptor.serialization.gson.adapters.iso8601.DateSerializer());
		}
	}

	public List<JsonSerializer> getSerializers() {
		return serializers;
	}
}
