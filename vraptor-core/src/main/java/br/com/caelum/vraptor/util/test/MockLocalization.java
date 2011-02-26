package br.com.caelum.vraptor.util.test;

import java.util.Locale;
import java.util.ResourceBundle;

import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.core.SafeResourceBundle;
import br.com.caelum.vraptor.util.EmptyBundle;

/**
 * A localization that returns safe empty bundles
 *
 * @author Lucas Cavalcanti
 * @since 3.3.0
 *
 */
public class MockLocalization implements Localization {

	public ResourceBundle getBundle() {
		return new SafeResourceBundle(new EmptyBundle());
	}

	public Locale getFallbackLocale() {
		return null;
	}

	public Locale getLocale() {
		return null;
	}

	public String getMessage(String key, Object... parameters) {
		return getBundle().getString(key);
	}

}
