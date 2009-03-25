package br.com.caelum.vraptor.ioc;

import br.com.caelum.vraptor.core.VRaptorRequest;

public interface ContainerProvider {

    Container provide(VRaptorRequest vraptorRequest);

    void stop();
    
    void start();

}
