/***
 *
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of the
 * copyright holders nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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
