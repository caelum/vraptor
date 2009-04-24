package br.com.caelum.vraptor.view;

import br.com.caelum.vraptor.ioc.Container;

/**
 * The default implementation of LogicResult.
 * 
 * @author Guilherme Silveira
 */
public class DefaultLogicResult implements LogicResult {

    private final Container container;

    public DefaultLogicResult(Container container) {
        this.container = container;
    }

    public <T> T redirectTo(Class<T> type) {
        return container.instanceFor(type);
    }

}
