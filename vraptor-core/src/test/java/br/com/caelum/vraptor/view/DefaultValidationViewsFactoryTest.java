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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.proxy.JavassistProxifier;
import br.com.caelum.vraptor.proxy.ObjenesisInstanceCreator;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.serialization.JSONSerialization;
import br.com.caelum.vraptor.serialization.SerializerBuilder;
import br.com.caelum.vraptor.util.test.MockedLogic;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationException;

public class DefaultValidationViewsFactoryTest {


	private Result result;
	private Proxifier proxifier;
	private DefaultValidationViewsFactory factory;
	private List<Message> errors;
	private SerializerBuilder serializerBuilder;

	@Before
	public void setUp() throws Exception {
		result = mock(Result.class);

		proxifier = new JavassistProxifier(new ObjenesisInstanceCreator());
		factory = new DefaultValidationViewsFactory(result, proxifier);
		errors = Collections.emptyList();

	}


	public static class RandomComponent {
		public void random() {

		}
	}
	@Test(expected=ValidationException.class)
	public void shouldUseValidationVersionOfLogicResult() throws Exception {

		when(result.use(LogicResult.class)).thenReturn(new MockedLogic());

		factory.instanceFor(LogicResult.class, errors).forwardTo(RandomComponent.class).random();
	}
	@Test
	public void shouldThrowExceptionOnlyAtTheEndOfValidationCall() throws Exception {

		when(result.use(LogicResult.class)).thenReturn(new MockedLogic());
		when(result.use(PageResult.class)).thenReturn(new MockedPage());

		factory.instanceFor(LogicResult.class, errors);
		factory.instanceFor(LogicResult.class, errors).forwardTo(RandomComponent.class);
		factory.instanceFor(PageResult.class, errors);
		factory.instanceFor(PageResult.class, errors).of(RandomComponent.class);
	}
	@Test(expected=ValidationException.class)
	public void shouldUseValidationVersionOfPageResult() throws Exception {
		when(result.use(PageResult.class)).thenReturn(new MockedPage());

		factory.instanceFor(PageResult.class, errors).forwardTo("any uri");
	}
	@Test(expected=ValidationException.class)
	public void shouldUseValidationVersionOfEmptyResult() throws Exception {
		when(result.use(EmptyResult.class)).thenReturn(new EmptyResult());

		factory.instanceFor(EmptyResult.class, errors);
	}

	@Test
	public void onHttpResultShouldNotThrowExceptionsOnHeaders() throws Exception {
		HttpResult httpResult = mock(HttpResult.class);

		when(result.use(HttpResult.class)).thenReturn(httpResult);

		factory.instanceFor(HttpResult.class, errors);
		factory.instanceFor(HttpResult.class, errors).addDateHeader("abc", 123l);
		factory.instanceFor(HttpResult.class, errors).addHeader("def", "ghi");
		factory.instanceFor(HttpResult.class, errors).addIntHeader("jkl", 456);
		factory.instanceFor(HttpResult.class, errors).addIntHeader("jkl", 456);
	}

	@Test(expected=ValidationException.class)
	public void onHttpResultShouldThrowExceptionsOnSendError() throws Exception {
		HttpResult httpResult = mock(HttpResult.class);

		when(result.use(HttpResult.class)).thenReturn(httpResult);

		factory.instanceFor(HttpResult.class, errors).sendError(404);
	}
	@Test(expected=ValidationException.class)
	public void onHttpResultShouldThrowExceptionsOnSendErrorWithMessage() throws Exception {
		HttpResult httpResult = mock(HttpResult.class);

		when(result.use(HttpResult.class)).thenReturn(httpResult);

		factory.instanceFor(HttpResult.class, errors).sendError(404, "Not Found");
	}
	@Test(expected=ValidationException.class)
	public void onHttpResultShouldThrowExceptionsOnSetStatus() throws Exception {
		HttpResult httpResult = mock(HttpResult.class);

		when(result.use(HttpResult.class)).thenReturn(httpResult);

		factory.instanceFor(HttpResult.class, errors).setStatusCode(200);
	}
	@Test
	public void shouldBeAbleToChainMethodsOnHttpResult() throws Exception {
		HttpResult httpResult = mock(HttpResult.class);

		when(result.use(HttpResult.class)).thenReturn(httpResult);

		factory.instanceFor(HttpResult.class, errors).addDateHeader("abc", 123l).addHeader("def", "ghi").addIntHeader("jkl", 234);
	}
	@SuppressWarnings("deprecation")
	@Test(expected=ValidationException.class)
	public void onHttpResultShouldThrowExceptionsOnMoved() throws Exception {
		HttpResult httpResult = mock(HttpResult.class);

		when(result.use(HttpResult.class)).thenReturn(httpResult);
		when(httpResult.movedPermanentlyTo(RandomComponent.class)).thenReturn(new RandomComponent());

		try {
			factory.instanceFor(HttpResult.class, errors).movedPermanentlyTo(RandomComponent.class);
		} catch (ValidationException e) {
			Assert.fail("The exception must occur only on method call");
		}
		factory.instanceFor(HttpResult.class, errors).movedPermanentlyTo(RandomComponent.class).random();
	}

	@SuppressWarnings("deprecation")
	@Test(expected=ValidationException.class)
	public void onHttpResultShouldThrowExceptionsOnMovedToLogic() throws Exception {
		HttpResult httpResult = mock(HttpResult.class);

		when(result.use(HttpResult.class)).thenReturn(httpResult);

		factory.instanceFor(HttpResult.class, errors).movedPermanentlyTo("anywhere");
	}

	@Test(expected=ValidationException.class)
	public void onRefererResultShouldThrowExceptionsOnForward() throws Exception {
		RefererResult referer = mock(RefererResult.class);

		when(result.use(RefererResult.class)).thenReturn(referer);

		factory.instanceFor(RefererResult.class, errors).forward();
	}

	@Test(expected=ValidationException.class)
	public void onRefererResultShouldThrowExceptionsOnRedirect() throws Exception {
		RefererResult referer = mock(RefererResult.class);

		when(result.use(RefererResult.class)).thenReturn(referer);

		factory.instanceFor(RefererResult.class, errors).redirect();
	}

	@Test(expected=ValidationException.class)
	public void onStatusResultShouldThrowExceptionsOnNotFound() throws Exception {
		Status status = mock(Status.class);

		when(result.use(Status.class)).thenReturn(status);

		factory.instanceFor(Status.class, errors).notFound();

	}
	@Test(expected=ValidationException.class)
	public void onStatusResultShouldThrowExceptionsOnHeader() throws Exception {
		Status status = mock(Status.class);

		when(result.use(Status.class)).thenReturn(status);

		factory.instanceFor(Status.class, errors).header("abc", "def");
	}

	@Test(expected=ValidationException.class)
	public void onStatusResultShouldThrowExceptionsOnCreated() throws Exception {
		Status status = mock(Status.class);

		when(result.use(Status.class)).thenReturn(status);

		factory.instanceFor(Status.class, errors).created();
	}
	@Test(expected=ValidationException.class)
	public void onStatusResultShouldThrowExceptionsOnCreatedWithLocation() throws Exception {
		Status status = mock(Status.class);

		when(result.use(Status.class)).thenReturn(status);

		factory.instanceFor(Status.class, errors).created("/newLocation");
	}

	@Test(expected=ValidationException.class)
	public void onStatusResultShouldThrowExceptionsOnOk() throws Exception {
		Status status = mock(Status.class);

		when(result.use(Status.class)).thenReturn(status);

		factory.instanceFor(Status.class, errors).ok();
	}

	@Test(expected=ValidationException.class)
	public void onStatusResultShouldThrowExceptionsOnConflict() throws Exception {
		Status status = mock(Status.class);

		when(result.use(Status.class)).thenReturn(status);

		factory.instanceFor(Status.class, errors).conflict();
	}
	@Test(expected=ValidationException.class)
	public void onStatusResultShouldThrowExceptionsOnMethodNotAllowed() throws Exception {
		Status status = mock(Status.class);

		when(result.use(Status.class)).thenReturn(status);

		factory.instanceFor(Status.class, errors).methodNotAllowed(EnumSet.allOf(HttpMethod.class));
	}
	@Test(expected=ValidationException.class)
	public void onStatusResultShouldThrowExceptionsOnMovedPermanentlyTo() throws Exception {
		Status status = mock(Status.class);

		when(result.use(Status.class)).thenReturn(status);

		factory.instanceFor(Status.class, errors).movedPermanentlyTo("/newUri");
	}
	@Test(expected=ValidationException.class)
	public void onStatusResultShouldThrowExceptionsOnMovedPermanentlyToLogic() throws Exception {
		Status status = mock(Status.class);

		when(result.use(Status.class)).thenReturn(status);
		when(status.movedPermanentlyTo(RandomComponent.class)).thenReturn(new RandomComponent());

		try {
			factory.instanceFor(Status.class, errors).movedPermanentlyTo(RandomComponent.class);
		} catch (ValidationException e) {
			Assert.fail("Should not throw exception yet");
		}
		factory.instanceFor(Status.class, errors).movedPermanentlyTo(RandomComponent.class).random();
	}
	@Test(expected=ValidationException.class)
	public void onXMLSerializationResultShouldThrowExceptionOnlyOnSerializeMethod() throws Exception {
		JSONSerialization serialization = mock(JSONSerialization.class);

		serializerBuilder = mock(SerializerBuilder.class, new Answer<SerializerBuilder>() {
			public SerializerBuilder answer(InvocationOnMock invocation) throws Throwable {
				return serializerBuilder;
			}
		});

		when(result.use(JSONSerialization.class)).thenReturn(serialization);
		when(serialization.from(any())).thenReturn(serializerBuilder);

		try {
			factory.instanceFor(JSONSerialization.class, errors).from(new Object());
			factory.instanceFor(JSONSerialization.class, errors).from(new Object()).include("abc");
			factory.instanceFor(JSONSerialization.class, errors).from(new Object()).exclude("abc");
		} catch (ValidationException e) {
			Assert.fail("Should not throw exception yet");
		}
		factory.instanceFor(JSONSerialization.class, errors).from(new Object()).serialize();
	}

	static class RandomSerializer implements SerializerBuilder {

		public RandomSerializer exclude(String... names) {
			return this;
		}

		public <T> RandomSerializer from(T object) {
			return this;
		}

		public <T> RandomSerializer from(T object, String alias) {
			return this;
		}

		public RandomSerializer include(String... names) {
			return this;
		}

		public RandomSerializer recursive() {
			return this;
		}

		public void serialize() {
		}

	}
	@Test(expected=ValidationException.class)
	public void onSerializerResultsShouldBeAbleToCreateValidationInstancesEvenIfChildClassesUsesCovariantType() throws Exception {
		JSONSerialization serialization = mock(JSONSerialization.class);

		serializerBuilder = new RandomSerializer();

		when(result.use(JSONSerialization.class)).thenReturn(serialization);
		when(serialization.from(any())).thenReturn(serializerBuilder);

		try {
			factory.instanceFor(JSONSerialization.class, errors).from(new Object());
			factory.instanceFor(JSONSerialization.class, errors).from(new Object()).include("abc");
			factory.instanceFor(JSONSerialization.class, errors).from(new Object()).exclude("abc");
		} catch (ValidationException e) {
			Assert.fail("Should not throw exception yet");
		}
		factory.instanceFor(JSONSerialization.class, errors).from(new Object()).serialize();
	}

}
