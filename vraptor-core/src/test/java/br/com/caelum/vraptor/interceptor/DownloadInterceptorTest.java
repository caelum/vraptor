/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
