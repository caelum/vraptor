package br.com.caelum.vraptor.util;

import br.com.caelum.vraptor.vraptor2.Info;

/**
 * Utility methods to handle strings

 * @author Lucas Cavalcanti
 */
public class StringUtils {

	public static String lowercaseFirst(String name) {
		// common case: SomeClass -> someClass
		if(name.length() > 1 && Character.isLowerCase(name.charAt(1))) {
			return Info.decapitalize(name);
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

}
