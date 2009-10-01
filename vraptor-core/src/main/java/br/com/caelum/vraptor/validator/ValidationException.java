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

import java.util.List;

/**
 * If some validation error occur, its encapsulated within an instance of
 * ValidationException, which is then throw and parsed.
 *
 * @author Guilherme Silveira
 */
public class ValidationException extends RuntimeException {

    /**
     * Serialized id.
     */
    private static final long serialVersionUID = -1069844446293479183L;

    private final List<Message> errors;

    public ValidationException(List<Message> errors) {
        this.errors = errors;
    }

    public List<Message> getErrors() {
        return errors;
    }

    /**
     * We don't need stack traces for this exception. It is used only to control flow.
     * The default implementation for this method is extremely expensive.
     *
     * @return reference for this, without filling the stack trace
     */
    @Override
    public final Throwable fillInStackTrace() {
        return this;
    }
}
