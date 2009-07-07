package br.com.caelum.vraptor.ioc.pico;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.resource.DefaultResourceClass;

import java.util.Arrays;

/**
 * @author Fabio Kung
 */
public class ResourceRegistrarTest {

    private Mockery mockery;
    private ResourceRegistrar registrar;
    private Router registry;
    private Scanner scanner;

    @Before
    public void setup() {
        mockery = new Mockery();
        registry = mockery.mock(Router.class);
        scanner = mockery.mock(Scanner.class);
        this.registrar = new ResourceRegistrar(registry);
    }

    @Test
    public void shouldRegisterResourcesAnnotatedWithResource() {
        mockery.checking(new Expectations() {
            {
                one(scanner).getTypesWithAnnotation(Resource.class);
                will(returnValue(Arrays.asList(ResourceAnnotated.class)));
                one(registry).register(new DefaultResourceClass(ResourceAnnotated.class));
            }
        });
        registrar.registerFrom(scanner);
        mockery.assertIsSatisfied();
    }

    @Resource
    class ResourceAnnotated {
    }
}
