/***
 *
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
