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

package br.com.caelum.vraptor;

import java.util.Collection;

import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.Validations;

/**
 * A validator interface for vraptor3.<br>
 * Based on hamcrest, it allows you to assert for specific situations.
 *
 * @author Guilherme Silveira
 */
public interface Validator {

    void checking(Validations rules);

    /**
     * Validate an object using some Bean Validation engine. If the object is null,
     * the validation will be skiped.
     *
     * @param object The object to be validated.
     * @since vraptor3.1.2
     */
    void validate(Object object);

    <T extends View> T onErrorUse(Class<T> view);

    void addAll(Collection<? extends Message> message);

    void add(Message message);

    boolean hasErrors();

}
