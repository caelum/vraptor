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
}
