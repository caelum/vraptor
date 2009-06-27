package br.com.caelum.vraptor.http.iogi;

import static org.junit.Assert.*;

import java.util.Locale;

import org.jmock.Expectations;
import org.junit.After;
import org.junit.Test;

import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.test.VRaptorMockery;


public class JstlLocaleProviderTest {
	private VRaptorMockery mockery = new VRaptorMockery();
	
	@After
	public void tearDown() {
		mockery.assertIsSatisfied();
	}
	
	@Test
	public void willReturnValueSetAsARequestAttribute() throws Exception {
		final MutableRequest request = mockery.mock(MutableRequest.class);
		RequestInfo mockRequestInfo = new RequestInfo(null, request, null);
		JstlLocaleProvider localeProvider = new JstlLocaleProvider(mockRequestInfo);
		mockery.checking(new Expectations() {{
			exactly(2).of(request).getAttribute("javax.servlet.jsp.jstl.fmt.locale.request");
            will(returnValue("pt_br"));
		}});
		Locale locale = localeProvider.getLocale();
		assertEquals("pt", locale.getLanguage());
		assertEquals("BR", locale.getCountry());
	}
}
