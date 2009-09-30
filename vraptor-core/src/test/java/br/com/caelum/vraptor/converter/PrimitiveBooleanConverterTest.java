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

package br.com.caelum.vraptor.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.ResourceBundle;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PrimitiveBooleanConverterTest {

	private PrimitiveBooleanConverter converter;
	private ResourceBundle bundle;

	@Before
	public void setup() {
		this.converter = new PrimitiveBooleanConverter();
		this.bundle = ResourceBundle.getBundle("messages");
	}

	@Test
	public void shouldBeAbleToConvertNumbers() {
		assertThat((Boolean) converter.convert("", boolean.class, bundle), is(equalTo(false)));
		assertThat((Boolean) converter.convert("false", boolean.class, bundle), is(equalTo(false)));
		assertThat((Boolean) converter.convert("true", boolean.class, bundle), is(equalTo(true)));
		assertThat((Boolean) converter.convert("True", boolean.class, bundle), is(equalTo(true)));
	}

	@Test
	public void shouldConvertToZeroWhenNull() {
		assertThat((Boolean) converter.convert(null, boolean.class, bundle), is(equalTo(false)));
	}

	@Test
	public void shouldConvertToZeroWhenEmpty() {
		assertThat((Boolean) converter.convert("", boolean.class, bundle), is(equalTo(false)));
	}

	@Test
	public void shouldConvertYesNo() {
		assertThat((Boolean) converter.convert("yes", boolean.class, bundle), is(equalTo(true)));
		assertThat((Boolean) converter.convert("no", boolean.class, bundle), is(equalTo(false)));
	}

	@Test
	public void shouldConvertYN() {
		assertThat((Boolean) converter.convert("y", boolean.class, bundle), is(equalTo(true)));
		assertThat((Boolean) converter.convert("n", boolean.class, bundle), is(equalTo(false)));
	}

	@Test
	public void shouldConvertOnOff() {
		assertThat((Boolean) converter.convert("on", boolean.class, bundle), is(equalTo(true)));
		assertThat((Boolean) converter.convert("off", boolean.class, bundle), is(equalTo(false)));
	}

	@Test
	public void shouldConvertIgnoringCase() {
		assertThat((Boolean) converter.convert("truE", boolean.class, bundle), is(equalTo(true)));
		assertThat((Boolean) converter.convert("FALSE", boolean.class, bundle), is(equalTo(false)));
		assertThat((Boolean) converter.convert("On", boolean.class, bundle), is(equalTo(true)));
		assertThat((Boolean) converter.convert("oFf", boolean.class, bundle), is(equalTo(false)));
	}

	@Test
	public void shouldThrowExceptionForInvalidString() {
		try {
			converter.convert("not a boolean!", boolean.class, bundle);
			Assert.assertTrue(false);
		} catch (ConversionError e) {
			assertThat(e.getMessage(),
					is(equalTo("NOT A BOOLEAN! is not a valid boolean. Please use true/false, yes/no, y/n or on/off")));
		}
	}
}
