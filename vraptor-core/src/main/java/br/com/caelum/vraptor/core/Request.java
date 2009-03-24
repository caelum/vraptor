package br.com.caelum.vraptor.core;

import java.io.IOException;

import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * A request execution process.
 * 
 * @author Guilherme Silveira
 */
public interface Request {

    /**
     * Executes this method.
     */
    void execute(ResourceMethod method) throws IOException;

}
