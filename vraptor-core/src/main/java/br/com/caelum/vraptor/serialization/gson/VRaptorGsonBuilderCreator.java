package br.com.caelum.vraptor.serialization.gson;

import java.util.Arrays;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.serialization.xstream.Serializee;

import com.google.gson.ExclusionStrategy;

@Component
public class VRaptorGsonBuilderCreator implements ComponentFactory<VraptorGsonBuilder> {

	private final JsonSerializers serializers;
	private final Serializee serializee;

	public VRaptorGsonBuilderCreator(JsonSerializers serializers, Serializee serializee) {
		this.serializers = serializers;
		this.serializee = serializee;
	}

	public VraptorGsonBuilder getInstance() {
		ExclusionStrategy exclusion = new Exclusions(serializee);

		return new VraptorGsonBuilder(serializers.getSerializers(), Arrays.asList(exclusion));
	}

}
