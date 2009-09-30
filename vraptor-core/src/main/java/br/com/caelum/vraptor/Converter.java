
package br.com.caelum.vraptor;

import java.util.ResourceBundle;



/**
 * Converts a string value to an object.
 * 
 * @author Guilherme Silveira
 */
public interface Converter<T> {

    T convert(String value, Class<? extends T> type, ResourceBundle bundle);

}
