package br.com.caelum.vraptor.util.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.ResourceBundle;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.validator.Message;

public class MockValidatorTest {
	private MockValidator validator;
	private ResourceBundle bundle;

	@Before
	public void setUp() {
		validator = spy(new MockValidator());
		bundle = ResourceBundle.getBundle("messages");
	}

	@Test
	public void containsMessageShouldReturnFalseWhenExpectedMessageDontExists() {
		assertFalse(validator.containsMessage("required_field", "name"));
	}
	
	@Test
	public void containsMessageShouldReturnTrueWhenExpectedMessageExists() {
		Message message = mock(Message.class);
		
		when(message.getMessage()).thenReturn(bundle.getString("underage"));
		when(validator.getErrors()).thenReturn(Arrays.asList(message));
		
		assertTrue(validator.containsMessage("underage"));
	}
	
	@Test
	public void containsMessageShouldReturnTrueWhenExpectedMessageWithParamsExists() {
		Message message = mock(Message.class);
		
		when(message.getMessage()).thenReturn(MessageFormat.format(bundle.getString("required_field"), "name"));
		when(validator.getErrors()).thenReturn(Arrays.asList(message));
		
		assertTrue(validator.containsMessage("required_field", "name"));
	}
}
