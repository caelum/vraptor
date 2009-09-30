
package br.com.caelum.vraptor.core;

import br.com.caelum.vraptor.VRaptorException;

/**
 * A request execution process.
 * 
 * @author Guilherme Silveira
 */
public interface RequestExecution {

    /**
     * Executes this method.
     * @throws VRaptorException 
     */
    void execute() throws VRaptorException;
    
}
