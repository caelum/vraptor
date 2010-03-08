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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

}
