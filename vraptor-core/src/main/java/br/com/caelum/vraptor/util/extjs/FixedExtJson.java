package br.com.caelum.vraptor.util.extjs;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.serialization.ProxyInitializer;
import br.com.caelum.vraptor.serialization.xstream.XStreamSerializer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JsonWriter;

public class FixedExtJson implements ExtJSJson {

	private static class ExtJSWrapper {
		private Object data;
		private List<Object> list;
		private Boolean success;
		private Integer total;
		private Object selected;

		public ExtJSWrapper(Object object) {
			if (object instanceof Collection) {
				this.list = new ArrayList<Object>((Collection)object);
			} else {
				this.data = object;
			}
		}
	}

	private XStreamSerializer serializer;
	private XStream xstream;
	private ExtJSWrapper wrapper;

	public FixedExtJson(HttpServletResponse response, TypeNameExtractor extractor, ProxyInitializer initializer) throws IOException {
		xstream = new XStream(new JsonHierarchicalStreamDriver() {
			@Override
			public HierarchicalStreamWriter createWriter(Writer writer) {
				return new JsonWriter(writer, new char[0], "",  JsonWriter.DROP_ROOT_MODE) {
					@Override
					public void addAttribute(String key, String value) {
						if (!key.equals("class")) {
							super.addAttribute(key, value);
						}
					}
				};
            }
		});
		xstream.aliasField("data", ExtJSWrapper.class, "list");
		serializer = new XStreamSerializer(xstream, response.getWriter(), extractor, initializer);
	}

	public ExtJSJson from(Object object) {
		wrapper = new ExtJSWrapper(object);
		serializer.from(object);
		return this;
	}

	public ExtJSJson exclude(String... names) {
		serializer.exclude(names);
		return this;
	}

	public ExtJSJson include(String... fields) {
		serializer.include(fields);
		return this;
	}

	public ExtJSJson selected(Object value) {
		wrapper.selected = value;
		return this;
	}

	public ExtJSJson serialize() {
		serializer.from(wrapper).recursive().serialize();
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
