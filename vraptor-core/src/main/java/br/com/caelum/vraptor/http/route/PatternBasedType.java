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
package br.com.caelum.vraptor.http.route;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PatternBasedType {

	private final List<String> parameters = new ArrayList<String>();
	private final Pattern pattern;
	private String originalPattern;
	private String finalPattern;

	public PatternBasedType(String pattern) {
		this.originalPattern = pattern;
		String finalUri = "";
		String patternUri = "";
		String paramName = "";
		// not using stringbuffer because this is only run in startup
		boolean ignore = false;
		for (int i = 0; i < pattern.length(); i++) {
			if (pattern.charAt(i) == '{') {
				ignore = true;
				patternUri += "(";
				continue;
			} else if (pattern.charAt(i) == '}') {
				ignore = false;
				finalUri += ".*";
				patternUri += ".*)";
				parameters.add(paramName);
				paramName = "";
				continue;
			} else if (!ignore) {
				patternUri += pattern.charAt(i);
				finalUri += pattern.charAt(i);
			} else {
				paramName += pattern.charAt(i);
			}
		}
		if(ignore) {
			throw new IllegalRouteException("Illegal route contains invalid pattern: " + originalPattern);
		}
		this.finalPattern = patternUri;
		this.pattern = Pattern.compile(patternUri);
	}

	public boolean matches(String name) {
		return pattern.matcher(name).matches();
	}

	public String apply(String key, String value) {
		return originalPattern.replace("{"+key+"}", value);
	}

}
