package br.com.caelum.vraptor.converter;

import java.util.ResourceBundle;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;

/**
 * A no-op converter
 *
 * @author Lucas Cavalcanti
 * @since 3.2.1
 *
 */
@Convert(String.class)
public class StringConverter implements Converter<String> {

	public String convert(String value, Class<? extends String> type, ResourceBundle bundle) {
		return value;
	}
}
