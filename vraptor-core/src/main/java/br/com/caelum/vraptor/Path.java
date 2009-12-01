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

/**
 * Sets a custom path to allow web requisitions to access this resource.<br>
 * To be used together with web methods annotations as Get, Post and so on.
 *
 * @author Guilherme Silveira
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Path {

	/**
	 * All paths that will be mapped to an annotated Resource method.
	 * @return
	 */
    String[] value();

    /**
     * Used to decide which path will be tested first.
     * Paths with priority 0 will be tested before paths with priority 1, and
     * so on.
     * @return
     */
    int priority() default Integer.MAX_VALUE - 1;

}
