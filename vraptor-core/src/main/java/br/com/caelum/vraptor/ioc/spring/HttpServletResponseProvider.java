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

package br.com.caelum.vraptor.ioc.spring;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.FactoryBean;

import br.com.caelum.vraptor.http.MutableResponse;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * Provides the current javax.servlet.http.HttpServletResponse object, provided that Spring has registered it for the
 * current Thread.
 *
 * @author Fabio Kung
 * @see org.springframework.web.context.request.ServletWebRequest
 */
@ApplicationScoped
class HttpServletResponseProvider implements FactoryBean<HttpServletResponse> {

    public HttpServletResponse getObject() throws Exception {
        return VRaptorRequestHolder.currentRequest().getResponse();
    }

    public Class<? extends HttpServletResponse> getObjectType() {
        return MutableResponse.class;
    }

    public boolean isSingleton() {
        return false;
    }
}
