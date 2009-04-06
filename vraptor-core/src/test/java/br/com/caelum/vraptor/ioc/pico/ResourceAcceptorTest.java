package br.com.caelum.vraptor.ioc.pico;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.interceptor.VRaptorMatchers;

public class ResourceAcceptorTest {

    private List<br.com.caelum.vraptor.resource.Resource> resources;
    private ResourceAcceptor acceptor;

    @Before
    public void setup() {
        this.resources = new ArrayList<br.com.caelum.vraptor.resource.Resource>();
        this.acceptor = new ResourceAcceptor(resources);
    }

    @Test
    public void shouldAcceptResourcesAnnotatedWithResourceAnnotation() {
        acceptor.analyze(ResourceAnnotated.class);
        assertThat(resources, hasItem(VRaptorMatchers.resource(ResourceAnnotated.class)));
    }

    @Test
    public void ignoresNonAnnotatedResources() {
        acceptor.analyze(ResourceNotAnnotated.class);
        assertThat(resources, not(hasItem(VRaptorMatchers.resource(ResourceAnnotated.class))));
    }

    @Resource
    class ResourceAnnotated {
    }

    class ResourceNotAnnotated {
    }

}
