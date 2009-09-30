
package br.com.caelum.vraptor.validator;

import java.text.MessageFormat;

/**
 * A simple validation message.
 *
 * @author Guilherme Silveira
 */
public class ValidationMessage implements Message {

	private final String message, category;
    private final Object[] messageParameters;

	public ValidationMessage(String message, String category) {
		this(message, category, (Object[]) null);
	}

	public ValidationMessage(String message, String category, Object... messageParameters) {
	    this.message = message;
	    this.category = category;
	    this.messageParameters = messageParameters;
	}

	public String getMessage() {
	    if (messageParameters != null) {
	        return MessageFormat.format(message, messageParameters);
	    }
		return message;
	}

	public String getCategory() {
		return category;
	}

}
