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

package br.com.caelum.vraptor.validator;

import static com.google.common.base.Objects.toStringHelper;

import java.text.MessageFormat;

/**
 * A simple validation message.
 *
 * @author Guilherme Silveira
 */
public class ValidationMessage implements Message {

	private static final long serialVersionUID = 1L;

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

	@Override
	public String toString() {
		return toStringHelper(this).add("category", category).add("message", message).add("parameters", messageParameters).toString();
	}

}
