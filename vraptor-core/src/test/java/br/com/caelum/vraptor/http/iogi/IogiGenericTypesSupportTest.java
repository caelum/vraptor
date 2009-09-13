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

import java.lang.reflect.Type;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.iogi.Iogi;
import br.com.caelum.iogi.parameters.Parameter;
import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.iogi.util.DefaultLocaleProvider;
import br.com.caelum.iogi.util.NullDependencyProvider;

/**
 * Unfortunately OGNL sucks so bad in its design that we had to create a "unit"
 * test which accesses more than a single class to test the ognl funcionality.
 * Even worse, OGNL sucks with its static configuration methods in such a way
 * that tests are not thread safe. Summing up: OGNL api sucks, OGNL idea rulez.
 * This test is here to ensure generic support through our implementation using
 * OGNL.
 *
 * @author Guilherme Silveira
 */
public class IogiGenericTypesSupportTest {
	private Iogi iogi;

    @Before
    public void setup() {
        this.iogi = new Iogi(new NullDependencyProvider(), new DefaultLocaleProvider());
    }

    public static class Cat {
        private List<String> legLength;

        public void setLegLength(List<String> legLength) {
            this.legLength = legLength;
        }

        public List<String> getLegLength() {
            return legLength;
        }

        public void setLegs(List<Leg> legs) {
            this.legs = legs;
        }

        public List<Leg> getLegs() {
            return legs;
        }

        public void setIds(Long[] ids) {
            this.ids = ids;
        }

        public Long[] getIds() {
            return ids;
        }

        public void setEyeColorCode(List<Long> eyeColorCode) {
            this.eyeColorCode = eyeColorCode;
        }

        public List<Long> getEyeColorCode() {
            return eyeColorCode;
        }

        private List<Leg> legs;
        private Long[] ids;
        private List<Long> eyeColorCode;
    }

    public static class Leg {
        private String color;

        public void setColor(String color) {
            this.color = color;
        }

        public String getColor() {
            return color;
        }
    }

    @Test
    public void canInstantiatingStringsInAListSettingItsInternalValueWithoutInvokingConverters1() throws Exception {
        final Type type = ContainsParameterizedList.class.getField("listOfString").getGenericType();
		final Target<List<String>> target = new Target<List<String>>(type, "legLength");
		final List<String> legs = iogi.instantiate(target , new Parameter("legLength[0]", "small"), new Parameter("legLength[1]", "big"));
        assertThat(legs.get(1), is(equalTo("big")));
    }
	
	@Test
	public void canInstantiatingStringsInAListSettingItsInternalValueWithoutInvokingConverters2() throws Exception {
		final Target<Cat> target = Target.create(Cat.class, "cat");
		final Cat cat = iogi.instantiate(target , new Parameter("cat.legLength[0]", "small"), new Parameter("cat.legLength[1]", "big"));
		assertThat(cat.legLength.get(1), is(equalTo("big")));
	}
	
	@Test
    public void canInstantiateAndPopulateAnArrayOfWrappers1() {
		final Target<long[]> target = Target.create(long[].class, "ids");
		final long[] ids = iogi.instantiate(target, new Parameter("ids[0]", "3"), new Parameter("ids[1]", "5"));
		assertThat(ids[0], is(equalTo(3L)));
        assertThat(ids[1], is(equalTo(5L)));
    }
	
	@Test
	public void canInstantiateAndPopulateAnArrayOfWrappers2() {
		final Target<Cat> target = Target.create(Cat.class, "cat");
		final Cat cat = iogi.instantiate(target, new Parameter("cat.ids[0]", "3"), new Parameter("cat.ids[1]", "5"));
		assertThat(cat.ids[0], is(equalTo(3L)));
		assertThat(cat.ids[1], is(equalTo(5L)));
	}

    @Test
    public void canInstantiateAndPopulateAListOfWrappers1() throws Exception {
    	final Type type = ContainsParameterizedList.class.getField("listOfLong").getGenericType();
		final Target<List<Long>> target = new Target<List<Long>>(type, "eyeColorCode");
    	final List<Long> eyeColorCode = iogi.instantiate(target, new Parameter("eyeColorCode[0]", "3"), new Parameter("eyeColorCode[1]", "5"));
        assertThat(eyeColorCode.get(0), is(equalTo(3L)));
        assertThat(eyeColorCode.get(1), is(equalTo(5L)));
    }
    
    @Test
    public void canInstantiateAndPopulateAListOfWrappers() {
    	final Target<Cat> target = Target.create(Cat.class, "myCat");
        final Cat myCat = iogi.instantiate(target, new Parameter("myCat.eyeColorCode[0]", "3"), new Parameter("myCat.eyeColorCode[1]", "5"));
		assertThat(myCat.eyeColorCode.get(0), is(equalTo(3L)));
        assertThat(myCat.eyeColorCode.get(1), is(equalTo(5L)));
    }
    
    static class ContainsParameterizedList {
    	public List<Integer> listOfInteger;
    	public List<Long> listOfLong;
    	public List<String> listOfString;
    }
}
