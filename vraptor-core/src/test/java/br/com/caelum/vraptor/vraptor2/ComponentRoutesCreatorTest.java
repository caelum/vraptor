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

package br.com.caelum.vraptor.vraptor2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.vraptor.annotations.Component;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.route.DefaultRouteBuilder;
import br.com.caelum.vraptor.http.route.JavaEvaluator;
import br.com.caelum.vraptor.http.route.NoTypeFinder;
import br.com.caelum.vraptor.http.route.Route;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.proxy.JavassistProxifier;
import br.com.caelum.vraptor.proxy.ObjenesisInstanceCreator;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.resource.DefaultResourceClass;
import br.com.caelum.vraptor.resource.ResourceClass;

public class ComponentRoutesCreatorTest {

    private Proxifier proxifier;
	private @Mock Converters converters;
	private ComponentRoutesParser parser;
	private NoTypeFinder typeFinder;
	private @Mock Router router;
	private @Mock ParameterNameProvider nameProvider;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        this.proxifier = new JavassistProxifier(new ObjenesisInstanceCreator());
        this.typeFinder = new NoTypeFinder();

        when(router.builderFor(anyString())).thenAnswer(new Answer<DefaultRouteBuilder>() {


			public DefaultRouteBuilder answer(InvocationOnMock invocation) throws Throwable {
				return new DefaultRouteBuilder(proxifier, typeFinder, converters, nameProvider, new JavaEvaluator(), (String) invocation.getArguments()[0]);
			}
		});


        parser = new ComponentRoutesParser(router);
    }

    class NonVRaptorComponent {
        public void name() {
        }
    }

    @Resource
    class VRaptor3Component {
        public void name() {
        }
    }

    @Test
    public void shouldUseVRaptor3AlgorithmIfNotAVRaptor2Component() throws SecurityException, NoSuchMethodException {
        final ResourceClass resource = new DefaultResourceClass(VRaptor3Component.class);
        List<Route> rules = parser.rulesFor(resource);

        assertThat(rules, hasRouteMatching("/vRaptor3Component/name"));

    }

    @Test
    public void shouldThrowExceptionIfNotFound() throws SecurityException, NoSuchMethodException {
        final ResourceClass resource = new DefaultResourceClass(NonVRaptorComponent.class);
        List<Route> rules = parser.rulesFor(resource);

        assertThat(rules, not(hasRouteMatching("/NonVRaptorComponent/name")));

    }

    @Component
    static class MyResource {
        public static void ignorableStatic() {
        }

        protected void ignorableProtected() {
        }

        @Path("/findable")
        public void findable() {
        }
        public String getValue() {
        	return "";
        }
    }

    @Test
    public void ignoresNonPublicMethod() {
    	final ResourceClass resource = new DefaultResourceClass(MyResource.class);
    	List<Route> rules = parser.rulesFor(resource);

        assertThat(rules, not(hasRouteMatching("/MyResource.ignorableProtected.logic")));

    }

    @Test
    public void ignoresGetters() {
    	final ResourceClass resource = new DefaultResourceClass(MyResource.class);
    	List<Route> rules = parser.rulesFor(resource);

    	assertThat(rules, not(hasRouteMatching("/MyResource.getValue.logic")));

    }

    @Test
    public void ignoresStaticMethod() {
    	final ResourceClass resource = new DefaultResourceClass(MyResource.class);
    	List<Route> rules = parser.rulesFor(resource);

    	assertThat(rules, not(hasRouteMatching("/MyResource.ignorableStatic.logic")));

    }

    @Test
    public void returnsNullIfNothingFound() {
    	final ResourceClass resource = new DefaultResourceClass(MyResource.class);
    	List<Route> rules = parser.rulesFor(resource);

    	assertThat(rules, not(hasRouteMatching("/MyResource.iDontExist.logic")));
    }

    @Test
    public void returnsTheCorrectDefaultResourceMethodIfFound() throws SecurityException, NoSuchMethodException {
        final ResourceClass resource = new DefaultResourceClass(MyResource.class);
        List<Route> rules = parser.rulesFor(resource);

        assertThat(rules, hasRouteMatching("/MyResource.findable.logic"));

    }

    private Matcher<List<Route>> hasRouteMatching(final String uri) {
    	return new TypeSafeMatcher<List<Route>>() {

			@Override
			protected void describeMismatchSafely(List<Route> item, Description mismatchDescription) {
			}

			@Override
			protected boolean matchesSafely(List<Route> item) {
				for (Route route : item) {
					if (route.canHandle(uri)) {
						return true;
					}
				}
				return false;
			}

			public void describeTo(Description description) {
				description.appendText("a list of routes matching " + uri);

			}
		};
    }

}
