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

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A bean validator that does nothing
 * @author Lucas Cavalcanti
 * @since 3.1.2
 */
public class NullBeanValidator implements BeanValidator {

	private static final Logger logger = LoggerFactory.getLogger(NullBeanValidator.class);

	public List<Message> validate(Object object) {
		logger.warn("You are willing to validate an object, but there is no bean validation engine " +
				"registered. Please add the jars of some implementation of Bean Validator.");
		return Collections.emptyList();
	}

}
