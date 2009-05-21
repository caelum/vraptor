package br.com.caelum.vraptor.interceptor;

import java.io.File;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.interceptor.download.Download;
import br.com.caelum.vraptor.interceptor.download.DownloadInterceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class DownloadInterceptorTest {
    private Mockery mockery;

    private DownloadInterceptor interceptor;
    private MethodInfo info;
	private HttpServletResponse response;
	private ResourceMethod resourceMethod;
	private InterceptorStack stack;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.response = mockery.mock(HttpServletResponse.class);
        this.info = mockery.mock(MethodInfo.class);
        interceptor = new DownloadInterceptor(response, info);
        this.resourceMethod = mockery.mock(ResourceMethod.class);
        this.stack = mockery.mock(InterceptorStack.class);
    }

	@Test
	public void testIfAcceptsFile() throws Exception {
		mockery.checking(new Expectations() {{
			one(resourceMethod).getMethod(); will(returnValue(FakeResource.class.getMethod("file")));
		}});

		Assert.assertTrue("Nao aceitou java.io.File", interceptor.accepts(resourceMethod));
		mockery.assertIsSatisfied();
	}
	@Test
	public void testIfAcceptsInputStream() throws Exception {
		mockery.checking(new Expectations() {{
			one(resourceMethod).getMethod(); will(returnValue(FakeResource.class.getMethod("input")));
		}});

		Assert.assertTrue("Nao aceitou java.io.InputStream", interceptor.accepts(resourceMethod));
		mockery.assertIsSatisfied();
	}
	@Test
	public void testIfAcceptsDownload() throws Exception {
		mockery.checking(new Expectations() {{
			one(resourceMethod).getMethod(); will(returnValue(FakeResource.class.getMethod("download")));
		}});

		Assert.assertTrue("Nao aceitou Download", interceptor.accepts(resourceMethod));
		mockery.assertIsSatisfied();
	}

	@Test
	public void testIfCalsNextOnStackIfDoesntAcceptsIt() throws Exception {
		final Object instance = new Object();

		mockery.checking(new Expectations() {{
			one(resourceMethod).getMethod(); will(returnValue(FakeResource.class.getMethod("string")));
			one(stack).next(resourceMethod, instance);
		}});

		interceptor.intercept(stack, resourceMethod, instance);
		mockery.assertIsSatisfied();
	}

	static class FakeResource {
		public String string() {
			return null;
		}
		public File file() {
			return null;
		}
		public InputStream input() {
			return null;
		}
		public Download download() {
			return null;
		}
	}
}
