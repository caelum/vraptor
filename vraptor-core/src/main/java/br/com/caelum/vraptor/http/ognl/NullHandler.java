package br.com.caelum.vraptor.http.ognl;

public interface NullHandler {

	<T> T instantiate(Class<T> baseType);

}