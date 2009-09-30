
package br.com.caelum.vraptor.converter;


import java.util.ResourceBundle;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * VRaptor's primitive boolean converter.
 *
 * @author Cecilia Fernandes
 */
@Convert(boolean.class)
@ApplicationScoped
public class PrimitiveBooleanConverter implements Converter<Boolean> {
	private final BooleanConverter booleanConverter = new BooleanConverter();

    public Boolean convert(String value, Class<? extends Boolean> type, ResourceBundle bundle) {
        if (value == null || "".equals(value)) {
        	return false;
        }
        return booleanConverter.convert(value, type, bundle);
    }

}
