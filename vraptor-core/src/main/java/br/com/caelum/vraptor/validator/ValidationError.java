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

    /**
     * We don't need stack traces for this exception. It is used only to control flow.
     * The default implementation for this method is extremely expensive.
     *
     * @return reference for this, without filling the stack trace
     */
    @Override
    public final Throwable fillInStackTrace() {
        return this;
    }
}
