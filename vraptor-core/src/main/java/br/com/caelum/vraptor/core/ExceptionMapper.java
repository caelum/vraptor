/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.core;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import br.com.caelum.vraptor.Result;

/**
 * Store the exception mapping for exception handling.
 * 
 * @author Otávio Scherer Garcia
 * @since 3.2
 */
public class ExceptionMapper {

    private final Map<Class<? extends Exception>, ExceptionRecorder<Result>> exceptions;

    public ExceptionMapper() {
        exceptions = new LinkedHashMap<Class<? extends Exception>, ExceptionRecorder<Result>>();
    }

    /**
     * Add an exception.
     * 
     * @param exception
     * @param result
     */
    public void add(Class<? extends Exception> exception, ExceptionRecorder<Result> result) {
        exceptions.put(exception, result);
    }

    /**
     * Return an exception list.
     * 
     * @return
     */
    public Set<Entry<Class<? extends Exception>, ExceptionRecorder<Result>>> entries() {
        return Collections.unmodifiableMap(exceptions).entrySet();
    }

}
