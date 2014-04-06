package br.com.caelum.vraptor.http.iogi;

import static java.util.Arrays.asList;
import static java.util.Collections.enumeration;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.iogi.reflection.Target;

public class RequestAttributeInstantiatorTest {

	private static final String TARGET_ATTRIBUTE_NAME = "attributeName";

	private HttpServletRequest mockRequest;
	
	private RequestAttributeInstantiator requestAttributeInstantiator;
	
	@Before
	public void setup() {
		
		mockRequest = mock(HttpServletRequest.class);
		
		requestAttributeInstantiator = new RequestAttributeInstantiator(mockRequest);
		
		when(mockRequest.getAttribute(TARGET_ATTRIBUTE_NAME)).thenReturn("attribute value");
		
		when(mockRequest.getAttributeNames()).thenReturn(enumeration(asList(TARGET_ATTRIBUTE_NAME)));
		
	}
	
	@Test
	public void shouldAbleToInstantiateWhenRequestAttributeExistsAndSameTypeOfTarget() {
		
		Target<String> target = Target.create(String.class, TARGET_ATTRIBUTE_NAME);
		
		assertTrue(requestAttributeInstantiator.isAbleToInstantiate(target));
	}
	
	@Test
	public void shouldNotAbleToInstantiateWhenRequestAttributeExistsButDifferentTypeOfTarget() {
		
		Target<Integer> target = Target.create(Integer.class, TARGET_ATTRIBUTE_NAME);
		
		assertFalse(requestAttributeInstantiator.isAbleToInstantiate(target));
	}
	
	@Test
	public void shouldAbleToInstantiateWhenRequestAttributeExistsButValueIsNull() {
		
		Target<String> target = Target.create(String.class, TARGET_ATTRIBUTE_NAME);
		
		when(mockRequest.getAttribute(TARGET_ATTRIBUTE_NAME)).thenReturn(null);
		
		assertTrue(requestAttributeInstantiator.isAbleToInstantiate(target));
	}
		
}
