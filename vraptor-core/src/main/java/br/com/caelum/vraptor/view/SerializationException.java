package br.com.caelum.vraptor.view;

/**
 * Representation of problems during serialization
 * @author guilherme silveira
 * @since 3.0.2
 */
public class SerializationException extends RuntimeException {

	public SerializationException(String string, Throwable e) {
		super(string,e);
	}

}
