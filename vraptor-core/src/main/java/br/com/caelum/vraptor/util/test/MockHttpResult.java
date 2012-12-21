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

package br.com.caelum.vraptor.util.test;

import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.proxy.CglibProxifier;
import br.com.caelum.vraptor.proxy.ObjenesisInstanceCreator;
import br.com.caelum.vraptor.util.test.MockHttpServletResponse;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.view.DefaultHttpResult;
import br.com.caelum.vraptor.view.DefaultStatus;
import br.com.caelum.vraptor.view.HttpResult;

/**
 *
 * A mocked Result for testing the http body, content type and character encoding.
 *
 * It will store the http body when using result.use(Results.http()).addHeader("Content-Type", "application/json; charset=utf-8").body(json)
 * and return this content as string.
 *
 * @author Danilo Muñoz
 */
@Component
public class MockHttpResult extends MockResult{
	private MockHttpServletResponse response = new MockHttpServletResponse();
	
	public MockHttpResult() {
		super(new CglibProxifier(new ObjenesisInstanceCreator()));
	}
	
	@Override
	public <T extends View> T use(Class<T> view) {
		if (view.isAssignableFrom(HttpResult.class)){
			DefaultHttpResult defaultHttpResult = new DefaultHttpResult(response, new DefaultStatus(response, this, null, proxifier, null));
			return view.cast(defaultHttpResult);
		}
		
		return proxifier.proxify(view, returnOnFinalMethods(view));
	}
	
	public String getBody(){
		response.getWriter().flush();
		return response.getContentAsString();
	}
	
	public String getContentType(){
		return response.getContentType();
	}
	
	public String getCharacterEncoding(){
		return response.getCharacterEncoding();
	}
}
