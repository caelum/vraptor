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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.TwoWayConverter;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.util.StringUtils;

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
	private final Converters converters;
    private final Evaluator evaluator;

    public DefaultParametersControl(String originalPattern, Map<String, String> parameterPatterns, Converters converters, Evaluator evaluator) {
		this.originalPattern = originalPattern;
		this.converters = converters;
		this.pattern = compilePattern(originalPattern, parameterPatterns);
        this.evaluator = evaluator;
	}

	public DefaultParametersControl(String originalPattern, Converters converters, Evaluator evaluator) {
		this(originalPattern, Collections.<String, String>emptyMap(), converters, evaluator);
	}

	private Pattern compilePattern(String originalPattern, Map<String, String> parameterPatterns) {
		Map<String, String> parameters = new HashMap<String, String>(parameterPatterns);
		Matcher matcher = Pattern.compile("\\{((?=[^\\{]+?[\\{])[^\\}]+?\\}|[^\\}]+?)\\}").matcher(originalPattern);
		while (matcher.find()) {
			String value = matcher.group(1);
			String defaultPattern = value.matches("^[^:]+\\*$")? ".*" : value.indexOf(":") >= 0 ? value.replaceAll("^[^\\:]+?:", "") : "[^/]*";
			if (!parameters.containsKey(value)) {
				parameters.put(value, defaultPattern);
			}
			this.parameters.add(value.replaceAll("(\\:.*|\\*)$", ""));
		}
		String patternUri = originalPattern;
		patternUri = patternUri.replaceAll("/\\*", "/.*");
		for (Entry<String, String> parameter : parameters.entrySet()) {
			patternUri = patternUri.replace("{" + parameter.getKey() + "}", "(" + parameter.getValue() + ")");
		}
		
		if (logger.isDebugEnabled()) {
            logger.debug("For {} retrieved {} with {}", new Object[] { originalPattern, patternUri, parameters });
		}
		return Pattern.compile(patternUri);
	}

	public String fillUri(String[] paramNames, Object... paramValues) {
		if (paramNames.length != paramValues.length) {
			throw new IllegalArgumentException("paramNames must have the same length as paramValues. Names: " + Arrays.toString(paramNames) + " Values: " + Arrays.toString(paramValues));
		}
	
		String[] splittedPatterns = StringUtils.extractParameters(originalPattern);
		
		String base = originalPattern;
		for (int i=0; i<parameters.size(); i++) {
			String key = parameters.get(i);
			Object param = selectParam(key, paramNames, paramValues);
			Object result = evaluator.get(param, key);
			if (result != null) {
				Class<?> type = result.getClass();
				if (converters.existsTwoWayFor(type)) {
					TwoWayConverter converter = converters.twoWayConverterFor(type);
					result = converter.convert(result);
				}
			}

			base = base.replace("{" + splittedPatterns[i] + "}", result == null ? "" : result.toString());
		}
		
		return base.replaceAll("\\.\\*", "");
	}

	private Object selectParam(String key, String[] paramNames, Object[] paramValues) {
		for (int i = 0; i < paramNames.length; i++) {
			if (key.matches("^" + paramNames[i] + "(\\..*|$)")) {
				return paramValues[i];
			}
		}
		return null;
	}

	public boolean matches(String uri) {
		return pattern.matcher(uri).matches();
	}

	public void fillIntoRequest(String uri, MutableRequest request) {
		Matcher m = pattern.matcher(uri);
		m.matches();
		for (int i = 1; i <= m.groupCount(); i++) {
			String name = parameters.get(i - 1);
			try {
				request.setParameter(name, URLDecoder.decode(m.group(i), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				logger.error("Error when decoding url parameters");
			}
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
