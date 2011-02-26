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

package br.com.caelum.vraptor.interceptor.download;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Lazy;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.ForwardToDefaultViewInterceptor;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Intercepts methods whom return a File or an InputStream.
 *
 * @author filipesabella
 */
@Intercepts(after=ExecuteMethodInterceptor.class, before=ForwardToDefaultViewInterceptor.class)
@RequestScoped
@Lazy
public class DownloadInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(DownloadInterceptor.class);

	private final HttpServletResponse response;
	private final MethodInfo info;
	private final Result result;

	public DownloadInterceptor(HttpServletResponse response, MethodInfo info, Result result) {
		this.response = response;
		this.info = info;
		this.result = result;
	}

	public boolean accepts(ResourceMethod method) {
		Class<?> type = method.getMethod().getReturnType();
		return InputStream.class.isAssignableFrom(type) || type == File.class || Download.class.isAssignableFrom(type)
				|| type == byte[].class;
	}

	public void intercept(InterceptorStack stack, ResourceMethod method, Object instance) throws InterceptionException {
    	logger.debug("Sending a file to the client");

		Object result = info.getResult();

		if (result == null) {
			if (this.result.used()) {
				stack.next(method, instance);
				return;
			} else {
				throw new NullPointerException("You've just returned a Null Download. Consider redirecting to another page/logic");
			}
		}
		try {
			Download download = null;

			if (result instanceof InputStream) {
				InputStream input = (InputStream) result;
				download = new InputStreamDownload(input, null, null);
			} else if (result instanceof byte[]) {
				byte[] bytes = (byte[]) result;
				download = new ByteArrayDownload(bytes, null, null);
			} else if (result instanceof File) {
				File file = (File) result;
				download = new FileDownload(file, null, null);
			} else if (result instanceof Download) {
				download = (Download) result;
			}

			OutputStream output = response.getOutputStream();
			download.write(response);

			output.flush();
			output.close();
		} catch (IOException e) {
			throw new InterceptionException(e);
		}

	}
}
