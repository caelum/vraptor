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

import static org.mockito.Mockito.when;

import org.mockito.Mock;

import br.com.caelum.iogi.Instantiator;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.core.SafeResourceBundle;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.http.ParametersProviderTest;
import br.com.caelum.vraptor.util.EmptyBundle;

public class IogiParametersProviderTest extends ParametersProviderTest {
    private IogiParametersProvider iogiProvider;

	private @Mock Localization mockLocalization;

	@Override
	protected ParametersProvider getProvider() {
		Instantiator<Object> instantiator = new VRaptorInstantiator(converters, container, mockLocalization, nameProvider, request);
		when(mockLocalization.getBundle()).thenReturn(new SafeResourceBundle(new EmptyBundle()));
		return new IogiParametersProvider(nameProvider, request, instantiator);
	}

//	@Before
////    @Override
//	public void setupx() throws Exception {
//		MockitoAnnotations.initMocks(this);
//		Instantiator<Object> instantiator = new VRaptorInstantiator(new DefaultConverters(container), container, mockLocalization, nameProvider, mockValidator );
//
//		this.iogiProvider = new IogiParametersProvider(nameProvider, request, instantiator);
//        this.errors = new ArrayList<Message>();
//
//        when(mockLocalization.getBundle()).thenReturn(new SafeResourceBundle(new EmptyBundle()));
//
//        when(nameProvider.parameterNamesFor(any(AccessibleObject.class))).thenReturn(new String[] {});
//    }
//
//	public Enumeration<String> enumerationFor(String... values) {
//        return Collections.enumeration(Arrays.asList(values));
//    }
//
//    @Override
//	@Test
//    public void removeFromTheCollectionIfAnElementIsCreatedWithinAnArrayButNoFieldIsSet() throws SecurityException,
//            NoSuchMethodException {
//        final Method method = MyResource.class.getDeclaredMethod("buyA", House.class);
//        mockery.checking(new Expectations() {
//            {
//            	allowing(container).instanceFor(LongConverter.class);
//            	will(returnValue(new LongConverter()));
//
//                one(request).getParameterValues("house.ids[1]");
//                will(returnValue(new String[]{"3"}));
//                one(request).getParameterNames();
//                will(returnValue(enumerationFor("house.ids[1]")));
//                one(nameProvider).parameterNamesFor(method);
//                will(returnValue(new String[]{"house"}));
//
//                ignoring(mockLocalization);
//            }
//        });
//        Object[] params = iogiProvider.getParametersFor(mockery.methodFor(MyResource.class, "buyA", House.class), errors,
//                null);
//        House house = (House) params[0];
//        assertThat(house.ids.length, is(equalTo(1)));
//        assertThat(house.ids[0], is(equalTo(3L)));
//        mockery.assertIsSatisfied();
//    }
//
//
//    @Test
//	public void returnsAnEmptyObjectArrayForZeroArityMethods() throws Exception {
//        final ResourceMethod method = mockery.methodFor(MyResource.class, "doNothing");
//
//        mockery.checking(new Expectations() {
//            {
//                ignoring(request);
//                ignoring(nameProvider);
//            }
//        });
//        Object[] params = iogiProvider.getParametersFor(method, errors, null);
//
//        assertArrayEquals(new Object[] {}, params);
//    }
//
//    @Test
//    public void returnsNullWhenInstantiatingAListForWhichThereAreNoParameters() throws Exception {
//    	final ResourceMethod method = mockery.methodFor(MyResource.class, "kill", List.class);
//
//    	mockery.checking(new Expectations() {
//    		{
//    			ignoring(request);
//    			allowing(nameProvider).parameterNamesFor(with(any(Method.class)));
//    			will(returnValue(new String[] {"kittens"}));
//    		}
//    	});
//    	Object[] params = iogiProvider.getParametersFor(method, errors, null);
//
//    	assertArrayEquals(new Object[] {null}, params);
//    }
//
//    @Test
//    public void returnsNullWhenInstantiatingAStringForWhichThereAreNoParameters() throws Exception {
//    	final ResourceMethod method = mockery.methodFor(Cat.class, "setId", String.class);
//
//    	mockery.checking(new Expectations() {
//    		{
//    			ignoring(request);
//    			allowing(nameProvider).parameterNamesFor(with(any(Method.class)));
//    			will(returnValue(new String[] {"kittens"}));
//    		}
//    	});
//    	Object[] params = iogiProvider.getParametersFor(method, errors, null);
//
//    	assertArrayEquals(new Object[] {null}, params);
//    }
//
//	@Test
//	public void canInjectADependencyProvidedByVraptor() throws Exception {
//    	ResourceMethod resourceMethod = mockery.methodFor(OtherResource.class, "logic", NeedsMyResource.class);
//    	final MyResource providedInstance = new MyResource();
//
//    	mockery.checking(new Expectations() {{
//    		allowing(request).getParameterNames();
//    		will(returnValue(enumerationFor()));
//
//    		allowing(nameProvider).parameterNamesFor(with(any(Method.class)));
//    		will(returnValue(new String[] {"param"}));
//
//    		allowing(container).canProvide(MyResource.class);
//    		will(returnValue(true));
//
//    		allowing(container).instanceFor(MyResource.class);
//    		will(returnValue(providedInstance));
//    	}});
//
//    	Object[] params = iogiProvider.getParametersFor(resourceMethod, errors, null);
//		assertThat(((NeedsMyResource)params[0]).getMyResource(), is(sameInstance(providedInstance)));
//	}
//
//    //---------- The Following tests mock iogi to unit test the ParametersProvider impl.
//	@Test
//	public void willCreateAnIogiParameterForEachRequestParameterValue() throws Exception {
//		ResourceMethod anyMethod = mockery.methodFor(MyResource.class, "buyA", House.class);
//
//		@SuppressWarnings("unchecked")
//		final Instantiator<Object> mockInstantiator = mockery.mock(Instantiator.class);
//		final String parameterName = "name";
//		final Parameters expectedParamters = new Parameters(
//				Arrays.asList(new Parameter(parameterName, "a"), new Parameter(parameterName, "b")));
//
//		IogiParametersProvider iogiProvider = new IogiParametersProvider(nameProvider, request, mockInstantiator);
//
//		mockery.checking(new Expectations() {{
//			allowing(request).getParameterNames();
//			will(returnValue(enumerationFor(parameterName)));
//
//			allowing(request).getParameterValues(parameterName);
//			will(returnValue(new String[] {"a", "b"}));
//
//			allowing(nameProvider).parameterNamesFor(with(any(Method.class)));
//			will(returnValue(new String[] {parameterName}));
//
//			one(mockInstantiator).instantiate(with(any(Target.class)), with(equal(expectedParamters)));
//		}});
//
//		iogiProvider.getParametersFor(anyMethod, errors, null);
//	}
//
//	@Test
//	public void willCreateATargerForEachFormalParameterDeclaredByTheMethod() throws Exception {
//		final ResourceMethod buyAHouse = mockery.methodFor(MyResource.class, "buyA", House.class);
//
//		@SuppressWarnings("unchecked")
//		final Instantiator<Object> mockInstantiator = mockery.mock(Instantiator.class);
//		IogiParametersProvider iogiProvider = new IogiParametersProvider(nameProvider, request, mockInstantiator);
//		final Target<House> expectedTarget = Target.create(House.class, "house");
//
//		mockery.checking(new Expectations() {{
//			ignoring(request);
//
//			allowing(nameProvider).parameterNamesFor(with(any(Method.class)));
//			will(returnValue(new String[] {"house"}));
//
//			one(mockInstantiator).instantiate(with(equal(expectedTarget)), with(any(Parameters.class)));
//		}});
//
//		iogiProvider.getParametersFor(buyAHouse, errors, null);
//	}
//
//	@Test
//	public void willAddValidationMessagesForConversionErrors() throws Exception {
//		ResourceMethod setId = mockery.methodFor(Dog.class, "setId", int.class);
//
//		final ResourceBundle resourceBundle =
//			mockery.stubResourceBundle(this, "is_not_a_valid_integer", "is_not_a_valid_integer");
//
//		mockery.checking(new Expectations() {{
//			allowing(request).getParameterNames();
//			will(returnValue(enumerationFor("id")));
//
//			allowing(request).getParameterValues("id");
//			will(returnValue(new String[] {"asdf"}));
//
//			allowing(nameProvider).parameterNamesFor(with(any(Method.class)));
//			will(returnValue(new String [] {"id"}));
//
//			allowing(container).instanceFor(PrimitiveIntConverter.class);
//			will(returnValue(new PrimitiveIntConverter()));
//
//			allowing(mockLocalization).getBundle();
//			will(returnValue(resourceBundle));
//
//			one(mockValidator).add(with(any(Message.class)));
//		}});
//
//		iogiProvider.getParametersFor(setId, errors, resourceBundle);
//	}
//
//	@Test
//	public void inCaseOfConversionErrorsOnlyNullifyTheProblematicParameter() throws Exception {
//		ResourceMethod setId = mockery.methodFor(House.class, "setCat", Cat.class);
//
//		final ResourceBundle resourceBundle =
//			mockery.stubResourceBundle(this, "is_not_a_valid_integer", "is_not_a_valid_integer");
//
//		mockery.checking(new Expectations() {{
//			allowing(request).getParameterNames();
//			will(returnValue(enumerationFor("cat.lols")));
//
//			allowing(request).getParameterValues("cat.lols");
//			will(returnValue(new String[] {"sad kitten"}));
//
//			allowing(nameProvider).parameterNamesFor(with(any(Method.class)));
//			will(returnValue(new String [] {"cat"}));
//
//			allowing(container).instanceFor(LongConverter.class);
//			will(returnValue(new LongConverter()));
//
//			allowing(mockLocalization).getBundle();
//			will(returnValue(resourceBundle));
//
//			one(mockValidator).add(with(any(Message.class)));
//		}});
//
//		Object[] results = iogiProvider.getParametersFor(setId, errors, resourceBundle);
//		Cat cat = (Cat) results[0];
//		assertThat(cat, is(notNullValue()));
//		assertThat(cat.getLols(), is(nullValue()));
//	}
	//----------
}