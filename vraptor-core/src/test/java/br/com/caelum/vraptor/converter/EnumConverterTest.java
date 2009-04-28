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

import java.util.ArrayList;
import java.util.ResourceBundle;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.interceptor.VRaptorMatchers;
import br.com.caelum.vraptor.validator.ValidationMessage;

public class EnumConverterTest {

    private EnumConverter converter;
	private ArrayList<ValidationMessage> errors;
	private ResourceBundle bundle;

    @Before
    public void setup() {
        this.converter = new EnumConverter();
        this.errors = new ArrayList<ValidationMessage>();
        this.bundle = ResourceBundle.getBundle("messages");
    }

    @Test
    public void shouldBeAbleToConvertByOrdinal() {
        assertThat((MyCustomEnum) converter.convert("1", MyCustomEnum.class, errors, bundle), is(equalTo(MyCustomEnum.SECOND)));
    }

    @Test
    public void shouldBeAbleToConvertByName() {
        assertThat((MyCustomEnum) converter.convert("FIRST", MyCustomEnum.class, errors, bundle), is(equalTo(MyCustomEnum.FIRST)));
    }

    @Test
    public void shouldConvertEmptyToNull() {
        assertThat(converter.convert("", MyCustomEnum.class, errors, bundle), is(nullValue()));
    }

    @Test
    public void shouldComplainAboutInvalidIndex() {
        converter.convert("3200", MyCustomEnum.class, errors, bundle);
        assertThat(errors.get(0), is(VRaptorMatchers.error("", "3200 is not a valid option.")));
    }

    @Test
    public void shouldComplainAboutInvalidNumber() {
        converter.convert("32a00", MyCustomEnum.class, errors, bundle);
        assertThat(errors.get(0), is(VRaptorMatchers.error("", "32a00 is not a valid option.")));
    }

    @Test
    public void shouldComplainAboutInvalidOrdinal() {
        converter.convert("THIRD", MyCustomEnum.class, errors, bundle);
        assertThat(errors.get(0), is(VRaptorMatchers.error("", "THIRD is not a valid option.")));
    }

    @Test
    public void shouldAcceptNull() {
        assertThat((MyCustomEnum) converter.convert(null, MyCustomEnum.class, errors, bundle), is(nullValue()));
    }

    enum MyCustomEnum {
        FIRST, SECOND
    }

}
