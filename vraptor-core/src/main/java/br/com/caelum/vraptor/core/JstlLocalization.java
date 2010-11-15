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
package br.com.caelum.vraptor.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ioc.RequestScoped;

/**
 * The default implementation of bundle provider uses jstl's api to access user
 * information on the bundle to be used.
 *
 * @author Guilherme Silveira
 */
@RequestScoped
public class JstlLocalization implements Localization {


	private static final Logger logger = LoggerFactory.getLogger(JstlLocalization.class);

    private static final String DEFAULT_BUNDLE_NAME = "messages";

    private final RequestInfo request;

    private ResourceBundle bundle;

    public JstlLocalization(RequestInfo request) {
        this.request = request;
    }

    public ResourceBundle getBundle() {
        if (this.bundle == null) {
            Locale locale = getLocale();
            Object bundle = get(Config.FMT_LOCALIZATION_CONTEXT);
            if (bundle instanceof String || bundle == null) {
				String baseName = (String) bundle;
	            if (baseName == null) {
	                baseName = DEFAULT_BUNDLE_NAME;
	            }
	            try {
					this.bundle = new SafeResourceBundle(ResourceBundle.getBundle(baseName, locale));
				} catch (MissingResourceException e) {
					logger.debug("couldn't find message bundle, creating an empty one");
					this.bundle = new SafeResourceBundle(createEmptyBundle());
				}
            } else if (bundle instanceof LocalizationContext) {
            	LocalizationContext context = (LocalizationContext) bundle;
            	this.bundle = context.getResourceBundle();
            } else {
            	logger.warn("Can't handle bundle: " + bundle + ". Please report this bug. Using an empty bundle");
            	this.bundle = new SafeResourceBundle(createEmptyBundle());
            }
        }
        return this.bundle;
    }
    


	private ResourceBundle createEmptyBundle() {
		try {
			return new PropertyResourceBundle(new ByteArrayInputStream(new byte[0]));
		} catch (IOException e) {
			logger.warn("It shouldn't happen. Please report this bug", e);
			return null;
		}
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
        try {
            String content = getBundle().getString(key);
            return MessageFormat.format(content, (Object[]) parameters);
        } catch (MissingResourceException e) {
            return "???" + key + "???";
        }
    }

}
