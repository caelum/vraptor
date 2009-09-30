
package br.com.caelum.vraptor.core;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * A i18n messages provider.
 * 
 * @author Guilherme Silveira
 */
public interface Localization {

	Locale getLocale();

	Locale getFallbackLocale();

	/**
	 * Returns a formated message or '???key???' if the key was not found.
	 */
	String getMessage(String key, String... parameters);

	ResourceBundle getBundle();

}
