package br.com.caelum.vraptor.deserialization.gson;

import java.util.Collections;
import java.util.List;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.serialization.gson.PackageComparator;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializer;

@Component
@SuppressWarnings("rawtypes")
public class DefaultJsonDeserializers implements JsonDeserializers {

	private List<JsonDeserializer> deserializers;
	
	public DefaultJsonDeserializers(List<JsonDeserializer> deserializers) {
		this.deserializers = Lists.newArrayList(deserializers);
		
		sortDeserializers();
	}
	
	public List<JsonDeserializer> getDeserializers() {
		return deserializers;
	}
	
	/**
     * Override this method if you want another ordering strategy.
     */
	protected void sortDeserializers() {
        Collections.sort(this.deserializers, new PackageComparator());
    }
}
