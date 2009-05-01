package br.com.caelum.vraptor.http;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.resource.ResourceRegistry;

public class StupidTranslatorTest {
	
	private Mockery mockery;
	private ResourceRegistry registry;
	private StupidTranslator translator;
	private HttpServletRequest request;
	private VRaptorRequest webRequest;

	@Before
	public void setup() {
		this.mockery = new Mockery();
		this.registry = mockery.mock(ResourceRegistry.class);
		this.translator = new StupidTranslator(registry);
		this.request = mockery.mock(HttpServletRequest.class);
        this.webRequest = new VRaptorRequest(request);
	}

    @Test
    public void handlesInclude() {
        
        final ResourceMethod expected = mockery.mock(ResourceMethod.class);
        
        mockery.checking(new Expectations(){{
            exactly(2).of(request).getAttribute(StupidTranslator.INCLUDE_REQUEST_URI); will(returnValue("/url"));
            one(request).getMethod(); will(returnValue("POST"));
            one(registry).gimmeThis("/url", "POST", webRequest); will(returnValue(expected));
        }});
        
        ResourceMethod resource = translator.translate(request);
        assertThat(resource, is(equalTo(expected)));
        mockery.assertIsSatisfied();
        
    }
	@Test
	public void testCanHandleTheCorrectMethod() {
		
		final ResourceMethod expected = mockery.mock(ResourceMethod.class);
		
		mockery.checking(new Expectations(){{
		    one(request).getAttribute(StupidTranslator.INCLUDE_REQUEST_URI); will(returnValue(null));
			one(request).getRequestURI(); will(returnValue("/url"));
			one(request).getMethod(); will(returnValue("POST"));
			one(registry).gimmeThis("/url", "POST",webRequest); will(returnValue(expected));
		}});
		
		ResourceMethod resource = translator.translate(request);
		assertThat(resource, is(equalTo(expected)));
		mockery.assertIsSatisfied();
		
	}
	@Test
	public void testCanHandleUrlIfRootContext() {
		
		final ResourceMethod expected = mockery.mock(ResourceMethod.class);
		
		mockery.checking(new Expectations(){{
            one(request).getAttribute(StupidTranslator.INCLUDE_REQUEST_URI); will(returnValue(null));
			one(request).getRequestURI(); will(returnValue("/url"));
			one(request).getMethod(); will(returnValue("GET"));
			one(registry).gimmeThis("/url", "GET",webRequest); will(returnValue(expected));
		}});
		
		ResourceMethod resource = translator.translate(request);
		assertThat(resource, is(equalTo(expected)));
		mockery.assertIsSatisfied();
		
	}
	
	@Test
	public void testCanHandleUrlIfNonRootContext() {
		
		final ResourceMethod expected = mockery.mock(ResourceMethod.class);
		
		mockery.checking(new Expectations(){{
            one(request).getAttribute(StupidTranslator.INCLUDE_REQUEST_URI); will(returnValue(null));
			one(request).getRequestURI(); will(returnValue("/custom_context/url"));
			one(request).getMethod(); will(returnValue("GET"));
			one(registry).gimmeThis("/url", "GET",webRequest); will(returnValue(expected));
		}});
		
		ResourceMethod resource = translator.translate(request);
		assertThat(resource, is(equalTo(expected)));
		mockery.assertIsSatisfied();
		
	}
	
	@Test
	public void testCanHandleUrlIfPlainRootContext() {
		
		final ResourceMethod expected = mockery.mock(ResourceMethod.class);
		
		mockery.checking(new Expectations(){{
            one(request).getAttribute(StupidTranslator.INCLUDE_REQUEST_URI); will(returnValue(null));
			one(request).getRequestURI(); will(returnValue("/"));
			one(request).getMethod(); will(returnValue("GET"));
			one(registry).gimmeThis("/", "GET",webRequest); will(returnValue(expected));
		}});
		
		ResourceMethod resource = translator.translate(request);
		assertThat(resource, is(equalTo(expected)));
		mockery.assertIsSatisfied();
		
	}
	
	@Test
	public void testCanHandleUrlIfNonRootContextButPlainRequest() {
		
		final ResourceMethod expected = mockery.mock(ResourceMethod.class);
		
		mockery.checking(new Expectations(){{
            one(request).getAttribute(StupidTranslator.INCLUDE_REQUEST_URI); will(returnValue(null));
			one(request).getRequestURI(); will(returnValue("/custom_context/"));
			one(request).getMethod(); will(returnValue("GET"));
			one(registry).gimmeThis("/", "GET",webRequest); will(returnValue(expected));
		}});
		
		ResourceMethod resource = translator.translate(request);
		assertThat(resource, is(equalTo(expected)));
		mockery.assertIsSatisfied();
		
	}
	
}
