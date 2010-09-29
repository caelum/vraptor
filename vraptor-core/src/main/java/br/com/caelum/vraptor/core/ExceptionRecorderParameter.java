/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.caelum.vraptor.core;

import java.lang.reflect.Method;
import java.util.Arrays;

import br.com.caelum.vraptor.interceptor.ExceptionHandlerInterceptor;

/**
 * Wraps the input parameters for {@link ExceptionRecorder}.
 *
 * <p>This class is a part of Exception Handling Feature.</p>
 *
 * @author Otávio Scherer Garcia
 * @see ExceptionRecorder
 * @see ExceptionMapper
 * @see DefaultExceptionMapper
 * @see ExceptionHandlerInterceptor
 * @since 3.2
 */
class ExceptionRecorderParameter {
    public Object[] args;
    public Method method;

    public ExceptionRecorderParameter(Object[] args, Method method) {
        this.args = args;
        this.method = method;
    }

    /**
     * Gets the method arguments.
     *
     * @return
     */
    public Object[] getArgs() {
        return args;
    }

    /**
     * Gets the {@link Method}.
     *
     * @return
     */
    public Method getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return new StringBuilder().append(Arrays.toString(args)).append(method.getName()).toString();
    }

}