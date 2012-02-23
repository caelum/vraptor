package br.com.caelum.vraptor.serialization.xstream;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

public class Serializee {
	private Object root;
	private Class<?> rootClass;
	private List<String> includes = Lists.newArrayList();
	private List<String> excludes = Lists.newArrayList();
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
	public List<String> getIncludes() {
		return includes;
	}
	public List<String> getExcludes() {
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
	public void excludeAll(List<String> names) {
		checkPresenceOf(names);
		excludes.addAll(names);
	}
	public void includeAll(List<String> names) {
		checkPresenceOf(names);
		includes.addAll(names);
	}

	private void checkPresenceOf(List<String> names) {
		
	}
}