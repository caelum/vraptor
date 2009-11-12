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
package br.com.caelum.vraptor.serialization;

import br.com.caelum.vraptor.ioc.Component;

/**
 * Basic xml configuration for xml serialization.
 * @author guilherme silveira
 * @since 3.0.2
 */
@Component
public class DefaultXMLConfiguration implements XMLConfiguration {

//	private static final List<Class<?>> PRIMITIVE_TYPES = Arrays.asList(
//			String.class, Integer.class, BigInteger.class, BigDecimal.class,
//
//			);
	public String nameFor(String name) {
		if(name.length()==1) {
			return name.toLowerCase();
		}
		StringBuilder content = new StringBuilder();
		content.append(Character.toLowerCase(name.charAt(0)));
		for(int i=1;i<name.length();i++) {
			char c = name.charAt(i);
			if(Character.isUpperCase(c)) {
				content.append("_");
				content.append(Character.toLowerCase(c));
			} else {
				content.append(c);
			}
		}
		return content.toString();
	}

}
