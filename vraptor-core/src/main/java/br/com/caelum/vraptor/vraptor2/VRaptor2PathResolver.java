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

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceClass;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.AcceptHeaderToFormat;
import br.com.caelum.vraptor.view.DefaultPathResolver;
import br.com.caelum.vraptor.view.PathResolver;

/**
 * Vraptor 2 and 3 compatible path resolver.
 *
 * @author Guilherme Silveira
 */
@RequestScoped
public class VRaptor2PathResolver implements PathResolver {

    private final PathResolver vraptor3;
    private final String pattern;
	private final MethodInfo info;

    public VRaptor2PathResolver(Config config, HttpServletRequest request, MethodInfo info) {
        this.info = info;
		this.pattern = config.getViewPattern();
        this.vraptor3 = new DefaultPathResolver(request, new AcceptHeaderToFormat() {
			public String getFormat(String mimeType) {
				return "html";
			}
		});
    }

    public String pathFor(ResourceMethod method) {
        ResourceClass resource = method.getResource();
        if (Info.isOldComponent(resource)) {
            String component = Info.getComponentName(resource.getType());
            String logicName = Info.getLogicName(method.getMethod());
            return pattern.replaceAll("\\$component", component).replaceAll("\\$logic", logicName).replaceAll("\\$result", info.getResult().toString());
        }
        return vraptor3.pathFor(method);
    }

}
