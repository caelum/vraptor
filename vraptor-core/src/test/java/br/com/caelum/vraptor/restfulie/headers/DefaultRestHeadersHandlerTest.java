package br.com.caelum.vraptor.restfulie.headers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.restfulie.Restfulie;
import br.com.caelum.vraptor.restfulie.hypermedia.HypermediaResource;
import br.com.caelum.vraptor.restfulie.relation.Relation;
import br.com.caelum.vraptor.restfulie.resource.Cacheable;

public class DefaultRestHeadersHandlerTest {
	
	@Mock private HttpServletResponse response;

	private DefaultRestHeadersHandler handler;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.handler = new DefaultRestHeadersHandler(this.response);
	}
	
	@Test
	public void whenResourceIsSimpleDoNothing() {
		handler.handle(mock(HypermediaResource.class));
	}
	
	class CacheableOrder implements Cacheable, HypermediaResource{

		public int getMaximumAge() {
			return 150;
		}

		public List<Relation> getRelations(Restfulie control) {
			return null;
		}
		
	}

	@Test
	public void whenResourceIsCacheableAddMaxAge() {
		CacheableOrder cacheable = new CacheableOrder();
		handler.handle(cacheable);
		verify(response).addHeader("Cache-control","max-age=150");
	}

	@Test
	public void whenResourceIsCacheableAddMaxAge() {
		CacheableOrder cacheable = new CacheableOrder();
		handler.handle(cacheable);
		// TODO add link headers
	}

}
