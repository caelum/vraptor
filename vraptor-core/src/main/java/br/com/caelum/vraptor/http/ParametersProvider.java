
package br.com.caelum.vraptor.http;

import java.util.List;
import java.util.ResourceBundle;

import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.Message;

/**
 * Provides all parameters required to invoke an specific java method.
 *
 * @author Guilherme Silveira
 */
public interface ParametersProvider {

    Object[] getParametersFor(ResourceMethod method, List<Message> errors, ResourceBundle bundle);

}
