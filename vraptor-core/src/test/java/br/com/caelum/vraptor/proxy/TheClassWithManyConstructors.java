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
package br.com.caelum.vraptor.proxy;

import java.util.Properties;

/**
 * @author Fabio Kung
 */
public class TheClassWithManyConstructors {
    private Number number;
    private boolean numberConstructorCalled;

    public TheClassWithManyConstructors(String nullValue) {
        // constructor calling methods on dependencies
        nullValue.toString();
    }

    public TheClassWithManyConstructors(Properties nullValue) throws IllegalAccessException {
        throw new IllegalAccessException("constructor with errors");
    }

    public TheClassWithManyConstructors(Number number) {
        this.number = number;
        this.numberConstructorCalled = true;
    }

    public boolean wasNumberConstructorCalled() {
        return numberConstructorCalled;
    }

    public Number getNumber() {
        return number;
    }
}
