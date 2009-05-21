/***
 *
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of the
 * copyright holders nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.vraptor2;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.vraptor2.outject.JsonOutjecter;
import org.vraptor.annotations.Remotable;
import org.vraptor.annotations.Viewless;
import org.vraptor.remote.json.JSONSerializer;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * VRaptor2 based ajax interceptor.<br>
 * Only outjects data if its not a viewless method.
 *
 * @author Guilherme Silveira
 */
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
