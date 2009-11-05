package br.com.caelum.vraptor.view;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class DefaultXmlConfigurationTest {
	
	private DefaultXmlConfiguration config;

	@Before
    public void setup() {
		this.config = new DefaultXmlConfiguration();
    }

	@Test
	public void shouldUndernalizeAllCamelcase() {
		String base = "CamelCaseValue";
		assertThat(config.nameFor(base), is(equalTo("camel_case_value")));
	}

	@Test
	public void shouldUndernalizeAllCamelcaseCharacters() {
		String base = "C";
		assertThat(config.nameFor(base), is(equalTo("c")));
	}

}
