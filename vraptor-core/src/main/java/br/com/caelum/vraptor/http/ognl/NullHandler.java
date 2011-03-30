package br.com.caelum.vraptor.http.ognl;

interface NullHandler {

	<T> T instantiate(Class<T> baseType);

}