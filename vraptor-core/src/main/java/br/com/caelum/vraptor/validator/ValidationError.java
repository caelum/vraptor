package br.com.caelum.vraptor.validator;

import java.util.List;

/**
 * If some validation error occur, its encapsulated within an instance of
 * ValidationError, which is then throw and parsed.
 * 
 * @author Guilherme Silveira
 */
public class ValidationError extends Error {

    /**
     * Serialized id.
     */
    private static final long serialVersionUID = -1069844446293479183L;

    private final List<String> errors;

    public ValidationError(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }

}
