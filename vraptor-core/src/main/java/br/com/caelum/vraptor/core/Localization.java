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

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * A i18n messages provider.
 * 
 * @author Guilherme Silveira
 */
public interface Localization {

    /**
     * Get the locale for current request. Locale is found by looking into request, session and application scoped (in
     * this order).
     * 
     * @return
     */
    Locale getLocale();

    /**
     * Get the fallback locale for current request. Locale is found by looking into request, session and application
     * scoped (in this order).
     * 
     * @return
     */
    Locale getFallbackLocale();

    /**
     * Returns a formated message or '???key???' if the key was not found.
     */
    String getMessage(String key, Object... parameters);

    /**
     * Get the resource bundle for current locale. If the resource bundle is not found, an empty resource bundle is
     * returned, to avoid {@link MissingResourceException}.
     * 
     * @see #getLocale()
     * @return
     */
    ResourceBundle getBundle();

}
