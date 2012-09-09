/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.caelum.vraptor.gae;

import java.net.MalformedURLException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.core.DefaultStaticContentHandler;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;

/**
 * Implementation for {@link DefaultStaticContentHandler} that ignores all requests for "/_ah"
 * URIs, used by GAE/J for admin purposes.
 * 
 * @author Lucas Cavalcanti
 * @since 3.4.1
 */
@Component
@ApplicationScoped
public class AppEngineStaticContentHandler extends DefaultStaticContentHandler {
	
	private static Logger logger = LoggerFactory.getLogger(AppEngineStaticContentHandler.class);

	public AppEngineStaticContentHandler(ServletContext context) {
		super(context);
	}

	@Override
	public boolean requestingStaticFile(HttpServletRequest request) throws MalformedURLException {
		if (request.getRequestURI().startsWith(request.getContextPath() + "/_ah")) {
			logger.debug("Requesting appEngine config URI: {}. Bypassing VRaptor", request.getRequestURI());
			return true;
		}
		return super.requestingStaticFile(request);
	}
}
