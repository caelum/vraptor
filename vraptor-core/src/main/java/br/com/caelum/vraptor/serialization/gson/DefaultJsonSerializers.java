package br.com.caelum.vraptor.serialization.gson;

import java.util.Collections;
import java.util.List;

import br.com.caelum.vraptor.ioc.Component;

import com.google.common.collect.Lists;
import com.google.gson.JsonSerializer;

@Component
@SuppressWarnings("rawtypes")
public class DefaultJsonSerializers implements JsonSerializers {

	private List<JsonSerializer> serializers;

	public DefaultJsonSerializers(List<JsonSerializer> serializers) {
		this.serializers = Lists.newArrayList(serializers);
		sortSerializers();
	}

	public List<JsonSerializer> getSerializers() {
		return serializers;
	}
	
	/**
     * Override this method if you want another ordering strategy.
     */
	protected void sortSerializers() {
        Collections.sort(this.serializers, new PackageComparator());
    }
}
