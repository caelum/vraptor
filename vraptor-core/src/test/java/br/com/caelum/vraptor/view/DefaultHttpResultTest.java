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
package br.com.caelum.vraptor.view;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DefaultHttpResultTest {

	private @Mock HttpServletResponse response;
	private @Mock Status status;

	private HttpResult httpResult;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		httpResult = new DefaultHttpResult(response, status);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void shouldDelegateToStatus() throws Exception {
		httpResult.movedPermanentlyTo("/my/uri");

		verify(status).movedPermanentlyTo("/my/uri");
	}

	public static class RandomController {
		public void method() {
		}
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void shouldDelegateToStatusWhenMovingToLogic() throws Exception {
		when(status.movedPermanentlyTo(RandomController.class)).thenReturn(new RandomController());

		httpResult.movedPermanentlyTo(RandomController.class).method();

		verify(status).movedPermanentlyTo(RandomController.class);
	}
	
	@Test
	public void shouldHeadersProperly() {
		httpResult.addDateHeader("key", 10L);
		verify(response).addDateHeader("key", 10L);
		
		httpResult.addHeader("key", "value");
		verify(response).addHeader("key", "value");
		
		httpResult.addIntHeader("key", 10);
		verify(response).addIntHeader("key", 10);
	}

	@Test
	public void shouldSendError() throws Exception {
		httpResult.sendError(SC_INTERNAL_SERVER_ERROR);
		verify(response).sendError(SC_INTERNAL_SERVER_ERROR);
	}
	
	@Test
	public void shouldThrowsResultExceptionIfAnIOExceptionWhenSendError() throws Exception {
		doThrow(new IOException()).when(response).sendError(anyInt());

		try {
			httpResult.sendError(SC_INTERNAL_SERVER_ERROR);
			fail("should throw ResultException");
		} catch (ResultException e) {
			verify(response, only()).sendError(anyInt());
		}
	}
	
	@Test
	public void shouldSendErrorWithMessage() throws Exception {
		httpResult.sendError(SC_INTERNAL_SERVER_ERROR, "A simple message");
		verify(response).sendError(SC_INTERNAL_SERVER_ERROR, "A simple message");
	}

	@Test
	public void shouldThrowResultExceptionIfAnIOExceptionWhenSendErrorWithMessage() throws Exception {
		doThrow(new IOException()).when(response).sendError(anyInt(), anyString());

		try {
			httpResult.sendError(SC_INTERNAL_SERVER_ERROR, "A simple message");
			fail("should throw ResultException");
		} catch (ResultException e) {
			verify(response, only()).sendError(anyInt(), anyString());
		}
	}
	
	@Test
	public void shouldSetStatusCode() throws Exception {
		httpResult.setStatusCode(SC_INTERNAL_SERVER_ERROR);
		verify(response).setStatus(SC_INTERNAL_SERVER_ERROR);
	}
	
	@Test
	public void shouldWriteStringBody() throws Exception {
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);
		
		httpResult.body("The text");
		verify(writer).print(anyString());
	}
	
	@Test
	public void shouldThrowResultExceptionIfAnIOExceptionWhenWriteStringBody() throws Exception {
		doThrow(new IOException()).when(response).getWriter();
		
		try {
			httpResult.body("The text");
			fail("should throw ResultException");
		} catch (ResultException e) {
			
		}
	}

	@Test
	public void shouldThrowResultExceptionIfAnIOExceptionWhenWriteInputStreamBody() throws Exception {
		doThrow(new IOException()).when(response).getOutputStream();
		InputStream in = new ByteArrayInputStream("the text".getBytes());
		
		try {
			httpResult.body(in);
			fail("should throw ResultException");
		} catch (ResultException e) {
			
		}
	}

	@Test
	public void shouldThrowResultExceptionIfAnIOExceptionWhenWriteReaderBody() throws Exception {
		doThrow(new IOException()).when(response).getWriter();
		Reader reader = new StringReader("the text");
		
		try {
			httpResult.body(reader);
			fail("should throw ResultException");
		} catch (ResultException e) {
			
		}
	}
}
