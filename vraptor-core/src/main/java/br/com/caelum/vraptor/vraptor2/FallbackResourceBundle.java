package br.com.caelum.vraptor.vraptor2;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * A resource bundle which uses two different bundles to look for messages.
 * 
 * @author Guilherme Silveira
 */
public class FallbackResourceBundle extends ResourceBundle{

	private final ResourceBundle main;
	private final ResourceBundle fallback;

	public FallbackResourceBundle(ResourceBundle main, ResourceBundle fallback) {
		this.main = main;
		this.fallback = fallback;
	}

	public Enumeration<String> getKeys() {
		return new FallbackEnumeration(main.getKeys(), fallback.getKeys());
	}

	protected Object handleGetObject(String key) {
		try {
			return main.getString(key);
		} catch (MissingResourceException e) {
			try {
				return fallback.getString(key);
			} catch (MissingResourceException e1) {
				return "???" + key + "???";
			}
		}
	}

}
