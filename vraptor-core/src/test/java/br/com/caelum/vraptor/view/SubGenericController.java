package br.com.caelum.vraptor.view;

/**
 * Controller used to test Generic Controllers on LinkToHandler
 * @author Nykolas Lima
 *
 */
public class SubGenericController extends GenericController<String> {
	public void method(String string) {
		System.out.println("Do something thing by Sub Generic Controller");
	}
	
	public void okMethod(String string) {
		System.out.println("OKOK");
	}
	
	public void anotherMethod(String string, String param) {
		System.out.println("Do another thing by Sub Generic Controller");
	}
}
