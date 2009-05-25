/***
 * 
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of the
 * copyright holders nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
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
