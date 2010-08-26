package br.com.caelum.vraptor.restfulie.headers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.restfulie.hypermedia.HypermediaResource;
import br.com.caelum.vraptor.restfulie.relation.RelationBuilder;
import br.com.caelum.vraptor.restfulie.resource.Cacheable;
import br.com.caelum.vraptor.restfulie.resource.RestfulEntity;

public class DefaultRestHeadersHandlerTest {

	@Mock private HttpServletResponse response;
	@Mock private RestDefaults defaults;

	private DefaultRestHeadersHandler handler;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.handler = new DefaultRestHeadersHandler(this.response, defaults);
	}

	@Test
	public void whenResourceIsSimpleDoNothing() {
		handler.handle(mock(HypermediaResource.class));
	}

	class CacheableOrder implements Cacheable, HypermediaResource{

		public int getMaximumAge() {
			return 150;
		}

		public void configureRelations(RelationBuilder builder) {
		}
	}

	@Test
	public void whenResourceIsCacheableAddMaxAge() {
		CacheableOrder cacheable = new CacheableOrder();
		handler.handle(cacheable);
		verify(response).addHeader("Cache-control","max-age=150");
	}

	@Test
	public void whenResourceHasLinksAddThemToTheHeader() {
		CacheableOrder cacheable = new CacheableOrder();
		handler.handle(cacheable);
		// TODO add link headers
	}

	@Test
	public void whenNotExplicitlyRestfulEntityUseDefaultEtagAndLastModified() {
		CacheableOrder cacheable = new CacheableOrder();

		Calendar date = Calendar.getInstance();
		when(defaults.getLastModifiedFor(cacheable)).thenReturn(date);
		when(defaults.getEtagFor(cacheable)).thenReturn("custom etag");

		handler.handle(cacheable);
		verify(response).addHeader("ETag", "custom etag");
		verify(response).setDateHeader("Last-modified", date.getTimeInMillis());
	}

	class CacheableOrderEntity implements RestfulEntity, HypermediaResource {

		private Calendar date = Calendar.getInstance();

		public void configureRelations(RelationBuilder builder) {
		}

		public String getEtag() {
			return "MY ETAG";
		}

		public Calendar getLastModified() {
			return date;
		}

		public int getMaximumAge() {
			return 0;
		}

	}

	@Test
	public void whenRestfulEntityIsHandledShouldUseItsValues() {
		CacheableOrderEntity cacheable = new CacheableOrderEntity();

		handler.handle(cacheable);
		verify(response).addHeader("ETag", "MY ETAG");
		verify(response).setDateHeader("Last-modified", cacheable.date.getTimeInMillis());
	}
}
