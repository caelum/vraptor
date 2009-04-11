package br.com.caelum.vraptor.converter;

import java.util.Locale;

import br.com.caelum.vraptor.core.VRaptorRequest;

public class JstlWrapper {

    public Object find(VRaptorRequest request, String name) {
        if (request.getRequest().getAttribute(name + ".request")!=null) {
            return request.getRequest().getAttribute(name + ".request");
        } else if (request.getRequest().getSession().getAttribute(name + ".session")!=null) {
            return request.getRequest().getSession().getAttribute(name + ".session");
        } else if (request.getServletContext().getAttribute(name + ".application")!=null) {
            return request.getServletContext().getAttribute(name + ".application");
        }
        return request.getServletContext().getInitParameter(name);
    }

    public Locale findLocale(VRaptorRequest request) {
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
