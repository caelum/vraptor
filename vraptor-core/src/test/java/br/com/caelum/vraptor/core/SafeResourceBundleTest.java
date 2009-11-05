package br.com.caelum.vraptor.core;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.util.PropertyResourceBundle;

import org.junit.Before;
import org.junit.Test;

public class SafeResourceBundleTest {


	private SafeResourceBundle bundle;

	@Before
	public void setUp() throws Exception {
		PropertyResourceBundle delegate = new PropertyResourceBundle(new ByteArrayInputStream("abc=def".getBytes()));
		bundle = new SafeResourceBundle(delegate);
	}

	@Test
	public void shouldReturnDelegateValueWhenKeyExists() throws Exception {
		String result = bundle.getString("abc");
		assertThat(result, is("def"));
	}

	@Test
	public void shouldReturnKeyBetweenQuestionMarksWhenKeyDoesntExist() throws Exception {
		String result = bundle.getString("any key");
		assertThat(result, is("???any key???"));
	}
}
