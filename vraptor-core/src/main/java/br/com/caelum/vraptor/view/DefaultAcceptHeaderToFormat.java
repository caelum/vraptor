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

import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Sets.newTreeSet;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.util.LRUCache;

import com.google.common.base.Function;

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

	private static final Map<String, String> acceptToFormatCache = Collections.synchronizedMap(new LRUCache<String, String>(100));
	private static final String DEFAULT_FORMAT = "html";
	private static final double DEFAULT_QUALIFIER_VALUE = 0.01;
	protected final Map<String, String> mimeToFormat;

	public DefaultAcceptHeaderToFormat() {
		mimeToFormat = new ConcurrentHashMap<String, String>();
		mimeToFormat.put("text/html", "html");
		mimeToFormat.put("application/json", "json");
		mimeToFormat.put("application/xml", "xml");
		mimeToFormat.put("text/xml", "xml");
		mimeToFormat.put("xml", "xml");
	}

	public String getFormat(String acceptHeader) {
		if (acceptHeader == null || acceptHeader.trim().equals("")) {
			return DEFAULT_FORMAT;
		}

		if (acceptHeader.contains(DEFAULT_FORMAT)) {
			// HACK! Opera may send "application/json, text/html, */*" and this should return html.
			return DEFAULT_FORMAT;
		}

		if (acceptToFormatCache.containsKey(acceptHeader)) {
			return acceptToFormatCache.get(acceptHeader);
		}

		return chooseMimeType(acceptHeader);
	}

	private String chooseMimeType(String acceptHeader) {
		String[] mimeTypes = getOrderedMimeTypes(acceptHeader);

		for (String mimeType : mimeTypes) {
			if (mimeToFormat.containsKey(mimeType)) {
				String format = mimeToFormat.get(mimeType);
				acceptToFormatCache.put(acceptHeader, format);
				return format;
			}
		}

		return mimeTypes[0];
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

		@Override
		public String toString() {
			return type;
		}
	}

	String[] getOrderedMimeTypes(String acceptHeader) {
		String[] types = acceptHeader.split(",");

		if (types.length == 0) {
			return new String[] { types[0].split(";")[0] };
		}

		Set<MimeType> mimes = newTreeSet();
		for (String string : types) {
			mimes.add(convertToMimeType(string));
		}

		return transform(mimes, mimeType()).toArray(new String[mimes.size()]);
	}

	private Function<MimeType, String> mimeType() {
		return new Function<MimeType, String>() {
			public String apply(MimeType mime) {
				return mime.getType().trim();
			}
		};
	}

	private MimeType convertToMimeType(String string) {
		if (string.contains("*/*")) {
			return new MimeType("text/html", DEFAULT_QUALIFIER_VALUE);
		} else if (string.contains(";")) {
			String type = string.substring(0, string.indexOf(';'));
			return new MimeType(type, extractQualifier(string));
		}
		return new MimeType(string, 1);
	}

	private double extractQualifier(String string) {
		double qualifier = DEFAULT_QUALIFIER_VALUE;
		if (string.contains("q=")) {
			Matcher matcher = Pattern.compile("\\s*q=(.+)\\s*").matcher(string);
			matcher.find();
			String value = matcher.group(1);
			qualifier = Double.parseDouble(value);
		}
		return qualifier;
	}
}
