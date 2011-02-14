package br.com.caelum.vraptor.proxy;

import org.objenesis.ObjenesisStd;

import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * Objenesis implementation for {@link InstanceCreator}.
 * 
 * @author Otávio Scherer Garcia
 * @since 3.3.1
 */
@ApplicationScoped
public class ObjenesisInstanceCreator
    implements InstanceCreator {

    @SuppressWarnings("unchecked")
    public <T> T instanceFor(Class<T> clazz) {
        return (T) new ObjenesisStd().newInstance(clazz);
    }

}
