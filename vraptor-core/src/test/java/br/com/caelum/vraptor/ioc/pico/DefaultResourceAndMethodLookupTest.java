package br.com.caelum.vraptor.ioc.pico;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Delete;
import br.com.caelum.vraptor.Head;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.VRaptorMockery;
import br.com.caelum.vraptor.resource.DefaultResourceAndMethodLookup;
import br.com.caelum.vraptor.resource.Resource;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class DefaultResourceAndMethodLookupTest {

    private VRaptorMockery mockery;
    private DefaultResourceAndMethodLookup lookuper;
    private Resource resource;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
        this.resource = mockery.resource(Clients.class);
        this.lookuper = new DefaultResourceAndMethodLookup(resource);
    }

    @Test
    public void findsTheCorrectAnnotatedMethodIfThereIsNoWebMethodAnnotationPresent() throws SecurityException, NoSuchMethodException {
        ResourceMethod method = lookuper.methodFor("/clients", "POST");
        assertThat(method.getMethod(), is(equalTo(Clients.class.getMethod("list"))));
        mockery.assertIsSatisfied();
    }

    @Test
    public void returnsNullIfMethodNotFound() {
        ResourceMethod method = lookuper.methodFor("/projects", "POST");
        assertThat(method, is(nullValue()));
        mockery.assertIsSatisfied();
    }

    static class Clients {
        @Path("/clients")
        public void list() {
        }
        @Path("/clients/remove")
        @Delete
        public void remove() {
        }
        @Path("/clients/head")
        @Head
        public void head() {
        }
        public void add() {
        }
    }

    @Test
    public void shouldIgnoreANonAnnotatedMethod() throws SecurityException, NoSuchMethodException {
        ResourceMethod method = lookuper.methodFor("/add", "POST");
        assertThat(method, is(Matchers.nullValue()));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldIgnoreAResourceWithTheWrongWebMethod() throws SecurityException, NoSuchMethodException {
        ResourceMethod method = lookuper.methodFor("/clients/remove", "POST");
        assertThat(method, is(Matchers.nullValue()));
        mockery.assertIsSatisfied();
    }
    @Test
    public void shouldAcceptAResultWithASpecificWebMethod() throws SecurityException, NoSuchMethodException {
        ResourceMethod method = lookuper.methodFor("/clients/head", "HEAD");
        assertThat(method.getMethod(), is(equalTo(Clients.class.getMethod("head"))));
        mockery.assertIsSatisfied();
    }

}
