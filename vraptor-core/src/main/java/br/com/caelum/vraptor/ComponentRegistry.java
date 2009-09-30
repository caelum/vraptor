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

/**
 * <p>Represents the registry of all components allowing them to be registered for injection.</p>
 * <p>This interface doesnt allow component lookup, so during component
 * registration phase no provider tries to instantiate something before it's time
 * to do so.</p>
 *
 * @author Guilherme Silveira
 */
public interface ComponentRegistry {

    /**
     * Registers a component to be used only when the required type is required.
     * @param componentType the component type
     */
    public void register(Class<?> requiredType, Class<?> componentType);

    /**
     * Registers a component to be used when the required type is the component, or
     * any of their interfaces and superclasses.
     * @param componentType the component type
     */
    public void deepRegister(Class<?> componentType);

}
