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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.interceptor.download.Download;
import br.com.caelum.vraptor.interceptor.download.DownloadInterceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class DownloadInterceptorTest {

    private DownloadInterceptor interceptor;

    @Mock private MethodInfo info;
	@Mock private HttpServletResponse response;
	@Mock private ResourceMethod resourceMethod;
	@Mock private InterceptorStack stack;
	@Mock private ServletOutputStream outputStream;
	@Mock private Result result;

    @Before
    public void setup() throws Exception {
    	MockitoAnnotations.initMocks(this);

		when(response.getOutputStream()).thenReturn(outputStream);

        interceptor = new DownloadInterceptor(response, info, result);
    }

	@Test
	public void testIfAcceptsFile() throws Exception {
		when(resourceMethod.getMethod()).thenReturn(FakeResource.class.getMethod("file"));

		Assert.assertTrue("Nao aceitou java.io.File", interceptor.accepts(resourceMethod));
	}
	@Test
	public void testIfAcceptsInputStream() throws Exception {
		when(resourceMethod.getMethod()).thenReturn(FakeResource.class.getMethod("input"));

		Assert.assertTrue("Nao aceitou java.io.InputStream", interceptor.accepts(resourceMethod));
	}
	@Test
	public void testIfAcceptsDownload() throws Exception {
		when(resourceMethod.getMethod()).thenReturn(FakeResource.class.getMethod("download"));

		Assert.assertTrue("Nao aceitou Download", interceptor.accepts(resourceMethod));
	}

	@Test
	public void whenResultIsADownloadShouldUseIt() throws Exception {
		Download download = mock(Download.class);

		when(info.getResult()).thenReturn(download);

		interceptor.intercept(stack, resourceMethod, null);

		verify(download).write(response);

	}

	@Test
	public void whenResultIsAnInputStreamShouldCreateAInputStreamDownload() throws Exception {

		byte[] bytes = "abc".getBytes();
		when(info.getResult()).thenReturn(new ByteArrayInputStream(bytes));

		interceptor.intercept(stack, resourceMethod, null);

		verify(outputStream).write(argThat(is(arrayStartingWith(bytes))), eq(0), eq(3));

	}
	@Test
	public void whenResultIsAnInputStreamShouldCreateAByteArrayDownload() throws Exception {

		byte[] bytes = "abc".getBytes();
		when(info.getResult()).thenReturn(bytes);

		interceptor.intercept(stack, resourceMethod, null);

		verify(outputStream).write(argThat(is(arrayStartingWith(bytes))), eq(0), eq(3));

	}

	@Test
	public void whenResultIsAFileShouldCreateAFileDownload() throws Exception {

		File tmp = File.createTempFile("test", "test");
		new PrintWriter(tmp).append("abc").close();

		when(info.getResult()).thenReturn(tmp);

		interceptor.intercept(stack, resourceMethod, null);

		verify(outputStream).write(argThat(is(arrayStartingWith("abc".getBytes()))), eq(0), eq(3));

	}
	@Test
	public void whenResultIsNullAndResultWasUsedShouldDoNothing() throws Exception {

		when(info.getResult()).thenReturn(null);
		when(result.used()).thenReturn(true);


		interceptor.intercept(stack, resourceMethod, null);

		verify(stack).next(resourceMethod, null);
		verifyZeroInteractions(response);

	}
	@Test
	public void whenResultIsNullAndResultWasNotUsedShouldThrowNPE() throws Exception {

		when(info.getResult()).thenReturn(null);
		when(result.used()).thenReturn(false);

		try {
			interceptor.intercept(stack, resourceMethod, null);
			fail("expected NullPointerException");
		} catch (NullPointerException e) {
			verifyZeroInteractions(response);
		}


	}

	private Matcher<byte[]> arrayStartingWith(final byte[] array) {
		return new TypeSafeMatcher<byte[]>() {
			@Override
			protected void describeMismatchSafely(byte[] item, Description mismatchDescription) {
			}
			@Override
			protected boolean matchesSafely(byte[] item) {
				if (item.length < array.length) {
					return false;
				}
				for (int i = 0; i < array.length; i++) {
					if (array[i] != item[i]) {
						return false;
					}
				}
				return true;
			}

			public void describeTo(Description description) {
				description.appendText("a byte array starting with " + Arrays.toString(array));
			}
		};
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
