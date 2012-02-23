package br.com.caelum.vraptor.serialization.xstream;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.vidageek.mirror.dsl.Mirror;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class Serializee {
	private Object root;
	private Class<?> rootClass;
	private Multimap<String, Class<?>> includes = LinkedListMultimap.create();
	private Multimap<String, Class<?>> excludes = LinkedListMultimap.create();
	private Set<Class<?>> elementTypes;
	private boolean recursive;

	public Object getRoot() {
		return root;
	}

	public void setRoot(Object root) {
		this.root = root;
	}

	public Class<?> getRootClass() {
		return rootClass;
	}

	public void setRootClass(Class<?> rootClass) {
		this.rootClass = rootClass;
	}

	public Multimap<String, Class<?>> getIncludes() {
		return includes;
	}

	public Multimap<String, Class<?>> getExcludes() {
		return excludes;
	}

	public Set<Class<?>> getElementTypes() {
		return elementTypes;
	}

	public void setElementTypes(Set<Class<?>> elementTypes) {
		this.elementTypes = elementTypes;
	}

	public boolean isRecursive() {
		return recursive;
	}

	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}

	public void excludeAll(String... names) {
		for (String name : names) {
			excludes.putAll(name, getParentTypesFor(name));
		}
	}

	public void includeAll(String... names) {
		for (String name : names) {
			includes.putAll(name, getParentTypesFor(name));
		}
	}

	private Set<Class<?>> getParentTypesFor(String name) {
		if (getElementTypes() == null) {
			Class<?> type = getRootClass();
			return getParentTypes(name, type);
		} else {
			Set<Class<?>> result = new HashSet<Class<?>>();
			for (Class<?> type : getElementTypes()) {
				result.addAll(getParentTypes(name, type));
			}
			return result;
		}
	}

	private Set<Class<?>> getParentTypes(String name, Class<?> type) {
		String[] path = name.split("\\.");
		
		try {
			for (int i = 0; i < path.length - 1; i++) {
				Field field = checkNotNull(new Mirror().on(type).reflect().field(path[i]));
				type = getActualType(field.getGenericType());
			}
			checkNotNull(new Mirror().on(type).reflect().field(path[path.length -1]));
		} catch (NullPointerException e) {
			throw new IllegalArgumentException("Field path '" + name + "' doesn't exists in " + type, e);
		}
		
		Set<Class<?>> types = Sets.newHashSet();
		while (type != Object.class) {
			types.add(type);
			type = type.getSuperclass();
		}
		return types;
	}

	static Class<?> getActualType(Type genericType) {
		if (genericType instanceof ParameterizedType) {
			ParameterizedType type = (ParameterizedType) genericType;

			if (isCollection(type)) {
				Type actualType = type.getActualTypeArguments()[0];

				if (actualType instanceof TypeVariable<?>) {
					return (Class<?>) type.getRawType();
				}

				return (Class<?>) actualType;
			}
		}

		return (Class<?>) genericType;
	}

	private static boolean isCollection(Type type) {
		if (type instanceof ParameterizedType) {
			ParameterizedType ptype = (ParameterizedType) type;
			return Collection.class.isAssignableFrom((Class<?>) ptype.getRawType())
					|| Map.class.isAssignableFrom((Class<?>) ptype.getRawType());
		}
		return Collection.class.isAssignableFrom((Class<?>) type);
	}

}