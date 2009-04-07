package br.com.caelum.vraptor.ioc.spring;

import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.GenericContainerTest;
import br.com.caelum.vraptor.ioc.pico.PicoProvider;
import org.jmock.Expectations;

public class RegisterAllComponentsTest extends GenericContainerTest {

    protected ContainerProvider getProvider() {
        return new SpringProvider();
    }

}