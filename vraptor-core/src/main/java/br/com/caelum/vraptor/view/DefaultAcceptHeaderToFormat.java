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
package br.com.caelum.vraptor.view;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * The default AcceptHeaderToFormat implementation
 *
 * @author Sérgio Lopes
 * @author Jonas Abreu
 */
@ApplicationScoped
public class DefaultAcceptHeaderToFormat implements AcceptHeaderToFormat {

	private static final String DEFAULT_FORMAT = "html";
	protected final Map<String, String> map;

	public DefaultAcceptHeaderToFormat() {
		map = new ConcurrentHashMap<String, String>();
		map.put("text/html", "html");
		map.put("application/json", "json");
		map.put("application/xml", "xml");
		// TODO add more mime types
	}

	public String getFormat(String acceptHeader) {
		String[] mimeTypes = acceptHeader.split("(;[^,]*)?,\\s*");


		for (String mimeType : mimeTypes) {
			String format = map.get(mimeType);
			if (format != null) {
				return format;
			}
		}

		return DEFAULT_FORMAT;
	}
}
