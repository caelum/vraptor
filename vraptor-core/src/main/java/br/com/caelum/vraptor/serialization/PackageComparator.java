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

import java.util.Comparator;

/**
 * 
 * @author A.C de Souza
 * @since 3.4.0
 */
final class PackageComparator implements Comparator<Serialization> {
	private int number(Serialization s) {
		String packageName = s.getClass().getPackage().getName();
		if (packageName.startsWith("br.com.caelum.vraptor.serialization")
		 || packageName.startsWith("br.com.caelum.vraptor.restfulie.serialization")) {
			return 1;
		}
		return 0;
	}
	
	private int giveMorePriorityToRestfulie(Serialization o1, Serialization o2) {
		String packageNameO1 = o1.getClass().getPackage().getName();
		String packageNameO2 = o2.getClass().getPackage().getName();
		
		if(packageNameO1.startsWith("br.com.caelum.vraptor.serialization") 
		&& packageNameO2.startsWith("br.com.caelum.vraptor.restfulie.serialization")) {
			return 1;
		}
		
		return 0;
	}

	public int compare(Serialization o1, Serialization o2) {
		int numberO1 = number(o1);
		int numberO2 = number(o2);
		int compareResult = numberO1 - numberO2;
		
		if(compareResult == 0) {
			return giveMorePriorityToRestfulie(o1, o2);
		}

		return compareResult;
	}
}