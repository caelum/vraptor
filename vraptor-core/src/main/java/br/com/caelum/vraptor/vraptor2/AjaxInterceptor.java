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

package br.com.caelum.vraptor.vraptor2;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.vraptor.annotations.Remotable;
import org.vraptor.annotations.Viewless;
import org.vraptor.remote.json.JSONSerializer;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.vraptor2.outject.JsonOutjecter;

/**
 * VRaptor2 based ajax interceptor.<br>
 * Only outjects data if its not a viewless method.
 *
 * @author Guilherme Silveira
 */
@Component
public class AjaxInterceptor implements Interceptor {

    private static final String UTF8 = "UTF-8";

    private final ComponentInfoProvider info;

    private final HttpServletResponse response;

    public AjaxInterceptor(HttpServletResponse response, ComponentInfoProvider info) {
        this.response = response;
        this.info = info;

    }

    public boolean accepts(ResourceMethod method) {
        // TODO this is not invoked as automatically loaded thorugh
        // RequestExecution
        // it should be included on the ExtractorList so would not be invoked?
        return info.isAjax();
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
            throws InterceptionException {
        boolean isViewless = method.getMethod().isAnnotationPresent(Viewless.class);
		if (!isViewless && info.isAjax()) {
            if (!method.getMethod().isAnnotationPresent(Remotable.class)) {
                throw new InterceptionException("Unable to make an ajax result in a non-remotable method.");
            }
            int depth = method.getMethod().getAnnotation(Remotable.class).depth();
            JsonOutjecter outjecter = (JsonOutjecter) info.getOutjecter();
            CharSequence output = new JSONSerializer(depth).serialize(outjecter.contents());
            response.setCharacterEncoding(UTF8);
            response.setContentType("application/json");

            try {
                PrintWriter writer = response.getWriter();
                writer.append(output);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                throw new InterceptionException(e);
            }
        } else {
            stack.next(method, resourceInstance);
        }
    }

}
