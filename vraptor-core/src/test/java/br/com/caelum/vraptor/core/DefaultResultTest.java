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

package br.com.caelum.vraptor.core;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.view.LogicResult;
import br.com.caelum.vraptor.view.PageResult;
import br.com.caelum.vraptor.view.Status;
import br.com.caelum.vraptor.view.DefaultHttpResultTest.RandomController;

public class DefaultResultTest {

    @Mock private HttpServletRequest request;
    @Mock private Container container;

	private Result result;
	@Mock private TypeNameExtractor extractor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        result = new DefaultResult(request, container, null, extractor);
    }

    public static class MyView implements View {

    }

    @Test
    public void shouldUseContainerForNewView() {
        final MyView expectedView = new MyView();
        when(container.instanceFor(MyView.class)).thenReturn(expectedView);

        MyView view = result.use(MyView.class);
        assertThat(view, is(expectedView));
    }

    @Test
    public void shouldSetRequestAttribute() {

        result.include("my_key", "my_value");

        verify(request).setAttribute("my_key", "my_value");
    }

    @Test
	public void shouldDelegateToPageResultOnForwardToURI() throws Exception {

    	PageResult pageResult = mockResult(PageResult.class);

    	result.forwardTo("/any/uri");

    	verify(pageResult).forwardTo("/any/uri");
	}

    @Test
	public void shouldDelegateToPageResultOnRedirectToURI() throws Exception {

    	PageResult pageResult = mockResult(PageResult.class);

    	result.redirectTo("/any/uri");

    	verify(pageResult).redirectTo("/any/uri");
	}

	private <T extends View> T mockResult(Class<T> view) {
		T pageResult = mock(view);

    	when(container.instanceFor(view)).thenReturn(pageResult);

		return pageResult;
	}
    @Test
    public void shouldDelegateToPageResultOnPageOf() throws Exception {

    	PageResult pageResult = mockResult(PageResult.class);

    	result.of(RandomController.class);

    	verify(pageResult).of(RandomController.class);
    }
    @Test
    public void shouldDelegateToLogicResultOnForwardToLogic() throws Exception {

    	LogicResult logicResult = mockResult(LogicResult.class);

    	result.forwardTo(RandomController.class);

    	verify(logicResult).forwardTo(RandomController.class);

    }
    @Test
    public void shouldDelegateToLogicResultOnRedirectToLogic() throws Exception {

    	LogicResult logicResult = mockResult(LogicResult.class);

    	result.redirectTo(RandomController.class);

    	verify(logicResult).redirectTo(RandomController.class);

    }
    @Test
    public void shouldDelegateToLogicResultOnRedirectToLogicWithInstance() throws Exception {

    	LogicResult logicResult = mockResult(LogicResult.class);

    	result.redirectTo(new RandomController());

    	verify(logicResult).redirectTo(RandomController.class);

    }

    @Test
    public void shouldDelegateToLogicResultOnForwardToLogicWithInstance() throws Exception {

    	LogicResult logicResult = mockResult(LogicResult.class);

    	result.forwardTo(new RandomController());

    	verify(logicResult).forwardTo(RandomController.class);

    }
    @Test
    public void shouldDelegateToPageResultOnPageOfWithInstance() throws Exception {

    	PageResult pageResult = mockResult(PageResult.class);

    	result.of(new RandomController());

    	verify(pageResult).of(RandomController.class);

    }

    @Test
    public void shouldDelegateToStatusOnNotFound() throws Exception {

    	Status status = mockResult(Status.class);

    	result.notFound();

    	verify(status).notFound();

    }

    @Test
    public void shouldDelegateToStatusOnPermanentlyRedirectToUri() throws Exception {

    	Status status = mockResult(Status.class);

    	result.permanentlyRedirectTo("url");

    	verify(status).movedPermanentlyTo("url");

    }

    @Test
    public void shouldDelegateToStatusOnPermanentlyRedirectToControllerClass() throws Exception {

    	Status status = mockResult(Status.class);

    	result.permanentlyRedirectTo(RandomController.class);

    	verify(status).movedPermanentlyTo(RandomController.class);

    }

    @Test
    public void shouldDelegateToStatusOnPermanentlyRedirectToControllerInstance() throws Exception {

    	Status status = mockResult(Status.class);

    	result.permanentlyRedirectTo(new RandomController());

    	verify(status).movedPermanentlyTo(RandomController.class);

    }


    class Account {
    	
    }

    @Test
    public void shouldIncludeExtractedNameWhenSimplyIncluding() throws Exception {

    	Account account = new Account();
    	when(extractor.nameFor(Account.class)).thenReturn("account");
    	
    	result.include(account);

    	verify(request).setAttribute("account", account);

    }
    
    @Test
    public void shouldNotIncludeTheAttributeWhenTheValueIsNull() throws Exception {
    	result.include(null);
    	verify(request, never()).setAttribute(anyString(), anyObject());
    }
}
