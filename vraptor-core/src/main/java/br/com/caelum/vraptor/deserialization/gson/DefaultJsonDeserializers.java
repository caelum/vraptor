package br.com.caelum.vraptor.deserialization.gson;

import java.util.List;

import javax.servlet.ServletContext;

import br.com.caelum.vraptor.config.BasicConfiguration;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.util.ISO8601Util;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializer;

@SuppressWarnings("rawtypes")
@Component
public class DefaultJsonDeserializers implements JsonDeserializers {
	
	private List<JsonDeserializer> deserializers;
	
	public DefaultJsonDeserializers(List<JsonDeserializer> deserializers, ServletContext servletContext) {
		this.deserializers = Lists.newArrayList(deserializers);
	
		String packagesParam = servletContext.getInitParameter(BasicConfiguration.BASE_PACKAGES_PARAMETER_NAME);
		if ((packagesParam != null) && (packagesParam.contains("br.com.caelum.vraptor.serialization.gson.adapters.iso8601"))) {
			this.deserializers.add(new br.com.caelum.vraptor.serialization.gson.adapters.iso8601.CalendarDeserializer(new ISO8601Util()));
			this.deserializers.add(new br.com.caelum.vraptor.serialization.gson.adapters.iso8601.DateDeserializer(new ISO8601Util()));
		}
	}
	
	public List<JsonDeserializer> getDeserializers() {
		return deserializers;
	}
}
