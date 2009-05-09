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
import static org.hamcrest.Matchers.nullValue;

import java.util.ResourceBundle;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BooleanConverterTest {

    private BooleanConverter converter;
	private ResourceBundle bundle;

    @Before
    public void setup() {
        this.converter = new BooleanConverter();
        this.bundle = ResourceBundle.getBundle("messages");
    }

    @Test
    public void shouldBeAbleToConvertTrueAndFalse(){
        assertThat(converter.convert("true", Boolean.class, bundle), is(equalTo(true)));
        assertThat(converter.convert("false", Boolean.class, bundle), is(equalTo(false)));
    }

    @Test
    public void shouldNotComplainAboutNull() {
        assertThat(converter.convert(null, Boolean.class, bundle), is(nullValue()));
    }

    @Test
    public void shouldConvertYesNo() {
    	assertThat(converter.convert("yes", Boolean.class, bundle), is(equalTo(true)));
    	assertThat(converter.convert("no", Boolean.class, bundle), is(equalTo(false)));
    }
    @Test
    public void shouldConvertYN() {
    	assertThat(converter.convert("y", Boolean.class, bundle), is(equalTo(true)));
    	assertThat(converter.convert("n", Boolean.class, bundle), is(equalTo(false)));
    }
    @Test
    public void shouldConvertOnOff() {
    	assertThat(converter.convert("on", Boolean.class, bundle), is(equalTo(true)));
    	assertThat(converter.convert("off", Boolean.class, bundle), is(equalTo(false)));
    }

    @Test
    public void shouldConvertIgnoringCase() {
    	assertThat(converter.convert("truE", Boolean.class, bundle), is(equalTo(true)));
    	assertThat(converter.convert("FALSE", Boolean.class, bundle), is(equalTo(false)));
    	assertThat(converter.convert("On", Boolean.class, bundle), is(equalTo(true)));
    	assertThat(converter.convert("oFf", Boolean.class, bundle), is(equalTo(false)));
    }

    @Test
    public void shouldThrowExceptionForInvalidString() {
    	try {
    		converter.convert("not a boolean!", Boolean.class, bundle);
    		Assert.assertTrue(false);
    	} catch(ConversionError e) {
    		assertThat(e.getMessage(), is(equalTo("NOT A BOOLEAN! is not a valid boolean. Please use true/false, yes/no, y/n or on/off")));
    	}
    }
}
