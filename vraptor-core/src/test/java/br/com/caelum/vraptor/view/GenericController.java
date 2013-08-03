package br.com.caelum.vraptor.view;

/**
 * Controller used to test Generic Controllers on LinkToHandler
 * @author Nykolas Lima
 *
 */
public class GenericController<T> {
	public void method(T entity) {
		System.out.println("Do Something");
	}
	
	public void anotherMethod(T entity, String param) {
		System.out.println("Do Another Thing");
	}
	
	public void methodWithoutGenericType(String param) {
		System.out.println("Without generic");
	}
}
