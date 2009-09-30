
package br.com.caelum.vraptor.converter;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * VRaptor's Boolean converter.
 *
 * Supports boolean-like values. Converts to TRUE the following strings : true,
 * 1, yes, y, on Converts to FALSE the following strings: false, 0, no, n, off
 *
 * @author Guilherme Silveira
 */
@Convert(Boolean.class)
@ApplicationScoped
public class BooleanConverter implements Converter<Boolean> {
	private static final Set<String> IS_TRUE  = new HashSet<String>(Arrays.asList("TRUE", "1", "YES", "Y", "ON"));
	private static final Set<String> IS_FALSE = new HashSet<String>(Arrays.asList("FALSE", "0", "NO", "N", "OFF"));

	public Boolean convert(String value, Class<? extends Boolean> type, ResourceBundle bundle) {
		if (value == null) {
			return null;
		}
		value = value.toUpperCase();
		if (matches(IS_TRUE, value)) {
			return true;
		} else if (matches(IS_FALSE, value)) {
			return false;
		}
		throw new ConversionError(MessageFormat.format(bundle.getString("is_not_a_valid_boolean"), value));
	}

	private boolean matches(Set<String> words, String value) {
		return words.contains(value);
	}
}
