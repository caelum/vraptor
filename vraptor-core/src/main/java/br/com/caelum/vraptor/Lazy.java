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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Identifies that given class should be used with a lazy strategy.
 *
 * On an {@link Interceptor}, it means that it should only
 * be instantiated if the {@link Interceptor#accepts(br.com.caelum.vraptor.resource.ResourceMethod)}
 * method returns true. In this case, the {@link Interceptor#accepts(br.com.caelum.vraptor.resource.ResourceMethod)}
 * should only depend on the received {@link ResourceMethod}.
 *
 * @author Lucas Cavalcanti
 * @author Alberto Souza
 * @since 3.2.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Lazy {

}
