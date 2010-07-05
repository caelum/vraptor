/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hamcrest;

import java.io.IOException;

/**
 * A description which uses a resource bundle to i18n messages.
 *
 * @author guilherme silveira
 *
 */
public class ResourceBundleDescription extends BaseDescription {

    private final Appendable out;

    public ResourceBundleDescription() {
		this(new StringBuilder());
	}

    public ResourceBundleDescription(Appendable out) {
		this.out = out;
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
			String keyValue = parsed.replace(' ', '_');
			super.appendText(keyValue);
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
