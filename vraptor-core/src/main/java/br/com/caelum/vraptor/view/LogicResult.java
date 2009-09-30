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

package br.com.caelum.vraptor.view;

import br.com.caelum.vraptor.View;

/**
 * Logic result allows logics to redirect to another logic by invoking the
 * method itself, in a very typesafe way.
 *
 * @author Guilherme Silveira
 */
public interface LogicResult extends View {

    /**
     * <p>Returns an instance of the given (pre-registered) logic.</p>
     * <p>
     * Any method called in the returned instance will cause a server side redirect (forward) to the called action.
     * </p>
     */
    <T> T forwardTo(Class<T> type);

    /**
     * <p>Returns an instance of that (pre-registered) logic.</p>
     * <p>
     * Any method called in the returned instance will cause a client side redirect to the called action, with the
     * given parameters as HTTP parameters.
     * </p>
     */
    <T> T redirectTo(Class<T> type);

}
