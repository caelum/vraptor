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

import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.vraptor.annotations.Component;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.route.DefaultRouter;
import br.com.caelum.vraptor.http.route.NoTypeFinder;
import br.com.caelum.vraptor.http.route.Route;
import br.com.caelum.vraptor.proxy.DefaultProxifier;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.resource.ResourceClass;
import br.com.caelum.vraptor.test.VRaptorMockery;

public class ComponentRoutesCreatorTest {

    private VRaptorMockery mockery;
	private DefaultRouter router;
	private MutableRequest request;
    private Proxifier proxifier;
	private Converters converters;
	private ComponentRoutesParser parser;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
        this.request = mockery.mock(MutableRequest.class);
        this.converters = mockery.mock(Converters.class);
        this.proxifier = new DefaultProxifier();

        parser = new ComponentRoutesParser(proxifier, new NoTypeFinder(), converters);
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
        final ResourceClass resource = mockery.resource(VRaptor3Component.class);
        List<Route> rules = parser.rulesFor(resource);

        assertThat(rules, hasRouteMatching("/vRaptor3Component/name"));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldThrowExceptionIfNotFound() throws SecurityException, NoSuchMethodException {
        final ResourceClass resource = mockery.resource(NonVRaptorComponent.class);
        List<Route> rules = parser.rulesFor(resource);

        assertThat(rules, not(hasRouteMatching("/NonVRaptorComponent/name")));
		mockery.assertIsSatisfied();
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
    	final ResourceClass resource = mockery.resource(MyResource.class);
    	List<Route> rules = parser.rulesFor(resource);

        assertThat(rules, not(hasRouteMatching("/MyResource.ignorableProtected.logic")));
		mockery.assertIsSatisfied();
    }

    @Test
    public void ignoresGetters() {
    	final ResourceClass resource = mockery.resource(MyResource.class);
    	List<Route> rules = parser.rulesFor(resource);

    	assertThat(rules, not(hasRouteMatching("/MyResource.getValue.logic")));
		mockery.assertIsSatisfied();
    }

    @Test
    public void ignoresStaticMethod() {
    	final ResourceClass resource = mockery.resource(MyResource.class);
    	List<Route> rules = parser.rulesFor(resource);

    	assertThat(rules, not(hasRouteMatching("/MyResource.ignorableStatic.logic")));
		mockery.assertIsSatisfied();
    }

    @Test
    public void returnsNullIfNothingFound() {
    	final ResourceClass resource = mockery.resource(MyResource.class);
    	List<Route> rules = parser.rulesFor(resource);

    	assertThat(rules, not(hasRouteMatching("/MyResource.iDontExist.logic")));
    }

    @Test
    public void returnsTheCorrectDefaultResourceMethodIfFound() throws SecurityException, NoSuchMethodException {
        final ResourceClass resource = mockery.resource(MyResource.class);
        List<Route> rules = parser.rulesFor(resource);

        assertThat(rules, hasRouteMatching("/MyResource.findable.logic"));
        mockery.assertIsSatisfied();
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
