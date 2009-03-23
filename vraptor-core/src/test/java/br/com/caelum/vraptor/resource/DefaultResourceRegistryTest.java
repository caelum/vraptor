package br.com.caelum.vraptor.resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;

import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Path;

public class DefaultResourceRegistryTest {

	private Mockery mockery;
	private DefaultResourceRegistry registry;

	@Before
	public void setup() {
		this.mockery = new Mockery();
		this.registry = new DefaultResourceRegistry(null);
	}

	@Test
	public void testReturnsResourceIfFound() throws SecurityException,
			NoSuchMethodException {
		ResourceMethod expected = mockery.mock(ResourceMethod.class);
		final Resource resource = mockery.mock(Resource.class);
		mockery.checking(new Expectations() {
			{
				one(resource).getType();
				will(returnValue(Clients.class));
			}
		});
		registry.register(Arrays.asList(resource));
		ResourceMethod method = registry.gimmeThis("/clients", "POST");
		assertThat(method.getMethod(), is(equalTo(Clients.class
				.getMethod("add"))));
		mockery.assertIsSatisfied();
	}

	public static class Clients {
		@Path("/clients")
		public void add() {

		}
	}

	@Test
	public void testReturnsNullIfResourceNotFound() {
		ResourceMethod method = registry.gimmeThis("unknown_id", "POST");
		assertThat(method, is(Matchers.nullValue()));
	}

}
