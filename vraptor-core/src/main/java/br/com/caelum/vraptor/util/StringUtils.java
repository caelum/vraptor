/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods to handle strings

 * @author Lucas Cavalcanti
 */
public class StringUtils {

	public static String decapitalize(String name) {
        if (name.length() == 1) {
            return name.toLowerCase();
        }
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
	}

	public static String capitalize(String name) {
        if (name.length() == 1) {
            return name.toUpperCase();
        }
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
	
	public static String lowercaseFirst(String name) {
		// common case: SomeClass -> someClass
		if(name.length() > 1 && Character.isLowerCase(name.charAt(1))) {
			return decapitalize(name);
		}

		// different case: URLClassLoader -> urlClassLoader
		for (int i = 1; i < name.length(); i++) {
			if(Character.isLowerCase(name.charAt(i))) {
				return name.substring(0, i-1).toLowerCase()+name.substring(i-1, name.length());
			}
		}

		// all uppercase: URL -> url
		return name.toLowerCase();
	}

	public static String[] extractParameters(String uri) {
		//yeah, regexes are the root of all evil... so falling back to bracket matching!!! =)
		List<String> params = new ArrayList<String>();
		StringBuilder param = new StringBuilder();
		int brackets = 0;
		for (int i = 0; i < uri.length(); i++) {
			char character = uri.charAt(i);
			if (character == '{') {
				brackets++;
				if (brackets == 1) {
					continue;
				}
			} else if (character == '}') {
				brackets--;
				if (brackets == 0) {
					params.add(param.toString());
					param = new StringBuilder();
				}
			}
			if (brackets > 0) {
				param.append(character);
			}
		}
		return params.toArray(new String[params.size()]);
	}

}
