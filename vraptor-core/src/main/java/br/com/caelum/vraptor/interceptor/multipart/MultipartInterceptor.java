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
package br.com.caelum.vraptor.interceptor.multipart;

import br.com.caelum.vraptor.interceptor.Interceptor;

/**
 * An interface to provides a choise between {@link NullMultipartInterceptor} and {@link DefaultMultipartInterceptor}.
 * If the commons file upload is found in classpath the {@link DefaultMultipartInterceptor} is enabled, or
 * {@link NullMultipartInterceptor} otherwise.
 * 
 * @author Otávio Scherer Gaecia
 * @since 3.1.x-garcia
 */
public interface MultipartInterceptor
    extends Interceptor {

}
