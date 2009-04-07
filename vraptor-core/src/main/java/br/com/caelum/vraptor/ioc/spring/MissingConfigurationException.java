package br.com.caelum.vraptor.ioc.spring;

/**
 * @author Fabio Kung
 */
public class MissingConfigurationException extends IllegalStateException {
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
