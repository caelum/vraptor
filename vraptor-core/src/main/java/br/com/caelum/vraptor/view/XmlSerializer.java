package br.com.caelum.vraptor.view;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.rest.Restfulie;
import br.com.caelum.vraptor.rest.StateResource;
import br.com.caelum.vraptor.rest.Transition;

/**
 * Basic xml serialization system.
 * @author guilherme silveira
 * @since 3.0.2
 */
@Component
public class XmlSerializer {
	
	private final Writer writer;
	private Object analyzing;
	private final List<String> excludes = new ArrayList<String>();
	private final Map<String, XmlSerializer> includes = new HashMap<String, XmlSerializer>();
	private final XmlSerializer parent;
	private final List<String> methods = new ArrayList<String>();
	
	private final XmlConfiguration configuration = new DefaultXmlConfiguration();
	private String prefixTag = null;
	private String namespaceUri, namespacePrefix;
	private final Restfulie restfulie;

	public XmlSerializer(OutputStream output) {
		this(new OutputStreamWriter(output));
	}
	
	public XmlSerializer(XmlSerializer parent, Writer writer, Restfulie restfulie) {
		this.parent = parent;
		this.writer = writer;
		this.restfulie = restfulie;
	}

	public XmlSerializer(Writer writer) {
		this(null, writer, null);
	}

	public <T> XmlSerializer from(T object) {
		this.analyzing = object;
		return this;
	}
	
	private boolean isBasicType(Class type) {
		return type.isPrimitive() || type.equals(String.class) || Enum.class.isAssignableFrom(type);
	}
	
	private void parseFields(Object object, Class type) throws IOException {
		if(type.equals(Object.class)) {
			return;
		}
		Field[] fields = type.getDeclaredFields();
		try {
			for (Field field : fields) {
				field.setAccessible(true);
				XmlSerializer serializer = includes.get(field.getName());
				if(serializer!=null) {
					serializer.from(field.get(object)).serializeForReal();
					continue;
				}
				boolean shouldExclude = excludes.contains(field.getName());
				if(!shouldExclude && isBasicType(field.getType())) {
						Object result = field.get(object);
						writer.write("  " + startTag(field.getName()) + result + endTag(field.getName()) + "\n");
				}
			}
		} catch (IllegalArgumentException e) {
			throw new SerializationException("Unable to serialize " + object, e);
		} catch (IllegalAccessException e) {
			throw new SerializationException("Unable to serialize " + object, e);
		}
		parseFields(object, type.getSuperclass());
	}

	private String endTag(String name) {
		if(namespacePrefix!=null) {
			return "</" +  namespacePrefix + ":" + name + ">";
		} else {
			return "</" + name + ">";
		}
	}

	private String startTag(String name) {
		if(namespacePrefix!=null) {
			return "<" +  namespacePrefix + ":" + name + ">";
		} else {
			return "<" + name + ">";
		}
	}

	public XmlSerializer exclude(String... names) {
		for(String fieldName : names) {
			this.excludes .add(fieldName);
		}
		return this;
	}

	public void serialize() {
		if(this.parent!=null) {
			parent.serialize();
			return;
		}
		try {
			serializeForReal();
		} catch (IOException e) {
			throw new SerializationException("Unable to serialize " + analyzing, e);
		}
	}

	private void serializeForReal() throws IOException {
		try {
			Class<? extends Object> baseType = analyzing.getClass();
			if(Collection.class.isAssignableFrom(baseType)) {
				Collection items = (Collection) analyzing;
				if(prefixTag!=null) {
					if(namespaceUri !=null) {
						writer.write("<" +  namespacePrefix + ":" + prefixTag + " " + "xmlns:" + namespacePrefix + "=\"" + namespaceUri +  "\">");
					} else {
						writer.write(startTag(prefixTag));
					}
				}
				for(Object object : items) {
					serializeSingleObjectForReal(object, object.getClass(), false);
				}
				if(prefixTag!=null) {
					writer.write(endTag(prefixTag));
				}
				return;
			} else {
				serializeSingleObjectForReal(analyzing, baseType, true);
			}
		} finally {
			writer.flush();
		}
	}

	private void serializeSingleObjectForReal(Object object, Class<? extends Object> baseType, boolean addNamespaceDeclaration) {
		String name = configuration.nameFor(baseType.getSimpleName());
		try {
			if(addNamespaceDeclaration && namespacePrefix!=null) {
			 	writer.write("<" +  namespacePrefix + ":" + name + " " + "xmlns:" + namespacePrefix + "=\"" + namespaceUri +  "\">\n");
			}else {
				writer.write(startTag(name) + "\n");
			}
			parseFields(object, baseType);
			parseTransitions(object, baseType);
			parseMethods(object, baseType);
			writer.write(endTag(name));
		} catch (Exception e) {
			throw new SerializationException("Unable to serialize " + analyzing, e);
		}
	}

	private void parseTransitions(Object object,
			Class<? extends Object> baseType) throws IOException {
		if(!StateResource.class.isAssignableFrom(baseType)) {
			return;
		}
		StateResource resource = (StateResource) object;
		List<Transition> transitions = resource.getFollowingTransitions(restfulie);
		for(Transition transition:transitions) {
			writer.write("  <atom:link href=\"" + transition.getUri() + "\" rel=\"" + transition.getName() + "\" xmlns:atom=\"http://www.w3.org/2005/Atom\" />");
		}
	}

	private void parseMethods(Object object, Class<? extends Object> baseType)
			throws IOException, IllegalAccessException,
			InvocationTargetException {
		for(String methodName : this.methods) {
			Method method = findMethod(baseType, methodName);
			if(method==null) {
				throw new SerializationException("Unable to find method " + methodName + " while inspecting " + analyzing);
			}
			writer.write(startTag(methodName) + method.invoke(object) + endTag(methodName));
		}
	}

	private Method findMethod(Class<? extends Object> type,
			String name) {
		// guilherme: careful, we want to later invoke this method with parameters to inject possible
		// atom/hypermedia aware xml constructors
		for(Method m : type.getDeclaredMethods()) {
			if(m.getName().equals(name)) {
				return m;
			}
		}
		return null;
	}

	public XmlSerializer include(String fieldName) {
		XmlSerializer serializer = new XmlSerializer(this, writer, restfulie);
		this.includes.put(fieldName, serializer);
		return serializer;
	}

	public XmlSerializer addMethod(String methodName) {
		this.methods.add(methodName);
		return this;
	}

	public XmlSerializer from(String prefix, Collection collection) {
		this.prefixTag= prefix;
		this.analyzing = collection;
		return this;
	}

	public XmlSerializer namespace(String uri, String prefix) {
		this.namespaceUri = uri;
		this.namespacePrefix = prefix;
		return this;
	}

}
