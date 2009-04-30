package br.com.caelum.vraptor.core;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.jsp.jstl.core.Config;

import br.com.caelum.vraptor.ioc.RequestScoped;

/**
 * The default implementation of bundle provider uses jstl's api to access user
 * information on the bundle to be used.
 * 
 * @author Guilherme Silveira
 */
@RequestScoped
public class JstlLocalization implements Localization {

	private static final String DEFAULT_BUNDLE_NAME = "messages";

	private final RequestInfo request;

	private ResourceBundle bundle;

	public JstlLocalization(RequestInfo request) {
		this.request = request;
	}

	public ResourceBundle getBundle() {
		if (this.bundle == null) {
			Locale locale = getLocale();
			String baseName = (String) get(Config.FMT_LOCALIZATION_CONTEXT);
			if (baseName == null) {
				baseName = DEFAULT_BUNDLE_NAME;
			}
			this.bundle = ResourceBundle.getBundle(baseName, locale);
		}
		return this.bundle;
	}

	public Locale getLocale() {
		return localeFor(Config.FMT_LOCALE);
	}

	public Locale getFallbackLocale() {
		return localeFor(Config.FMT_FALLBACK_LOCALE);
	}

	private Locale localeFor(String key) {
		Object localeValue = get(key);
		if (localeValue instanceof String) {
			return stringToLocale((String) localeValue);
		}
		if (localeValue != null) {
			return (Locale) localeValue;
		}
		return request.getRequest().getLocale();
	}

	/**
	 * Extracted from XStream project, copyright Joe Walnes
	 */
	private Locale stringToLocale(String str) {
		int[] underscorePositions = underscorePositions(str);
		if (underscorePositions[0] == -1) {
			return new Locale(str);
		}
		String language = str.substring(0, underscorePositions[0]);
		if (underscorePositions[1] == -1) {
			return new Locale(language, str.substring(underscorePositions[0] + 1));
		}
		return new Locale(language, str.substring(underscorePositions[0] + 1, underscorePositions[1]), str
				.substring(underscorePositions[1] + 1));
	}

	private int[] underscorePositions(String str) {
		int[] result = new int[2];
		for (int i = 0; i < result.length; i++) {
			int last = i == 0 ? 0 : result[i - 1];
			result[i] = str.indexOf('_', last + 1);
		}
		return result;
	}

	private Object get(String key) {
		Object value = Config.get(request.getRequest(), key);
		if (value != null) {
			return value;
		}
		value = Config.get(request.getRequest().getSession(), key);
		if (value != null) {
			return value;
		}
		value = Config.get(request.getServletContext(), key);
		if (value != null) {
			return value;
		}
		return request.getServletContext().getInitParameter(key);
	}

	public String getMessage(String key, String... parameters) {
		if(!getBundle().containsKey(key)) {
			return "???" + key + "???";
		}
		String content = getBundle().getString(key);
		return MessageFormat.format(content, parameters);
	}

}
