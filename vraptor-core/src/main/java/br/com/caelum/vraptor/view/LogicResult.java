
package br.com.caelum.vraptor.view;

import br.com.caelum.vraptor.View;

/**
 * Logic result allows logics to redirect to another logic by invoking the
 * method itself, in a very typesafe way.
 *
 * @author Guilherme Silveira
 */
public interface LogicResult extends View {

    /**
     * <p>Returns an instance of the given (pre-registered) logic.</p>
     * <p>
     * Any method called in the returned instance will cause a server side redirect (forward) to the called action.
     * </p>
     */
    <T> T forwardTo(Class<T> type);

    /**
     * <p>Returns an instance of that (pre-registered) logic.</p>
     * <p>
     * Any method called in the returned instance will cause a client side redirect to the called action, with the
     * given parameters as HTTP parameters.
     * </p>
     */
    <T> T redirectTo(Class<T> type);

}
