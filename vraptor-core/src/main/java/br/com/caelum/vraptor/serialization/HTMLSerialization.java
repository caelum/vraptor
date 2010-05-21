package br.com.caelum.vraptor.serialization;

import static br.com.caelum.vraptor.view.Results.page;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.TypeNameExtractor;

public class HTMLSerialization implements Serialization {

	private final Result result;
	private final TypeNameExtractor extractor;

	public HTMLSerialization(Result result, TypeNameExtractor extractor) {
		this.result = result;
		this.extractor = extractor;
	}

	public boolean accepts(String format) {
		return "html".equals(format);
	}

	public <T> Serializer from(T object, String alias) {
		result.include(alias, object);
		result.use(page()).forward();
		return new IgnoringSerializer();
	}

	public <T> Serializer from(T object) {
		result.include(extractor.nameFor(object.getClass()), object);
		result.use(page()).forward();
		return new IgnoringSerializer();
	}

}
