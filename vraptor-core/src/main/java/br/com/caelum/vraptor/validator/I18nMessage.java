package br.com.caelum.vraptor.validator;

import java.text.MessageFormat;
import java.util.ResourceBundle;


/**
 * In this Message implementation, the message is i18n'ed while the category is literal.
 *
 * The i18n is lazy.
 *
 * @author Lucas Cavalcanti
 * @since 3.1.3
 */
public class I18nMessage implements Message {

	private final String category;
	private final String message;
	private final Object[] parameters;
	private ResourceBundle bundle;

	public I18nMessage(String category, String message, Object... parameters) {
		this.category = category;
		this.message = message;
		this.parameters = parameters;
	}

	public void setBundle(ResourceBundle bundle) {
		this.bundle = bundle;
	}

	public String getMessage() {
		if (bundle == null) {
			throw new IllegalStateException("You must set the bundle before using the I18nMessage");
		}
		return MessageFormat.format(bundle.getString(message), parameters);
	}

	public String getCategory() {
		return category;
	}

}
