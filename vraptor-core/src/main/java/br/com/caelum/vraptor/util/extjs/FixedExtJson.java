package br.com.caelum.vraptor.util.extjs;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.serialization.ProxyInitializer;
import br.com.caelum.vraptor.serialization.xstream.XStreamSerializer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;

public class FixedExtJson implements ExtJSJson {

	private static class ExtJSWrapper {

		private Object data;
		private Boolean success;
		private Integer total;
		private Object selected;

		public ExtJSWrapper(Object object) {
			this.data = object;
		}

	}
	private XStreamSerializer serializer;
	private XStream xstream;
	private ExtJSWrapper wrapper;

	public FixedExtJson(HttpServletResponse response, TypeNameExtractor extractor, ProxyInitializer initializer) throws IOException {
		xstream = new XStream(new JsonHierarchicalStreamDriver());
		serializer = new XStreamSerializer(xstream, response.getWriter(), extractor, initializer);
	}


	public ExtJSJson exclude(String... names) {
		for (int i = 0; i < names.length; i++) {
			names[i] = "data." + names[i];
		}
		serializer.exclude(names);
		return this;
	}

	public ExtJSJson from(Object object) {
		wrapper = new ExtJSWrapper(object);
		serializer.from(wrapper);
		return this;
	}

	public ExtJSJson include(String... fields) {
		for (int i = 0; i < fields.length; i++) {
			fields[i] = "data." + fields[i];
		}
		serializer.exclude(fields);
		return this;
	}

	public ExtJSJson selected(Object value) {
		wrapper.selected = value;
		return this;
	}

	public ExtJSJson serialize() {
		serializer.serialize();
		return this;
	}

	public ExtJSJson success() {
		wrapper.success = true;
		return this;
	}

	public ExtJSJson success(boolean success) {
		wrapper.success = success;
		return this;
	}

	public ExtJSJson total(Integer total) {
		wrapper.total = total;
		return this;
	}

}
