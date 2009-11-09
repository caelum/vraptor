package br.com.caelum.vraptor.rest;

public interface Dependency<T> {
	
	boolean allows(T object);

}
