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
package br.com.caelum.vraptor.converter;

import java.util.Locale;

import br.com.caelum.vraptor.core.RequestInfo;

public class JstlWrapper {

    public Object find(RequestInfo request, String name) {
        if (request.getRequest().getAttribute(name + ".request")!=null) {
            return request.getRequest().getAttribute(name + ".request");
        } else if (request.getRequest().getSession().getAttribute(name + ".session")!=null) {
            return request.getRequest().getSession().getAttribute(name + ".session");
        } else if (request.getServletContext().getAttribute(name + ".application")!=null) {
            return request.getServletContext().getAttribute(name + ".application");
        }
        return request.getServletContext().getInitParameter(name);
    }

    public Locale findLocale(RequestInfo request) {
        Object obj = find(request, "javax.servlet.jsp.jstl.fmt.locale");
        if(obj instanceof String) {
            return stringToLocale((String) obj);
        } else if (obj != null){
            return (Locale) obj;
        } else {
            return request.getRequest().getLocale();
        }
    }

    /**
     * Extracted from XStream project, copyright Joe Walnes
     * @param str the string to convert
     * @return  the locale
     */
    public Locale stringToLocale(String str) {
        int[] underscorePositions = underscorePositions(str);
        String language, country, variant;
        if (underscorePositions[0] == -1) { // "language"
            language = str;
            country = "";
            variant = "";
        } else if (underscorePositions[1] == -1) { // "language_country"
            language = str.substring(0, underscorePositions[0]);
            country = str.substring(underscorePositions[0] + 1);
            variant = "";
        } else { // "language_country_variant"
            language = str.substring(0, underscorePositions[0]);
            country = str.substring(underscorePositions[0] + 1, underscorePositions[1]);
            variant = str.substring(underscorePositions[1] + 1);
        }
        return new Locale(language, country, variant);
    }

    private int[] underscorePositions(String in) {
        int[] result = new int[2];
        for (int i = 0; i < result.length; i++) {
            int last = i == 0 ? 0 : result[i - 1];
            result[i] = in.indexOf('_', last + 1);
        }
        return result;
    }

}
