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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Test;
import org.mockito.Mock;

import br.com.caelum.iogi.parameters.Parameter;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.core.SafeResourceBundle;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.http.ParametersProviderTest;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.util.EmptyBundle;

public class IogiParametersProviderTest extends ParametersProviderTest {
	private @Mock Localization mockLocalization;

	@Override
	protected ParametersProvider getProvider() {
		when(mockLocalization.getBundle()).thenReturn(new SafeResourceBundle(new EmptyBundle()));
		return new IogiParametersProvider(nameProvider, request, new VRaptorInstantiator(converters, new VRaptorDependencyProvider(container), mockLocalization, new VRaptorParameterNamesProvider(nameProvider), request));
	}

    @Test
    public void returnsNullWhenInstantiatingAStringForWhichThereAreNoParameters() throws Exception {
    	thereAreNoParameters();
    	final ResourceMethod method = string;

    	Object[] params = provider.getParametersFor(method, errors, null);

    	assertArrayEquals(new Object[] {null}, params);
    }

	@Test
	public void canInjectADependencyProvidedByVraptor() throws Exception {
		thereAreNoParameters();

		ResourceMethod resourceMethod = DefaultResourceMethod.instanceFor(OtherResource.class, OtherResource.class.getDeclaredMethod("logic", NeedsMyResource.class));
    	final MyResource providedInstance = new MyResource();

    	when(container.canProvide(MyResource.class)).thenReturn(true);
    	when(container.instanceFor(MyResource.class)).thenReturn(providedInstance);

    	Object[] params = provider.getParametersFor(resourceMethod, errors, null);
		assertThat(((NeedsMyResource)params[0]).getMyResource(), is(sameInstance(providedInstance)));
	}
    //---------- The Following tests mock iogi to unit test the ParametersProvider impl.
	@Test
	public void willCreateAnIogiParameterForEachRequestParameterValue() throws Exception {
		ResourceMethod anyMethod = buyA;
		requestParameterIs(anyMethod, "name", "a", "b");

		final InstantiatorWithErrors mockInstantiator = mock(InstantiatorWithErrors.class);
		final Parameters expectedParamters = new Parameters(
				Arrays.asList(new Parameter("name", "a"), new Parameter("name", "b")));

		IogiParametersProvider iogiProvider = new IogiParametersProvider(nameProvider, request, mockInstantiator);

		iogiProvider.getParametersFor(anyMethod, errors, null);

		verify(mockInstantiator).instantiate(any(Target.class), eq(expectedParamters), eq(errors));
	}

	@Test
	public void willCreateATargerForEachFormalParameterDeclaredByTheMethod() throws Exception {
		final ResourceMethod buyAHouse = buyA;
		requestParameterIs(buyAHouse, "house", "");

		final InstantiatorWithErrors mockInstantiator = mock(InstantiatorWithErrors.class);
		IogiParametersProvider iogiProvider = new IogiParametersProvider(nameProvider, request, mockInstantiator);
		final Target<House> expectedTarget = Target.create(House.class, "house");

		iogiProvider.getParametersFor(buyAHouse, errors, null);

		verify(mockInstantiator).instantiate(eq(expectedTarget), any(Parameters.class), eq(errors));
	}

	@Test
	public void willAddValidationMessagesForConversionErrors() throws Exception {
		ResourceMethod setId = simple;
		requestParameterIs(setId, "id", "asdf");

		getParameters(setId);

		assertThat(errors.size(), is(1));
	}

	@Test
	public void inCaseOfConversionErrorsOnlyNullifyTheProblematicParameter() throws Exception {
		ResourceMethod setId = DefaultResourceMethod.instanceFor(House.class, House.class.getMethod("setCat", Cat.class));
		requestParameterIs(setId, "cat.lols", "sad kitten");

		Cat cat = getParameters(setId);
		assertThat(cat, is(notNullValue()));
		assertThat(cat.getLols(), is(nullValue()));
	}
	//----------

	class OtherResource {
    	void logic(NeedsMyResource param) {
    	}
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
}