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
