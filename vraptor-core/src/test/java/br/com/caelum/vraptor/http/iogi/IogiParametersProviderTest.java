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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertArrayEquals;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.iogi.Instantiator;
import br.com.caelum.iogi.parameters.Parameter;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.vraptor.converter.LongConverter;
import br.com.caelum.vraptor.converter.PrimitiveIntConverter;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.core.DefaultConverters;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.test.VRaptorMockery;
import br.com.caelum.vraptor.validator.Message;

public class IogiParametersProviderTest {
    private VRaptorMockery mockery;
    private Converters converters;
    private IogiParametersProvider iogiProvider;
    private ParameterNameProvider mockNameProvider;
    private HttpServletRequest mockHttpServletRequest;
    private ArrayList<Message> errors;
	private Container mockContainer;
	private Localization mockLocalization;

    @SuppressWarnings("unchecked")
    @Before
    public void setup() throws Exception {
        this.mockery = new VRaptorMockery(true);
        this.converters = mockery.mock(Converters.class);
        this.mockHttpServletRequest = mockery.mock(HttpServletRequest.class);
        this.mockNameProvider = mockery.mock(ParameterNameProvider.class);
        
        mockContainer = mockery.mock(Container.class);
		mockLocalization = mockery.mock(Localization.class);
	
		Instantiator<Object> instantiator = new VRaptorInstantiator(new DefaultConverters(), mockContainer, mockLocalization, mockNameProvider);
		
		this.iogiProvider = new IogiParametersProvider(mockNameProvider, mockHttpServletRequest, instantiator);
        this.errors = new ArrayList<Message>();
        mockery.checking(new Expectations() {
            {
                allowing(converters).to((Class) with(an(Class.class)), with(any(Container.class)));
                will(returnValue(new LongConverter()));
                
//                ignoring(mockLocalization);
            }
        });
        
        mockery.checking(new Expectations() {{
            final AccessibleObject houseConstructor = House.class.getConstructor();
			allowing(mockNameProvider).parameterNamesFor(houseConstructor);
            will(returnValue(new String[] {}));
            
            AccessibleObject catConstructor = Cat.class.getConstructor();
			allowing(mockNameProvider).parameterNamesFor(catConstructor);
            will(returnValue(new String[] {}));
        }});
    }

    @After
    public void tearDown() {
    	mockery.assertIsSatisfied();
    }
    
    public static class Dog {
    	private int id;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
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

    class MyResource {
        void buyA(House house) {
        }
        void doNothing() {}
        void kill(List<Cat> kittens) {}
    }
    
    
    static class NeedsMyResource {
    	private final MyResource myResource;

		public NeedsMyResource(MyResource myResource) {
			this.myResource = myResource;
		}
		
		public MyResource getMyResource() {
			return myResource;
		}
    }
    
    class OtherResource {
    	void logic(NeedsMyResource param) {
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
    public void isCapableOfDealingWithEmptyParameterForInternalWrapperValue() throws Exception {
        final Method method = MyResource.class.getDeclaredMethod("buyA", House.class);
        
        mockery.checking(new Expectations() {{
            one(mockHttpServletRequest).getParameterValues("house.cat.id");
            will(returnValue(new String[]{"guilherme"}));
            one(mockHttpServletRequest).getParameterNames();
            will(returnValue(enumerationFor("house.cat.id")));
            
            one(mockNameProvider).parameterNamesFor(method); 
            will(returnValue(new String[]{"house"})); 
            
        }});

        Object[] params = iogiProvider.getParametersFor(mockery.methodFor(MyResource.class, "buyA", House.class), errors,
                null);
        House house = (House) params[0];
        assertThat(house.cat.id, is(equalTo("guilherme")));
        mockery.assertIsSatisfied();
    }

    @Test
    public void removeFromTheCollectionIfAnElementIsCreatedWithinACollectionButNoFieldIsSet() throws Exception {
        final Method method = MyResource.class.getDeclaredMethod("buyA", House.class);
        mockery.checking(new Expectations() {
            {
                one(mockHttpServletRequest).getParameterValues("house.extraCats[1].id");
                will(returnValue(new String[]{"guilherme"}));
                one(mockHttpServletRequest).getParameterNames();
                will(returnValue(enumerationFor("house.extraCats[1].id")));
                one(mockNameProvider).parameterNamesFor(method);
                will(returnValue(new String[]{"house"}));
            }
        });
        Object[] params = iogiProvider.getParametersFor(mockery.methodFor(MyResource.class, "buyA", House.class), errors, null);
        House house = (House) params[0];
        assertThat(house.extraCats, hasSize(1));
        assertThat(house.extraCats.get(0).id, is(equalTo("guilherme")));
        mockery.assertIsSatisfied();
    }

    @SuppressWarnings("unchecked")
	public Enumeration enumerationFor(String... values) {
        return new Vector(Arrays.asList(values)).elements();
    }

    @Test
    public void removeFromTheCollectionIfAnElementIsCreatedWithinAnArrayButNoFieldIsSet() throws SecurityException,
            NoSuchMethodException {
        final Method method = MyResource.class.getDeclaredMethod("buyA", House.class);
        mockery.checking(new Expectations() {
            {
            	allowing(mockContainer).instanceFor(LongConverter.class);
            	will(returnValue(new LongConverter()));
            	
                one(mockHttpServletRequest).getParameterValues("house.ids[1]");
                will(returnValue(new String[]{"3"}));
                one(mockHttpServletRequest).getParameterNames();
                will(returnValue(enumerationFor("house.ids[1]")));
                one(mockNameProvider).parameterNamesFor(method);
                will(returnValue(new String[]{"house"}));
                
                ignoring(mockLocalization);
            }
        });
        Object[] params = iogiProvider.getParametersFor(mockery.methodFor(MyResource.class, "buyA", House.class), errors,
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
        mockery.checking(new Expectations() {
            {
                one(mockHttpServletRequest).getParameterValues("house.owners[1]");
                will(returnValue(new String[]{"guilherme"}));
                one(mockHttpServletRequest).getParameterNames();
                will(returnValue(enumerationFor("house.owners[1]")));
                one(mockNameProvider).parameterNamesFor(method);
                will(returnValue(new String[]{"house"}));
            }
        });
        Object[] params = iogiProvider.getParametersFor(mockery.methodFor(MyResource.class, "buyA", House.class), errors,
                null);
        House house = (House) params[0];
        assertThat(house.owners, hasSize(1));
        assertThat(house.owners.get(0), is(equalTo("guilherme")));
        mockery.assertIsSatisfied();
    }
    
    @Test
	public void returnsAnEmptyObjectArrayForZeroArityMethods() throws Exception {
        final ResourceMethod method = mockery.methodFor(MyResource.class, "doNothing");
        
        mockery.checking(new Expectations() {
            {
                ignoring(mockHttpServletRequest);
                ignoring(mockNameProvider);
            }
        });
        Object[] params = iogiProvider.getParametersFor(method, errors, null);
        
        assertArrayEquals(new Object[] {}, params);
    }
    
    @Test
    public void returnsNullWhenInstantiatingAListForWhichThereAreNoParameters() throws Exception {
    	final ResourceMethod method = mockery.methodFor(MyResource.class, "kill", List.class);
    	
    	mockery.checking(new Expectations() {
    		{
    			ignoring(mockHttpServletRequest);
    			allowing(mockNameProvider).parameterNamesFor(with(any(Method.class)));
    			will(returnValue(new String[] {"kittens"}));
    		}
    	});
    	Object[] params = iogiProvider.getParametersFor(method, errors, null);
    	
    	assertArrayEquals(new Object[] {null}, params);
    }
    
	@Test
	public void canInjectADependencyProvidedByVraptor() throws Exception {
    	ResourceMethod resourceMethod = mockery.methodFor(OtherResource.class, "logic", NeedsMyResource.class);
    	final MyResource providedInstance = new MyResource();
    	
    	mockery.checking(new Expectations() {{
    		allowing(mockHttpServletRequest).getParameterNames();
    		will(returnValue(enumerationFor()));
    		
    		allowing(mockNameProvider).parameterNamesFor(with(any(Method.class)));
    		will(returnValue(new String[] {"param"}));
    		
    		allowing(mockContainer).instanceFor(MyResource.class);
    		will(returnValue(providedInstance));
    	}});
    	
    	Object[] params = iogiProvider.getParametersFor(resourceMethod, errors, null);
		assertThat(((NeedsMyResource)params[0]).getMyResource(), is(sameInstance(providedInstance)));
	}
	
    //---------- The Following tests mock iogi to unit test the ParametersProvider impl. 
	@Test
	public void willCreateAnIogiParameterForEachRequestParameterValue() throws Exception {
		ResourceMethod anyMethod = mockery.methodFor(MyResource.class, "buyA", House.class);
		
		@SuppressWarnings("unchecked")
		final Instantiator<Object> mockInstantiator = mockery.mock(Instantiator.class);
		final String parameterName = "name";
		final Parameters expectedParamters = new Parameters(
				Arrays.asList(new Parameter(parameterName, "a"), new Parameter(parameterName, "b")));
		
		IogiParametersProvider iogiProvider = new IogiParametersProvider(mockNameProvider, mockHttpServletRequest, mockInstantiator);
		
		mockery.checking(new Expectations() {{
			allowing(mockHttpServletRequest).getParameterNames();
			will(returnValue(enumerationFor(parameterName)));
			
			allowing(mockHttpServletRequest).getParameterValues(parameterName);
			will(returnValue(new String[] {"a", "b"}));
			
			allowing(mockNameProvider).parameterNamesFor(with(any(Method.class)));
			will(returnValue(new String[] {parameterName}));
			
			one(mockInstantiator).instantiate(with(any(Target.class)), with(equal(expectedParamters)));
		}});
		
		iogiProvider.getParametersFor(anyMethod, errors, null);
	}
	
	@Test
	public void willCreateATargerForEachFormalParameterDeclaredByTheMethod() throws Exception {
		final ResourceMethod buyAHouse = mockery.methodFor(MyResource.class, "buyA", House.class);
		
		@SuppressWarnings("unchecked")
		final Instantiator<Object> mockInstantiator = mockery.mock(Instantiator.class);
		IogiParametersProvider iogiProvider = new IogiParametersProvider(mockNameProvider, mockHttpServletRequest, mockInstantiator);
		final Target<House> expectedTarget = Target.create(House.class, "house");
		
		mockery.checking(new Expectations() {{
			ignoring(mockHttpServletRequest);
			
			allowing(mockNameProvider).parameterNamesFor(with(any(Method.class)));
			will(returnValue(new String[] {"house"}));
			
			one(mockInstantiator).instantiate(with(equal(expectedTarget)), with(any(Parameters.class)));
		}});
		
		iogiProvider.getParametersFor(buyAHouse, errors, null);
	}
	
	@Test
	public void willAddValidationMessagesForConversionErrors() throws Exception {
		ResourceMethod setId = mockery.methodFor(Dog.class, "setId", int.class);
		
		final ResourceBundle resourceBundle = 
			mockery.stubResourceBundle(this, "is_not_a_valid_integer", "is_not_a_valid_integer");
		
		mockery.checking(new Expectations() {{
			allowing(mockHttpServletRequest).getParameterNames();
			will(returnValue(enumerationFor("id")));
			
			allowing(mockHttpServletRequest).getParameterValues("id");
			will(returnValue(new String[] {"asdf"}));
			
			allowing(mockNameProvider).parameterNamesFor(with(any(Method.class)));
			will(returnValue(new String [] {"id"}));
			
			allowing(mockContainer).instanceFor(PrimitiveIntConverter.class);
			will(returnValue(new PrimitiveIntConverter()));
			
			allowing(mockLocalization).getBundle();
			will(returnValue(resourceBundle));
		}});
		
		iogiProvider.getParametersFor(setId, errors, resourceBundle);
		assertThat(errors.size(), is(equalTo(1)));
	}
	//----------
}