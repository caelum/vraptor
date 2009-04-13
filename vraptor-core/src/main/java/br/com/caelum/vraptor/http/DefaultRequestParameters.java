package br.com.caelum.vraptor.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

/**
 * The default request parameters implementation is backed by a http request and
 * a map allowing new values to be injected.
 * 
 * @author Guilherme Silveira
 */
public class DefaultRequestParameters implements RequestParameters {

    private final Map<String, String[]> names = new HashMap<String, String[]>();

    @SuppressWarnings("unchecked")
    public DefaultRequestParameters(HttpServletRequest request) {
        Map<String, Object> parameters = request.getParameterMap();
        for (String key : parameters.keySet()) {
            Object value = parameters.get(key);
            names.put(key, value.getClass().isArray() ? (String[]) value : new String[] { (String) value });
        }
    }

    public String[] get(String key) {
        return names.get(key);
    }

    public Set<String> getNames() {
        return names.keySet();
    }

    public void set(String key, String[] values) {
        names.put(key, values);
    }

}
