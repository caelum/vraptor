/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.caelum.vraptor.validator;

import static br.com.caelum.vraptor.view.Results.logic;
import static br.com.caelum.vraptor.view.Results.page;
import static br.com.caelum.vraptor.view.Results.status;

import java.util.List;

import br.com.caelum.vraptor.Validator;

/**
 *
 * Implements the Validator shortcut methods.
 *
 * @author Lucas Cavalcanti
 * @since 3.1.3
 *
 */
public abstract class AbstractValidator implements Validator {

	public <T> T onErrorForwardTo(Class<T> controller) {
		return onErrorUse(logic()).forwardTo(controller);
	}

	public <T> T onErrorForwardTo(T controller) {
		return (T) onErrorUse(logic()).forwardTo(controller.getClass());
	}

	public <T> T onErrorRedirectTo(Class<T> controller) {
		return onErrorUse(logic()).redirectTo(controller);
	}

	public <T> T onErrorRedirectTo(T controller) {
		return (T) onErrorUse(logic()).redirectTo(controller.getClass());
	}

	public <T> T onErrorUsePageOf(Class<T> controller) {
		return onErrorUse(page()).of(controller);
	}

	public <T> T onErrorUsePageOf(T controller) {
		return (T) onErrorUse(page()).of(controller.getClass());
	}

	public void onErrorSendBadRequest() {
		onErrorUse(status()).badRequest(getErrors());
	}

}
