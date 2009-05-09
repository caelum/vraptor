package br.com.caelum.vraptor.ioc.spring;

/**
 * @author Fabio Kung
 */
public class MissingConfigurationException extends IllegalStateException {
    private static final long serialVersionUID = -3501632236065591624L;

    public MissingConfigurationException(String s) {
        super(s);
    }

    public MissingConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingConfigurationException(Throwable cause) {
        super(cause);
    }
}
