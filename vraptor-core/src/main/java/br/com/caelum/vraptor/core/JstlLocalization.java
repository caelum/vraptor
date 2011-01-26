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

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.util.EmptyBundle;

import com.google.common.base.Strings;

/**
 * The default implementation of bundle provider uses JSTL's api to access user information on the bundle to be used.
 *
 * @author Guilherme Silveira
 * @author Otávio Scherer Garcia
 */
@RequestScoped
public class JstlLocalization
    implements Localization {

    private static final Logger logger = LoggerFactory.getLogger(JstlLocalization.class);

    private static final String DEFAULT_BUNDLE_NAME = "messages";

    private final RequestInfo request;
    private ResourceBundle bundle;

    public JstlLocalization(RequestInfo request) {
        this.request = request;
    }

    public ResourceBundle getBundle() {
        if (bundle == null) {
            initializeBundle();
        }

        return bundle;
    }

    /**
     * Find the bundle. If the bundle is not found, return an empty for safety operations (avoid
     * {@link MissingResourceException}.
     */
    private void initializeBundle() {
        Object bundle = findByKey(Config.FMT_LOCALIZATION_CONTEXT);
        ResourceBundle unsafe = extractUnsafeBundle(bundle);

        this.bundle = new SafeResourceBundle(unsafe);
    }

	private ResourceBundle extractUnsafeBundle(Object bundle) {
        if (bundle instanceof String || bundle == null) {
            String baseName = (bundle == null) ? DEFAULT_BUNDLE_NAME : bundle.toString();

            try {
                return ResourceBundle.getBundle(baseName, getLocale());
            } catch (MissingResourceException e) {
                logger.debug("couldn't find message bundle, creating an empty one");
                return new EmptyBundle();
            }

        }
        if (bundle instanceof LocalizationContext) {
            return ((LocalizationContext) bundle).getResourceBundle();
        }
        logger.warn("Can't handle bundle {}. Please report this bug. Using an empty bundle", bundle);
        return new EmptyBundle();
	}

    public Locale getLocale() {
        return localeFor(Config.FMT_LOCALE);
    }

    public Locale getFallbackLocale() {
        return localeFor(Config.FMT_FALLBACK_LOCALE);
    }

    private Locale localeFor(String key) {
        Object localeValue = findByKey(key);

        if (localeValue instanceof String) {
            return findLocalefromString((String) localeValue);
        } else if (localeValue instanceof Locale) {
            return (Locale) localeValue;
        }

        return request.getRequest().getLocale();
    }

    /**
     * Looks up a configuration variable in the request, session and application scopes. If none is found, return by
     * {@link ServletContext#getInitParameter(String)} method.
     *
     * @param key
     * @return
     */
    private Object findByKey(String key) {
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

    public String getMessage(String key, Object... parameters) {
        try {
            String content = getBundle().getString(key);
            return MessageFormat.format(content, parameters);
        } catch (MissingResourceException e) {
            return "???" + key + "???";
        }
    }

    /**
     * Converts a locale string to {@link Locale}. If the input string is null or empty, return an empty {@link Locale}.
     *
     * @param str
     * @return
     */
    private Locale findLocalefromString(String str) {
        if (!Strings.isNullOrEmpty(str)) {
            String[] arr = str.split("_");
            if (arr.length == 1) {
                return new Locale(arr[0]);
            } else if (arr.length == 2) {
                return new Locale(arr[0], arr[1]);

            } else {
                return new Locale(arr[0], arr[1], arr[2]);
            }
        }

        return null;
    }
}
