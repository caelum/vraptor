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
package br.com.caelum.vraptor.interceptor;


/**
 * Implements an interceptor stack capable of defining the order in which
 * interceptors are invoked.<br>
 * Interceptors within the set returned by getSequence are invoked in the
 * returned order.
 *
 * You should not annotate returned interceptors neither with @Intercepts nor
 * @Component.
 * @author Guilherme Silveira
 */
@Deprecated
public interface InterceptorSequence {

    /**
     * Returns an array of interceptors to be invoked in such order.
     */
    Class<? extends Interceptor>[] getSequence();

}
