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

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Lazy;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Whenever an interceptor accepts a resource method, its intercept method is
 * invoked to intercept the process of request parsing in order to allow the
 * software to do some specific tasks.<br>
 * Common usage for interceptors for end-users (end-programmers) are security
 * constraint checks, database session (open session in view) opening and much
 * more.
 *
 * If you have an interceptor A which depends on an interceptor B, i.e, interceptor
 * B must be executed before interceptor A, use {@link InterceptorSequence}.
 *
 * If the {@link Interceptor#accepts(ResourceMethod)} method only depends on received
 * {@link ResourceMethod}, you can annotate the interceptor with @{@link Lazy}, so the
 * Interceptor will only be instantiated if the accepts returns true.
 * You should not use @Lazy if accepts is constant, or depends on any constructor parameter.
 *
 * @see InterceptorSequence
 * @see Lazy
 * @author Guilherme Silveira
 */
public interface Interceptor {

    void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException;

    boolean accepts(ResourceMethod method);

}
