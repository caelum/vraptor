package br.com.caelum.vraptor.http;

import java.util.Set;

/**
 * A list of request parameters.<br>
 * Typical implementations would include HttpServletRequest-based,
 * MultiPart-based, XML data posted, Json data posted and so on.
 * 
 * @author Guilherme Silveira
 */
public interface RequestParameters {

    public Set<String> getNames();

    public String[] get(String key);

    public void set(String key, String[] values);

}
