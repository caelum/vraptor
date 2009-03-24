package br.com.caelum.vraptor.core;

import java.io.IOException;

/**
 * A request execution process.
 * 
 * @author Guilherme Silveira
 */
public interface RequestExecution {

    /**
     * Executes this method.
     */
    void execute(VRaptorRequest request) throws IOException;
    
}
