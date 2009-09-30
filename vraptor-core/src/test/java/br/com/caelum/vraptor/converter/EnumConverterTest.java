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
import static org.hamcrest.Matchers.nullValue;

import java.util.ResourceBundle;

import org.junit.Before;
import org.junit.Test;

public class EnumConverterTest {

	private EnumConverter<MyCustomEnum> converter;
	private ResourceBundle bundle;

	@Before
	public void setup() {
		this.converter = new EnumConverter<MyCustomEnum>();
		this.bundle = ResourceBundle.getBundle("messages");
	}

	@Test
	public void shouldBeAbleToConvertByOrdinal() {
		assertThat(converter.convert("1", MyCustomEnum.class, bundle), is(equalTo(MyCustomEnum.SECOND)));
	}

	@Test
	public void shouldBeAbleToConvertByName() {
		assertThat(converter.convert("FIRST", MyCustomEnum.class, bundle),
				is(equalTo(MyCustomEnum.FIRST)));
	}

	@Test
	public void shouldConvertEmptyToNull() {
		assertThat(converter.convert("", MyCustomEnum.class, bundle), is(nullValue()));
	}

	@Test
	public void shouldComplainAboutInvalidIndex() {
		try {
			converter.convert("3200", MyCustomEnum.class, bundle);
		} catch (ConversionError e) {
			assertThat(e.getMessage(), is(equalTo("3200 is not a valid option.")));
		}
	}

	@Test
	public void shouldComplainAboutInvalidNumber() {
		try {
			converter.convert("32a00", MyCustomEnum.class, bundle);
		} catch (ConversionError e) {
			assertThat(e.getMessage(), is(equalTo("32a00 is not a valid option.")));
		}
	}

	@Test
	public void shouldComplainAboutInvalidOrdinal() {
		try {
			converter.convert("THIRD", MyCustomEnum.class, bundle);
		} catch (ConversionError e) {
			assertThat(e.getMessage(), is(equalTo("THIRD is not a valid option.")));
		}
	}

	@Test
	public void shouldAcceptNull() {
		assertThat(converter.convert(null, MyCustomEnum.class, bundle), is(nullValue()));
	}

	enum MyCustomEnum {
		FIRST, SECOND
	}

}
