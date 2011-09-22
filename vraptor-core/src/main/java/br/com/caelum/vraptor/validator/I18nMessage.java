package br.com.caelum.vraptor.validator;

import static com.google.common.base.Objects.toStringHelper;

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

	private static final long serialVersionUID = 1L;

	private final Object category;
	private final String message;
	private final Object[] parameters;
	private transient ResourceBundle bundle;
	
	public I18nMessage(I18nParam category, String message, Object... parameters) {
		this.category = category;
		this.message = message;
		this.parameters = parameters;
	}

	public I18nMessage(String category, String message, Object... parameters) {
		this.category = category;
		this.message = message;
		this.parameters = parameters;
	}

	public void setBundle(ResourceBundle bundle) {
		this.bundle = bundle;
	}

	public boolean hasBundle() {
		return this.bundle != null;
	}

	public String getMessage() {
		checkBundle();

		return MessageFormat.format(bundle.getString(message), i18n(parameters));
	}

	private void checkBundle() {
		if (bundle == null) {
			throw new IllegalStateException("You must set the bundle before using the I18nMessage");
		}
	}

    private Object[] i18n(Object[] parameters) {
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i] instanceof I18nParam) {
                parameters[i] = ((I18nParam)parameters[i]).getKey(bundle);
            }
        }
        return parameters;
    }

    public String getCategory() {
    	if (category instanceof I18nParam) {
    		checkBundle();

			return ((I18nParam) category).getKey(bundle);
		}

		return category.toString();
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("category", category).add("message", message).add("parameters", parameters).toString();
	}

}
