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

package br.com.caelum.vraptor.http.ognl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.converter.LongConverter;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.http.InvalidParameterException;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.DefaultValidationException;
import br.com.caelum.vraptor.validator.Message;

@RunWith(MockitoJUnitRunner.class)
public class OgnlParametersProviderTest {

    private @Mock Converters converters;
    private @Mock Container container;
    private @Mock ParameterNameProvider nameProvider;
    private @Mock HttpServletRequest parameters;

    private EmptyElementsRemoval removal;
    private ArrayList<Message> errors;
    private OgnlParametersProvider provider;
	private ResourceMethod buyA;
	private ResourceMethod kick;
	private ResourceMethod error;
	private ResourceMethod array;
	private ResourceMethod simple;

	private @Mock HttpSession session;
	private ResourceMethod list;
	private ResourceMethod listOfObject;
	private ResourceMethod string;

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Before
    public void setup() throws Exception {
        this.removal = new EmptyElementsRemoval();
        this.provider = new OgnlParametersProvider(container, converters, nameProvider, parameters, removal);
        this.errors = new ArrayList<Message>();

        when(converters.to(Long.class)).thenReturn((Converter) new LongConverter());
        when(parameters.getSession()).thenReturn(session);
        when(container.instanceFor(EmptyElementsRemoval.class)).thenReturn(removal);
        when(container.instanceFor(Converters.class)).thenReturn(converters);

        buyA = DefaultResourceMethod.instanceFor(MyResource.class, MyResource.class.getDeclaredMethod("buyA", House.class));
        kick = DefaultResourceMethod.instanceFor(MyResource.class, MyResource.class.getDeclaredMethod("kick", AngryCat.class));
        error = DefaultResourceMethod.instanceFor(MyResource.class, MyResource.class.getDeclaredMethod("error", WrongCat.class));
        array = DefaultResourceMethod.instanceFor(MyResource.class, MyResource.class.getDeclaredMethod("array", Long[].class));
        list = DefaultResourceMethod.instanceFor(MyResource.class, MyResource.class.getDeclaredMethod("list", List.class));
        listOfObject = DefaultResourceMethod.instanceFor(MyResource.class, MyResource.class.getDeclaredMethod("listOfObject", List.class));
        simple = DefaultResourceMethod.instanceFor(MyResource.class, MyResource.class.getDeclaredMethod("simple", Long.class));
        string = DefaultResourceMethod.instanceFor(MyResource.class, MyResource.class.getDeclaredMethod("string", String.class));
    }

    @Test
    public void isCapableOfDealingWithStrings() throws Exception {
    	requestParameterIs(string, "abc", "eureka");

    	String abc = getParameters(string);

    	assertThat(abc, is("eureka"));
    }
    @Test
    public void isCapableOfDealingWithIndexedLists() throws Exception {
    	requestParameterIs(list, "abc[2]", "1");

    	List<Long> abc = getParameters(list);

    	assertThat(abc, hasSize(1));
    	assertThat(abc, hasItem(1l));
    }

    @Test
    public void isCapableOfDealingWithIndexedListsOfObjects() throws Exception {
    	requestParameterIs(listOfObject, "abc[2].x", "1");

    	List<ABC> abc = getParameters(listOfObject);

    	assertThat(abc, hasSize(1));
    	assertThat(abc.get(0).x, is(1l));
    }

    @Test
    public void isCapableOfDealingWithLists() throws Exception {
    	requestParameterIs(list, "abc", "1");

    	List<Long> abc = getParameters(list);

    	assertThat(abc, hasSize(1));
    	assertThat(abc, hasItem(1l));
    }

    @Test
    public void isCapableOfDealingIndexedArraysWithOneElement() throws Exception {
    	requestParameterIs(array, "abc[2]", "1");

    	Long[] abc = getParameters(array);

    	assertThat(abc, is(arrayContaining(1l)));
    }

    @Test
    public void isCapableOfDealingArraysWithOneElement() throws Exception {
    	requestParameterIs(array, "abc", "1");

    	Long[] abc = getParameters(array);

    	assertThat(abc, is(arrayContaining(1l)));
    }

    @Test
    public void isCapableOfDealingArraysWithSeveralElements() throws Exception {
    	requestParameterIs(array, "abc", "1", "2", "3");

    	Long[] abc = getParameters(array);

    	assertThat(abc, is(arrayContaining(1l, 2l, 3l)));
    }

    @Test
    public void isCapableOfDealingWithEmptyParameterForInternalWrapperValue() throws Exception {
    	requestParameterIs(buyA, "house.cat.id", "guilherme");

        House house = getParameters(buyA);

        assertThat(house.cat.id, is(equalTo("guilherme")));
    }

    @Test
    public void removeFromTheCollectionIfAnElementIsCreatedWithinACollectionButNoFieldIsSet() throws Exception {

    	requestParameterIs(buyA, "house.extraCats[1].id", "guilherme");

        House house = getParameters(buyA);

        assertThat(house.extraCats, hasSize(1));
        assertThat(house.extraCats.get(0).id, is(equalTo("guilherme")));
    }

    @Test
    public void removeFromTheCollectionIfAnElementIsCreatedWithinAnArrayButNoFieldIsSet() throws Exception {

    	requestParameterIs(buyA, "house.ids[1]", "3");

    	House house = getParameters(buyA);

        assertThat(house.ids.length, is(equalTo(1)));
        assertThat(house.ids[0], is(equalTo(3L)));
    }

    @Test
    public void removeFromTheCollectionIfAnElementIsCreatedWithinACollectionButNoFieldIsSetAppartFromTheValueItselfNotAChild()
            throws Exception {
    	requestParameterIs(buyA, "house.owners[1]", "guilherme");

    	House house = getParameters(buyA);

    	assertThat(house.owners, hasSize(1));
        assertThat(house.owners.get(0), is(equalTo("guilherme")));
    }

    @Test
    public void addsValidationMessageWhenSetterFailsWithAValidationException() throws Exception {
    	requestParameterIs(kick, "angryCat.id", "guilherme");

    	getParameters(kick);

        assertThat(errors.size(), is(greaterThan(0)));
    }

    @Test(expected=InvalidParameterException.class)
    public void throwsExceptionWhenSetterFailsWithOtherException() throws Exception {
    	requestParameterIs(error, "wrongCat.id", "guilherme");

    	getParameters(error);
    }

    @Test
    public void returnsASimpleValue() throws Exception {
    	requestParameterIs(simple, "xyz", "42");

    	Long xyz = getParameters(simple);
    	assertThat(xyz, is(42l));
    }

    private void requestParameterIs(ResourceMethod method, String paramName, String... values) {
    	String methodName = paramName.replaceAll("[\\.\\[].*", "");

		when(parameters.getParameterValues(paramName)).thenReturn(values);
		String[] values1 = { paramName };
		when(parameters.getParameterNames()).thenReturn(Collections.enumeration(Arrays.asList(values1)));
		when(nameProvider.parameterNamesFor(method.getMethod())).thenReturn(new String[]{methodName});
		when(parameters.getParameterMap()).thenReturn(Collections.singletonMap(paramName, values));

    }


    @SuppressWarnings("unchecked")
	private <T> T getParameters(ResourceMethod method) {
		return (T) provider.getParametersFor(method, errors, null)[0];
	}

    static class MyResource {
        void buyA(House house) {
        }
        void kick(AngryCat angryCat) {
        }
        void error(WrongCat wrongCat) {
        }
        void array(Long[] abc) {
        }
        void list(List<Long> abc) {
        }
        void listOfObject(List<ABC> abc) {
        }
        void simple(Long xyz) {
        }
        void string(String abc) {
        }
    }

    public static class Cat {
        private String id;

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
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

        public void setExtraCats(List<Cat> extraCats) {
            this.extraCats = extraCats;
        }

        public List<Cat> getExtraCats() {
            return extraCats;
        }

        public void setIds(Long[] ids) {
            this.ids = ids;
        }

        private List<String> owners;

        public Long[] getIds() {
            return ids;
        }

        public void setOwners(List<String> owners) {
            this.owners = owners;
        }

        public List<String> getOwners() {
            return owners;
        }

        private List<Cat> extraCats;

        private Long[] ids;

    }

    public static class ABC {
		private Long x;

		public Long getX() {
			return x;
		}

		public void setX(Long x) {
			this.x = x;
		}
	}


    public static class AngryCat {
        public void setId(String id) {
        	throw new DefaultValidationException("AngryCat Exception");
        }

        public String getId() {
        	throw new DefaultValidationException("AngryCat Exception");
        }
    }

    public static class WrongCat {
        public void setId(String id) {
        	throw new IllegalArgumentException("AngryCat Exception"); //it isn't a ValidationException
        }

        public String getId() {
        	throw new IllegalArgumentException("AngryCat Exception"); //it isn't a ValidationException
        }
    }
}
