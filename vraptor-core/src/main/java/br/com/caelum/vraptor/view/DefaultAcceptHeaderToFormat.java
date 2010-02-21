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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * The default AcceptHeaderToFormat implementation searches for registered mime types. It also
 * handles conneg with extended media types (i.e. vnd+xml)
 *
 * @author Sérgio Lopes
 * @author Jonas Abreu
 * @author Guilherme Silveira
 */
@ApplicationScoped
public class DefaultAcceptHeaderToFormat implements AcceptHeaderToFormat {

	private static Cache cache = new Cache();
	private static final String DEFAULT_FORMAT = "html";
	protected final Map<String, String> map;

	public DefaultAcceptHeaderToFormat() {
		map = new ConcurrentHashMap<String, String>();
		map.put("text/html", "html");
		map.put("application/json", "json");
		map.put("application/xml", "xml");
		map.put("xml", "xml");
	}

	public String getFormat(String acceptHeader) {

		if (acceptHeader == null) {
			throw new NullPointerException("accept header cant be null");
		}

		if (acceptHeader.contains(DEFAULT_FORMAT)) {
			return DEFAULT_FORMAT;
		}

		if (cache.containsKey(acceptHeader)) {
			return cache.get(acceptHeader);
		}

		// TODO: we really could cache acceptHeader -> format
		String[] mimeTypes = getOrderedMimeTypes(acceptHeader);

		for (String mimeType : mimeTypes) {
			if (map.containsKey(mimeType)) {
				String format = map.get(mimeType);
				cache.put(acceptHeader, format);
				return format;
			}
		}

		return mimeTypes[0];
	}

	private static class Cache extends LinkedHashMap<String, String> {
		@Override
		protected boolean removeEldestEntry(java.util.Map.Entry<String, String> eldest) {
			return this.size() > 100;
		}
	}

	private static class MimeType implements Comparable<MimeType> {
		String type;
		double qualifier;

		public MimeType(String type, double qualifier) {
			this.type = type;
			this.qualifier = qualifier;
		}

		public int compareTo(MimeType mime) {
			// reverse order
			return Double.compare(mime.qualifier, this.qualifier);
		}

		public String getType() {
			return type;
		}
	}

	String[] getOrderedMimeTypes(String acceptHeader) {
		String[] types = acceptHeader.split(",");

		if (types.length == 0) {
			if (types[0].contains(";")) {
				return new String[] { types[0].substring(0, types[0].indexOf(';')) };
			}
			return new String[] { types[0] };
		}

		List<MimeType> mimes = new ArrayList<MimeType>();
		for (String string : types) {
			if (string.contains("*/*")) {
				mimes.add(new MimeType("html", 1));
				continue;
			} else if (string.contains(";")) {
				String type = string.substring(0, string.indexOf(';'));
				double qualifier = 1;
				if (string.contains("q=")) {
					Matcher matcher = Pattern.compile("\\s*q=(.+)\\s*").matcher(string);
					matcher.find();
					String value = matcher.group(1);
					qualifier = Double.parseDouble(value);
				}

				mimes.add(new MimeType(type, qualifier));
			} else {
				mimes.add(new MimeType(string, 1));
			}
		}

		Collections.sort(mimes);
		String[] orderedTypes = new String[mimes.size()];
		for (int i = 0; i < mimes.size(); i++) {
			orderedTypes[i] = mimes.get(i).getType().trim();
		}

		return orderedTypes;
	}
}
