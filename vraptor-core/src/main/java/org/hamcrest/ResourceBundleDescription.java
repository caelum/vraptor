package org.hamcrest;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * A description which uses a resource bundle to i18n messages.
 * 
 * @author guilherme silveira
 * 
 */
public class ResourceBundleDescription extends BaseDescription {

    private final Appendable out;

    private final ResourceBundle bundle;

    public ResourceBundleDescription(ResourceBundle bundle) {
		this(bundle, new StringBuilder());
	}

    public ResourceBundleDescription(ResourceBundle bundle, Appendable out) {
		this.out = out;
		this.bundle = bundle;
	}

	@Override
    protected void append(char c) {
        try {
            out.append(c);
        } catch (IOException e) {
            throw new RuntimeException("Could not write description", e);
        }
    }

	/**
	 * Append the String <var>str</var> to the description. The default
	 * implementation passes every character to {@link #append(char)}. Override
	 * in subclasses to provide an efficient implementation.
	 */
	@Override
	protected void append(String str) {
		directAppend(str);
	}

	@Override
	public Description appendText(String text) {
		int len = text.length();
		int st = 0;
		char[] val = text.toCharArray();

		while ((st < len) && (val[st] <= ' ')) {
			append(val[st++]);
		}
		while ((st < len) && (val[len - 1] <= ' ')) {
			len--;
		}
		String parsed = ((st > 0) || (len < text.length())) ? text.substring(st, len) : text;
		if (parsed.length() != 0) {
			super.appendText(bundle.getString(parsed.replace(' ', '_')));
		}
		while (len != text.length()) {
			append(val[len++]);
		}
		return this;
	}

	/**
     * Appends the string straight to the buffer. 
     */
	protected void directAppend(String str) {
		try {
			out.append(str);
		} catch (IOException e) {
			throw new RuntimeException("Could not write description", e);
		}
	}
	
	@Override
	public String toString() {
		return out.toString();
	}

}
