package br.com.caelum.vraptor;


/**
 * Converts a string value to an object.
 * 
 * @author Guilherme Silveira
 */
public interface Converter<T> {

    T convert(String value);

}
