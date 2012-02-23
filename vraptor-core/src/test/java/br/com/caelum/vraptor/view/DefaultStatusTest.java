package br.com.caelum.vraptor.view;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.EnumSet;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.config.Configuration;
import br.com.caelum.vraptor.http.FormatResolver;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.proxy.CglibProxifier;
import br.com.caelum.vraptor.proxy.JavassistProxifier;
import br.com.caelum.vraptor.proxy.ObjenesisInstanceCreator;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.serialization.DefaultRepresentationResult;
import br.com.caelum.vraptor.serialization.JSONSerialization;
import br.com.caelum.vraptor.serialization.Serialization;
import br.com.caelum.vraptor.serialization.xstream.XStreamBuilderImpl;
import br.com.caelum.vraptor.util.test.MockSerializationResult;
import br.com.caelum.vraptor.validator.I18nMessage;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.MessageConverter;
import br.com.caelum.vraptor.validator.SingletonResourceBundle;
import br.com.caelum.vraptor.validator.ValidationMessage;

import com.google.common.collect.Lists;

public class DefaultStatusTest {

	private @Mock HttpServletResponse response;
	private @Mock Result result;
	private @Mock Configuration config;
	private @Mock Router router;

	private Status status;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		status = new DefaultStatus(response, result, config, new JavassistProxifier(new ObjenesisInstanceCreator()), router);
	}

	@Test
	public void shouldSetNotFoundStatus() throws Exception {
		status.notFound();

		verify(response).sendError(404);
	}

	@Test
	public void shouldSetHeader() throws Exception {
		status.header("Content-type", "application/xml");

		verify(response).addHeader("Content-type", "application/xml");
	}

	@Test
	public void shouldSetCreatedStatus() throws Exception {
		status.created();

		verify(response).setStatus(201);
	}

	@Test
	public void shouldSetCreatedStatusAndLocationWithAppPath() throws Exception {
		when(config.getApplicationPath()).thenReturn("http://myapp.com");
		status.created("/newResource");

		verify(response).setStatus(201);
		verify(response).addHeader("Location", "http://myapp.com/newResource");
	}

	@Test
	public void shouldSetOkStatus() throws Exception {
		status.ok();

		verify(response).setStatus(200);
	}

	@Test
	public void shouldSetConflictStatus() throws Exception {
		status.conflict();

		verify(response).sendError(409);
	}

	@Test 
	public void shouldSetAcceptedStatus() throws Exception {
		status.accepted();
		
		verify(response).setStatus(202);
	}
	
	@Test
	public void shouldSetMethodNotAllowedStatus() throws Exception {
		status.methodNotAllowed(EnumSet.of(HttpMethod.GET, HttpMethod.POST));

		verify(response).sendError(405);
		verify(response).addHeader("Allow", "GET, POST");
	}

	@Test
	public void shouldSetMovedPermanentlyStatus() throws Exception {
		when(config.getApplicationPath()).thenReturn("http://myapp.com");

		status.movedPermanentlyTo("/newURL");

		verify(response).setStatus(301);
		verify(response).addHeader("Location", "http://myapp.com/newURL");
	}
	@Test
	public void shouldMoveToExactlyURIWhenItIsNotAbsolute() throws Exception {

		status.movedPermanentlyTo("http://www.caelum.com.br");

		verify(response).addHeader("Location", "http://www.caelum.com.br");
		verify(response).setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
	}

	static interface Resource {
		void method();
	}

	@Test
	public void shouldSetMovedPermanentlyStatusOfLogic() throws Exception {
		when(config.getApplicationPath()).thenReturn("http://myapp.com");
		Method method = Resource.class.getDeclaredMethod("method");
        when(router.urlFor(eq(Resource.class), eq(method), Mockito.anyVararg())).thenReturn("/resource/method");

		status.movedPermanentlyTo(Resource.class).method();

		verify(response).setStatus(301);
		verify(response).addHeader("Location", "http://myapp.com/resource/method");
	}

	@Test
	public void shouldSerializeErrorMessages() throws Exception {
		Message normal = new ValidationMessage("The message", "category");
		I18nMessage i18ned = new I18nMessage("category", "message");
		i18ned.setBundle(new SingletonResourceBundle("message", "Something else"));
		
		MockSerializationResult result = new MockSerializationResult(XStreamBuilderImpl.cleanInstance(new MessageConverter()));
		DefaultStatus status = new DefaultStatus(response, result, config, new CglibProxifier(new ObjenesisInstanceCreator()), router);
		
		status.badRequest(Lists.newArrayList(normal, i18ned));
		
		String serialized = result.serializedResult();
		assertThat(serialized, containsString("<message>The message</message>"));
		assertThat(serialized, containsString("<category>category</category>"));
		assertThat(serialized, containsString("<message>Something else</message>"));
		assertThat(serialized, not(containsString("<validationMessage>")));
		assertThat(serialized, not(containsString("<i18nMessage>")));
	}
	@Test
	public void shouldSerializeErrorMessagesInJSON() throws Exception {
		Message normal = new ValidationMessage("The message", "category");
		I18nMessage i18ned = new I18nMessage("category", "message");
		i18ned.setBundle(new SingletonResourceBundle("message", "Something else"));

		MockSerializationResult result = new MockSerializationResult(XStreamBuilderImpl.cleanInstance(new MessageConverter())) {
			@Override
			public <T extends View> T use(Class<T> view) {
				return view.cast(new DefaultRepresentationResult(new FormatResolver() {
					public String getAcceptFormat() {
						return "json";
					}
					
				}, this, Arrays.<Serialization>asList(super.use(JSONSerialization.class)), null));
			}
		};
		DefaultStatus status = new DefaultStatus(response, result, config, new CglibProxifier(new ObjenesisInstanceCreator()), router);
		
		status.badRequest(Lists.newArrayList(normal, i18ned));
		
		String serialized = result.serializedResult();
		assertThat(serialized, containsString("\"message\": \"The message\""));
		assertThat(serialized, containsString("\"category\": \"category\""));
		assertThat(serialized, containsString("\"message\": \"Something else\""));
		assertThat(serialized, not(containsString("\"validationMessage\"")));
		assertThat(serialized, not(containsString("\"i18nMessage\"")));
	}
}
