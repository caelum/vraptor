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

/**
 * VRaptor's default implementation of a {@link ValidationException}.
 * Users can use this class if they don't want to implement their own business exceptions
 *
 * @author Sérgio Lopes
 * @see ValidationException
 */
@br.com.caelum.vraptor.validator.annotation.ValidationException
public class DefaultValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DefaultValidationException(String msg) {
		super(msg);
	}

	public DefaultValidationException(Throwable cause) {
		super(cause);
	}

	public DefaultValidationException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
