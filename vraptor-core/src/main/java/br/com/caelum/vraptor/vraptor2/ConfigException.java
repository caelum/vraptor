package br.com.caelum.vraptor.vraptor2;

import br.com.caelum.vraptor.VRaptorException;

public class ConfigException extends VRaptorException {
    private static final long serialVersionUID = -6464143621672701839L;

    public ConfigException(String msg, Throwable e) {
        super(msg, e);
    }

    public ConfigException(Throwable e) {
        super(e);
    }

    public ConfigException(String msg) {
        super(msg);
    }
}
