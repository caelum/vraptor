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

package br.com.caelum.vraptor.core;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * An interceptor handler is a wrapper to either an interceptor instance or a
 * interceptor definition. This way, the interceptor stack is capable of pushing
 * either already instantiated interceptors or
 * on-the-run-to-be-instantiated-interceptors. This gives an interceptor the
 * capability to require dependencies which will be provided by other
 * interceptors registered later on the interceptor stack (but in a previous position).
 * 
 * @author Guilherme Silveira
 */
public interface InterceptorHandler {

    void execute(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException;

}
