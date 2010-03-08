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
package br.com.caelum.vraptor.http.iogi;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import ognl.OgnlException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.iogi.Iogi;
import br.com.caelum.iogi.parameters.Parameter;
import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.iogi.spi.LocaleProvider;
import br.com.caelum.iogi.util.NullDependencyProvider;

/**
 * Unfortunately OGNL sucks so bad in its design that we had to create a "unit"
 * test which accesses more than a single class to test the ognl funcionality.
 * Even worse, OGNL sucks with its static configuration methods in such a way
 * that tests are not thread safe. Summing up: OGNL api sucks, OGNL idea rulez.
 * Tests written here are "acceptance tests" for the Ognl support on http
 * parameters.
 *
 * @author Guilherme Silveira
 */
public class MiscIogiSupportTest {

    private Mockery mockery;

    private Iogi iogi;
	private LocaleProvider mockLocaleProvider;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.mockLocaleProvider = mockery.mock(LocaleProvider.class);
    	this.iogi = new Iogi(new NullDependencyProvider(), mockLocaleProvider);
    }

    public static class Cat {
        private Leg firstLeg;

        public void setFirstLeg(Leg firstLeg) {
            this.firstLeg = firstLeg;
        }

        public Leg getFirstLeg() {
            return firstLeg;
        }
    }

    public static class Leg {
        private Integer id;
        private Calendar birthDay; // weird leg birthday!!

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getId() {
            return id;
        }

        public void setBirthDay(Calendar birthDay) {
            this.birthDay = birthDay;
        }

        public Calendar getBirthDay() {
            return birthDay;
        }
    }

    public static class House {
        private Cat cat;

        public void setCat(Cat cat) {
            this.cat = cat;
        }

        public Cat getCat() {
            return cat;
        }

    }

    @Test
    public void isCapableOfDealingWithEmptyParameterForInternalWrapperValue() {
        final Target<House> target = Target.create(House.class, "house");
        final Parameter parameter = new Parameter("house.cat.firstLeg.id", "");
        final House house = iogi.instantiate(target, parameter);
        assertThat(house.cat.firstLeg.id, is(equalTo(null)));
    }

    @Test
    public void isCapableOfDealingWithEmptyParameterForInternalValueWhichNeedsAConverter() throws OgnlException {
    	final Target<House> target = Target.create(House.class, "house");
    	final Parameter parameter = new Parameter("house.cat.firstLeg.birthDay", "10/5/2010");
    	mockery.checking(new Expectations() {{
    		allowing(mockLocaleProvider).getLocale();
    		will(returnValue(new Locale("pt", "BR")));
    	}});
    	final House house = iogi.instantiate(target, parameter);
    	assertThat(house.cat.firstLeg.birthDay, is(equalTo((Calendar)new GregorianCalendar(2010, 4, 10))));
	}

}
