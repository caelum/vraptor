
package br.com.caelum.vraptor.http.route;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
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

	public String extract(String paramName, String from) {
		Matcher matcher = pattern.matcher(from);
		matcher.matches();
		return matcher.group(parameters.indexOf(paramName)+1);
	}

}
