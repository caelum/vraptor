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

package br.com.caelum.vraptor.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Default implementation of {@link MutableResponse}
 * @author Lucas Cavalcanti
 * @author Caires Vinicius
 * @author Adriano Almeida
 */
public class VRaptorResponse extends HttpServletResponseWrapper implements MutableResponse {

	private final List<RedirectListener> listeners = new ArrayList<RedirectListener>();

	public VRaptorResponse(HttpServletResponse response) {
		super(response);
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		for (RedirectListener listener : listeners) {
			listener.beforeRedirect();
		}
		super.sendRedirect(location);
	}
	public void addRedirectListener(RedirectListener listener) {
		listeners.add(listener);
	}

}
