package br.com.caelum.vraptor;

import br.com.caelum.vraptor.validator.Validations;

/**
 * A validator interface for vraptor3.<br>
 * Based on hamcrest, it allows you to assert for specific situations.
 * 
 * @author Guilherme Silveira
 */
public interface Validator {
    
    void checking(Validations rules);

}
