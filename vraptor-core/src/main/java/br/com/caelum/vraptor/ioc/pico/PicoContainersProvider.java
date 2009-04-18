package br.com.caelum.vraptor.ioc.pico;

import java.util.ArrayList;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Startable;

import br.com.caelum.vraptor.RegisterContainer;


public class PicoContainersProvider implements RegisterContainer{
    
    private List<Class<?>> applicationScoped = new ArrayList<Class<?>>();
    private List<Class<?>> sessionScoped = new ArrayList<Class<?>>();
    private List<Class<?>> requestScoped = new ArrayList<Class<?>>();
    private final MutablePicoContainer container;

    public PicoContainersProvider(MutablePicoContainer container) {
        this.container = container;
    }

    public void register(Class<?> type) {
        
    }

}
