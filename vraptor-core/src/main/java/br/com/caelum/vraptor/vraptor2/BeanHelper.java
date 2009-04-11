package br.com.caelum.vraptor.vraptor2;

import java.lang.reflect.Method;

/**
 * Vraptor 2 bean dealing helper methods.
 * 
 * @author Guilherme Silveira
 */
public class BeanHelper {
    
    private static final String IS = "is";

    public String nameForGetter(Method getter) {
        String name = getter.getName();
        if(name.startsWith(IS)) {
            name = name.substring(2);
        } else {
            name = name.substring(3);
        }
        return decapitalize(name);
    }

    public String decapitalize(String name) {
        return name.length()==1 ? name.toLowerCase() : Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

}
