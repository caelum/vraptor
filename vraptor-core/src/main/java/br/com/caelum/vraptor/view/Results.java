
package br.com.caelum.vraptor.view;

/**
 * Some common results for most web based logics.
 *
 * @author Guilherme Silveira
 */
public class Results {

    /**
     * Offers server side forward and include for web pages.<br>
     * Should be used only with end results (not logics), otherwise you might
     * achieve the server-redirect-hell (f5 problem) issue.
     */
    public static Class<? extends PageResult> page() {
        return PageResult.class;
    }

    /**
     * Server and client side forward to another logic.
     */
    public static Class<LogicResult> logic() {
        return LogicResult.class;
    }

    /**
     * Uses an empty page.
     */
    public static Class<EmptyResult> nothing() {
    	return EmptyResult.class;
    }

    /**
     * Sends information through the HTTP protocol, like
     * status codes and header
     */
    public static Class<HttpResult> http() {
    	return HttpResult.class;
    }

}
