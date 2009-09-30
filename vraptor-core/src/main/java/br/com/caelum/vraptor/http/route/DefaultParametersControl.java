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

package br.com.caelum.vraptor.http.route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.eval.Evaluator;
import br.com.caelum.vraptor.http.MutableRequest;

/**
 * Default implementation of parameters control on uris.
 *
 * @author guilherme silveira
 */
public class DefaultParametersControl implements ParametersControl {

	private final Logger logger = LoggerFactory.getLogger(DefaultParametersControl.class);
	private final List<String> parameters = new ArrayList<String>();
	private final Pattern pattern;
	private final String originalPattern;

	public DefaultParametersControl(String originalPattern, Map<String, String> parameterPatterns) {
		this.originalPattern = originalPattern;
		this.pattern = compilePattern(originalPattern, parameterPatterns);
	}

	public DefaultParametersControl(String originalPattern) {
		this(originalPattern, Collections.<String, String>emptyMap());
	}

	private Pattern compilePattern(String originalPattern, Map<String, String> parameterPatterns) {
		Map<String, String> parameters = new HashMap<String, String>(parameterPatterns);
		Matcher matcher = Pattern.compile("\\{([^\\}]+?)\\}").matcher(originalPattern);
		while (matcher.find()) {
			String value = matcher.group(1);
			String defaultPattern = value.endsWith("*")? ".*" : "[^/]*";
			if (!parameters.containsKey(value)) {
				parameters.put(value, defaultPattern);
			}
			this.parameters.add(value.replace("*", ""));
		}
		String patternUri = originalPattern;
		for (Entry<String, String> parameter : parameters.entrySet()) {
			patternUri = patternUri.replace("{" + parameter.getKey() + "}", "(" + parameter.getValue() + ")");
		}
		logger.debug("For " + originalPattern + " retrieved " + patternUri + " with " + parameters);
		return Pattern.compile(patternUri);
	}

	public String fillUri(Object params) {
		String base = originalPattern.replaceAll("\\.\\*", "");
		for (String key : parameters) {
			Object result = new Evaluator().get(params, key);
			base = base.replace("{" + key + "}", result == null ? "" : result.toString());
		}
		return base;
	}

	public boolean matches(String uri) {
		return pattern.matcher(uri).matches();
	}

	public void fillIntoRequest(String uri, MutableRequest request) {
		Matcher m = pattern.matcher(uri);
		m.matches();
		for (int i = 1; i <= m.groupCount(); i++) {
			String name = parameters.get(i - 1);
			request.setParameter(name, m.group(i));
		}
	}

	public String apply(String[] values) {
		String regex = "\\{.*?\\}";
		String result = this.originalPattern;
		for (int i = 0; i < values.length; i++) {
			result = result.replaceFirst(regex, values[i].replaceAll("\\$", "\\\\\\$"));
		}
		return result.toString();
	}

}
