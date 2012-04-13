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
package br.com.caelum.vraptor.http;

import java.lang.reflect.AccessibleObject;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.util.Stringnifier;

import com.thoughtworks.paranamer.AnnotationParanamer;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.ParameterNamesNotFoundException;
import com.thoughtworks.paranamer.Paranamer;

/**
 * Paranamer based parameter name provider provides parameter names based on
 * their local variable name during compile time. Information is retrieved using
 * paranamer's mechanism.
 *
 * @author Guilherme Silveira
 */
@ApplicationScoped
public class ParanamerNameProvider implements ParameterNameProvider {
	private final Paranamer info = new CachingParanamer(new AnnotationParanamer(new BytecodeReadingParanamer()));

	private static final Logger logger = LoggerFactory.getLogger(ParanamerNameProvider.class);

	public String[] parameterNamesFor(AccessibleObject method) {
		try {
			String[] parameterNames = info.lookupParameterNames(method);
			if (logger.isDebugEnabled()) {
				logger.debug("Found parameter names with paranamer for {} as {}",
					Stringnifier.simpleNameFor(method), Arrays.toString(parameterNames));
			}

			// maybe paranamer cache already provides defensive copies?
			String[] defensiveCopy = new String[parameterNames.length];
			System.arraycopy(parameterNames, 0, defensiveCopy, 0, parameterNames.length);
			return defensiveCopy;
		} catch (ParameterNamesNotFoundException e) {
			throw new IllegalStateException("Paranamer were not able to find your parameter names for " + method
					+ "You must compile your code with debug information (javac -g) or register another "
					+ "name provider. Try to use " + DefaultParameterNameProvider.class.getName() + " instead.");
		}
	}

}
