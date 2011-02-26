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
	 * All paths that will be mapped to an annotated Resource method. The path value also can be 
	 * configured in class level and using {@link Get}, {@link Post}, {@link Trace} and {@link Delete} 
	 * annotations. If both {@link Path} and these annotations are used, an exception will be thrown.
	 * @return
	 */
    String[] value();

    /**
     * Used to decide which path will be tested first.
     * Paths with priority HIGHEST are tested before paths with priority HIGH,
     * which are tested before paths with priority DEFAULT, and so on.
     * <pre>
	     @Path(value="/url", priority=Path.HIGHEST)
	     @Path(value="/url", priority=Path.HIGH)
	     @Path(value="/url", priority=Path.DEFAULT)
	     @Path(value="/url", priority=Path.LOW)
	     @Path(value="/url", priority=Path.LOWEST)
     </pre>
     *
     */
    int priority() default DEFAULT;

    public static final int LOWEST = Integer.MAX_VALUE;
    public static final int LOW = Integer.MAX_VALUE/4*3;
    public static final int DEFAULT = Integer.MAX_VALUE/2;
    public static final int HIGH = Integer.MAX_VALUE/4;
    public static final int HIGHEST = 0;

}
