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
package br.com.caelum.vraptor.http;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import ognl.OgnlException;

import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.VRaptorMockery;
import br.com.caelum.vraptor.converter.LongConverter;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.interceptor.VRaptorMatchers;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.ValidationMessage;

public class OgnlParametersProviderTest {

	private VRaptorMockery mockery;
	private Converters converters;
	private OgnlParametersProvider provider;
	private TypeCreator creator;
	private Container container;
	private ParameterNameProvider nameProvider;
	private HttpServletRequest parameters;
	private EmptyElementsRemoval removal;
	private ArrayList<ValidationMessage> errors;

	@SuppressWarnings("unchecked")
	@Before
	public void setup() {
		this.mockery = new VRaptorMockery();
		this.converters = mockery.mock(Converters.class);
		this.parameters = mockery.mock(HttpServletRequest.class);
		this.creator = mockery.mock(TypeCreator.class);
		this.nameProvider = mockery.mock(ParameterNameProvider.class);
		this.container = mockery.mock(Container.class);
		this.removal = new EmptyElementsRemoval();
		this.provider = new OgnlParametersProvider(creator, container, converters, nameProvider, parameters, removal);
		this.errors = new ArrayList<ValidationMessage>();
		mockery.checking(new Expectations() {
			{
				allowing(converters).to((Class) with(an(Class.class)), with(any(Container.class)));
				will(returnValue(new LongConverter()));
			}
		});
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

	class MyResource {
		void buyA(House house) {
		}
	}

	public static class BuyASetter {
		private House House_;

		public void setHouse(House house_) {
			House_ = house_;
		}

		public House getHouse() {
			return House_;
		}
	}

	@Test
	public void isCapableOfDealingWithEmptyParameterForInternalWrapperValue() throws OgnlException,
			NoSuchMethodException {
		final Method method = MyResource.class.getDeclaredMethod("buyA", House.class);
		final Matcher<ResourceMethod> resourceMethod = VRaptorMatchers.resourceMethod(method);
		mockery.checking(new Expectations() {
			{
				one(parameters).getParameterValues("house.cat.id");
				will(returnValue(new String[] { "guilherme" }));
				one(parameters).getParameterNames();
				will(returnValue(enumFor("house.cat.id")));
				one(creator).typeFor(with(resourceMethod));
				will(returnValue(BuyASetter.class));
				one(nameProvider).parameterNamesFor(method);
				will(returnValue(new String[] { "House" }));
			}
		});

		Object[] params = provider.getParametersFor(mockery.methodFor(MyResource.class, "buyA", House.class), errors,
				null);
		House house = (House) params[0];
		assertThat(house.cat.id, is(equalTo("guilherme")));
		mockery.assertIsSatisfied();
	}

    @Test
    public void removeFromTheCollectionIfAnElementIsCreatedWithinACollectionButNoFieldIsSet() throws SecurityException,
            NoSuchMethodException {
        final Method method = MyResource.class.getDeclaredMethod("buyA", House.class);
        final Matcher<ResourceMethod> resourceMethod = VRaptorMatchers.resourceMethod(method);
        mockery.checking(new Expectations() {
            {
                one(parameters).getParameterValues("house.extraCats[1].id");
                will(returnValue(new String[] { "guilherme" }));
                one(parameters).getParameterNames();
                will(returnValue(enumFor("house.extraCats[1].id")));
                one(creator).typeFor(with(resourceMethod));
                will(returnValue(BuyASetter.class));
                one(nameProvider).parameterNamesFor(method);
                will(returnValue(new String[] { "House" }));
                allowing(container).instanceFor(EmptyElementsRemoval.class); will(returnValue(removal));
            }
        });
        Object[] params = provider.getParametersFor(mockery.methodFor(MyResource.class, "buyA", House.class), errors, null);
        House house = (House) params[0];
        assertThat(house.extraCats, hasSize(1));
        assertThat(house.extraCats.get(0).id, is(equalTo("guilherme")));
        mockery.assertIsSatisfied();
    }

	@SuppressWarnings("unchecked")
	private Enumeration enumFor(String... values) {
		return new Vector(Arrays.asList(values)).elements();
	}

	@Test
	public void removeFromTheCollectionIfAnElementIsCreatedWithinAnArrayButNoFieldIsSet() throws SecurityException,
			NoSuchMethodException {
		final Method method = MyResource.class.getDeclaredMethod("buyA", House.class);
		final Matcher<ResourceMethod> resourceMethod = VRaptorMatchers.resourceMethod(method);
		mockery.checking(new Expectations() {
			{
				one(parameters).getParameterValues("house.ids[1]");
				will(returnValue(new String[] { "3" }));
				one(parameters).getParameterNames();
				will(returnValue(enumFor("house.ids[1]" )));
				one(creator).typeFor(with(resourceMethod));
				will(returnValue(BuyASetter.class));
				one(nameProvider).parameterNamesFor(method);
				will(returnValue(new String[] { "House" }));
				one(container).instanceFor(EmptyElementsRemoval.class);
				will(returnValue(removal));
			}
		});
		Object[] params = provider.getParametersFor(mockery.methodFor(MyResource.class, "buyA", House.class), errors,
				null);
		House house = (House) params[0];
		assertThat(house.ids.length, is(equalTo(1)));
		assertThat(house.ids[0], is(equalTo(3L)));
		mockery.assertIsSatisfied();
	}

	@Test
	public void removeFromTheCollectionIfAnElementIsCreatedWithinACollectionButNoFieldIsSetAppartFromTheValueItselfNotAChild()
			throws SecurityException, NoSuchMethodException {
		final Method method = MyResource.class.getDeclaredMethod("buyA", House.class);
		final Matcher<ResourceMethod> resourceMethod = VRaptorMatchers.resourceMethod(method);
		mockery.checking(new Expectations() {
			{
				one(parameters).getParameterValues("house.owners[1]");
				will(returnValue(new String[] { "guilherme" }));
				one(parameters).getParameterNames();
				will(returnValue(enumFor( "house.owners[1]" )));
				one(creator).typeFor(with(resourceMethod));
				will(returnValue(BuyASetter.class));
				one(nameProvider).parameterNamesFor(method);
				will(returnValue(new String[] { "House" }));
				one(container).instanceFor(EmptyElementsRemoval.class);
				will(returnValue(removal));
			}
		});
		Object[] params = provider.getParametersFor(mockery.methodFor(MyResource.class, "buyA", House.class), errors,
				null);
		House house = (House) params[0];
		assertThat(house.owners, hasSize(1));
		assertThat(house.owners.get(0), is(equalTo("guilherme")));
		mockery.assertIsSatisfied();
	}

}
