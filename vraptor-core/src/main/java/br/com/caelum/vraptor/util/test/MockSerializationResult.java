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
import br.com.caelum.vraptor.interceptor.DefaultTypeNameExtractor;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.proxy.ObjenesisProxifier;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.serialization.HibernateProxyInitializer;
import br.com.caelum.vraptor.serialization.JSONSerialization;
import br.com.caelum.vraptor.serialization.NullProxyInitializer;
import br.com.caelum.vraptor.serialization.ProxyInitializer;
import br.com.caelum.vraptor.serialization.Serialization;
import br.com.caelum.vraptor.serialization.XMLSerialization;
import br.com.caelum.vraptor.serialization.xstream.XStreamJSONSerialization;
import br.com.caelum.vraptor.serialization.xstream.XStreamXMLSerialization;
import br.com.caelum.vraptor.view.EmptyResult;

/**
 *
 * A mocked Result for testing your serialized objects returns.
 *
 * It will serialize your objects for real and return them as string,
 * this way, you could use result.use(Resultrs.json()).from(object) for serialize and inspect objects.
 *
 * @author Vinícius Oliveira
 */
@Component
public class MockSerializationResult extends MockResult {

	private Serialization serialization;
	private MockHttpServletResponse response;
	private DefaultTypeNameExtractor extractor;
	private ProxyInitializer initializer;
	
	
	public MockSerializationResult(Proxifier proxifier, ProxyInitializer initializer ) {
		super(proxifier);
		this.initializer = initializer;
		this.response = new MockHttpServletResponse();
		this.extractor = new DefaultTypeNameExtractor();
	}

	public MockSerializationResult() {
		this(new ObjenesisProxifier(), new NullProxyInitializer());
	}

	public <T extends View> T use(final Class<T> view) {
		this.typeToUse = view;
		if (view.equals(EmptyResult.class)) {
			return null;
		}
		return instanceView(view);
	}

	@SuppressWarnings("unchecked")
	private <T extends View> T instanceView(Class<T> view){
		
		if(view.isAssignableFrom(JSONSerialization.class)){
			this.serialization = new XStreamJSONSerialization(response, extractor, initializer); 
			return (T) serialization;
		}
		
		if( view.isAssignableFrom(XMLSerialization.class)){
			this.serialization = new XStreamXMLSerialization(response, extractor, initializer); 
			return (T) serialization;
		}
		
		return proxifier.proxify(view, returnOnFinalMethods(view));
	}
	
		
	/**
	 * Retrieve the string with the serialized (JSON/XML) Object if have one as response. 
	 * 
	 * @return String with the object serialized 
	 */
	public String serializedResult() throws Exception {
		
		if("application/xml".equals(response.getContentType())){
			return response.getContentAsString();
		}
		
		if("application/json".equals(response.getContentType())){
			return response.getContentAsString();
		}
		
		return null;
	}

}