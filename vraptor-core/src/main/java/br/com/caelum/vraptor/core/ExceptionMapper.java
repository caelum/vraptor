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

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.ExceptionHandlerInterceptor;

/**
 * Store the exception mapping for exception handling.
 *
 * <p>This class is a part of Exception Handling Feature.</p>
 *
 * @author Otávio Scherer Garcia
 * @see ExceptionRecorder
 * @see ExceptionRecorderParameter
 * @see DefaultExceptionMapper
 * @see ExceptionHandlerInterceptor
 * @since 3.2
 */
public interface ExceptionMapper {

    /**
     * Store the exception and return a proxy with {@link Result} state.
     *
     * @param exception The exception to store.
     * @return
     * @throws NullPointerException if the exception is null.
     */
    Result record(Class<? extends Exception> exception);

    /**
     * Finds an {@link ExceptionRecorder} by an {@link Exception}. If
     * {@link ExceptionRecorder} is not found, returns null.
     *
     * @param exception The exception to find.
     * @return
     */
    ExceptionRecorder<Result> findByException(Exception exception);

}