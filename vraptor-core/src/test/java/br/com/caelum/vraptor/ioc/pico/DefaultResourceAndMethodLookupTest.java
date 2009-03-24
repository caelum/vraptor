package br.com.caelum.vraptor.ioc.pico;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.resource.DefaultResourceAndMethodLookup;
import br.com.caelum.vraptor.resource.Resource;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class DefaultResourceAndMethodLookupTest {

	private Mockery mockery;
	private DefaultResourceAndMethodLookup lookuper;
	private Resource resource;

	@Before
	public void setup() {
		this.mockery = new Mockery();
		this.resource = mockery.mock(Resource.class);
		mockery.checking(new Expectations() {
			{
				one(resource).getType();
				will(returnValue(Clients.class));
			}
		});
		this.lookuper = new DefaultResourceAndMethodLookup(resource);
	}

	@Test
	public void testFindsTheCorrectAnnotatedMethod() throws SecurityException, NoSuchMethodException {
		ResourceMethod method = lookuper.methodFor("/clients", "POST");
		assertThat(method.getMethod(), is(equalTo(Clients.class.getMethod("add"))));
		mockery.assertIsSatisfied();
	}

	@Test
	public void testReturnsNullIfMethodNotFound() {
		ResourceMethod method = lookuper.methodFor("/projects", "POST");
		assertThat(method, is(nullValue()));
		mockery.assertIsSatisfied();
	}

	static class Clients {
		@Path("/clients")
		public void add() {

		}
	}

}
