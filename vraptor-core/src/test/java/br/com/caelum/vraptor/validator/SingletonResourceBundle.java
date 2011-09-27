package br.com.caelum.vraptor.validator;

import java.util.Collections;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class SingletonResourceBundle extends ResourceBundle {
	private final String value;
	private final String key;

	public SingletonResourceBundle(String key, String value) {
		this.value = value;
		this.key = key;
	}

	@Override
	protected Object handleGetObject(String k) {
		if (k.equals(key)) {
			return value;
		}
		throw new MissingResourceException(k, value, key);
	}

	@Override
	public Enumeration<String> getKeys() {
		return Collections.enumeration(Collections.singleton(key));
	}
}