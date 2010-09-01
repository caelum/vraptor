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

package br.com.caelum.vraptor.http.route;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.EnumSet;
import java.util.List;

import javax.annotation.Resource;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Delete;
import br.com.caelum.vraptor.Head;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.proxy.DefaultProxifier;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceClass;
import br.com.caelum.vraptor.test.VRaptorMockery;

public class PathAnnotationRoutesParserTest {

    private VRaptorMockery mockery;
    private ResourceClass resource;
    private MutableRequest request;
    private Proxifier proxifier;
	private Converters converters;
	private PathAnnotationRoutesParser parser;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
        this.resource = mockery.resource(ClientsController.class);
        this.request = mockery.mock(MutableRequest.class);
        this.converters = mockery.mock(Converters.class);
        this.proxifier = new DefaultProxifier();
        parser = new PathAnnotationRoutesParser(proxifier, new NoTypeFinder(), converters);
    }

    @Resource
    @Path("/prefix")
    public static class PathAnnotatedController {
    	public void withoutPath() {
    	}
    	@Path("/absolutePath")
    	public void withAbsolutePath() {
    	}
    	@Path("relativePath")
    	public void withRelativePath() {
    	}
    	@Path("")
    	public void withEmptyPath() {
    	}
    }
    @Resource
    @Path("/endSlash/")
    public static class EndSlashAnnotatedController {
    	public void withoutPath() {
    	}
    	@Path("/absolutePath")
    	public void withAbsolutePath() {
    	}
    	@Path("relativePath")
    	public void withRelativePath() {
    	}
    	@Path("")
    	public void withEmptyPath() {
    	}
    }

    @Resource
    @Path("prefix")
    public static class WrongPathAnnotatedController {
    	public void noSlashPath() {
    	}
    }

    @Resource
    @Path({"/prefix", "/prefix2"})
    public static class MoreThanOnePathAnnotatedController {
    	public void noSlashPath() {
    	}
    }

    @Test(expected = IllegalArgumentException.class)
    public void addsAPrefixToMethodsWhenTheControllerHasMoreThanOneAnnotatedPath() throws Exception {
    	parser.rulesFor(mockery.resource(MoreThanOnePathAnnotatedController.class));
    }

    @Test
    public void addsAPrefixToMethodsWhenTheControllerAndTheMethodAreAnnotatedWithRelativePath() throws Exception {
    	List<Route> routes = parser.rulesFor(mockery.resource(PathAnnotatedController.class));
    	Route route = getRouteMatching(routes, "/prefix/relativePath");

    	assertThat(route, canHandle(PathAnnotatedController.class, "withRelativePath"));
    	mockery.assertIsSatisfied();
    }

	@Test
    public void addsAPrefixToMethodsWhenTheControllerEndsWithSlashAndTheMethodAreAnnotatedWithRelativePath() throws Exception {
		List<Route> routes = parser.rulesFor(mockery.resource(EndSlashAnnotatedController.class));
		Route route = getRouteMatching(routes, "/endSlash/relativePath");

		assertThat(route, canHandle(EndSlashAnnotatedController.class, "withRelativePath"));
    	mockery.assertIsSatisfied();
    }
    @Test
    public void addsAPrefixToMethodsWhenTheControllerEndsWithSlashAndTheMethodAreAnnotatedWithAbsolutePath() throws Exception {
    	List<Route> routes = parser.rulesFor(mockery.resource(EndSlashAnnotatedController.class));
    	Route route = getRouteMatching(routes, "/endSlash/absolutePath");

    	assertThat(route, canHandle(EndSlashAnnotatedController.class, "withAbsolutePath"));
    	mockery.assertIsSatisfied();
    }
    @Test
    public void addsAPrefixToMethodsWhenTheControllerEndsWithSlashAndTheMethodAreAnnotatedWithEmptyPath() throws Exception {
    	List<Route> routes = parser.rulesFor(mockery.resource(EndSlashAnnotatedController.class));
    	Route route = getRouteMatching(routes, "/endSlash/");

    	assertThat(route, canHandle(EndSlashAnnotatedController.class, "withEmptyPath"));
    	mockery.assertIsSatisfied();
    }
    public void addsAPrefixToMethodsWhenTheControllerEndsWithSlashAndTheMethodAreNotAnnotated() throws Exception {
    	List<Route> routes = parser.rulesFor(mockery.resource(EndSlashAnnotatedController.class));
    	Route route = getRouteMatching(routes, "/endSlash/withoutPath");

    	assertThat(route, canHandle(EndSlashAnnotatedController.class, "withoutPath"));
    	mockery.assertIsSatisfied();
    }
    @Test
    public void addsAPrefixToMethodsWhenTheControllerAndTheMethodAreAnnotatedWithAbsolutePath() throws Exception {
    	List<Route> routes = parser.rulesFor(mockery.resource(PathAnnotatedController.class));
    	Route route = getRouteMatching(routes, "/prefix/absolutePath");

    	assertThat(route, canHandle(PathAnnotatedController.class, "withAbsolutePath"));

    	mockery.assertIsSatisfied();
    }
    @Test
    public void addsAPrefixToMethodsWhenTheControllerAndTheMethodAreAnnotatedWithEmptyPath() throws Exception {
    	List<Route> routes = parser.rulesFor(mockery.resource(PathAnnotatedController.class));
    	Route route = getRouteMatching(routes, "/prefix");

    	assertThat(route, canHandle(PathAnnotatedController.class, "withEmptyPath"));

    	mockery.assertIsSatisfied();
    }
    @Test
    public void addsAPrefixToMethodsWhenTheControllerIsAnnotatedWithPath() throws Exception {
    	List<Route> routes = parser.rulesFor(mockery.resource(PathAnnotatedController.class));
    	Route route = getRouteMatching(routes, "/prefix/withoutPath");

    	assertThat(route, canHandle(PathAnnotatedController.class, "withoutPath"));
    	mockery.assertIsSatisfied();
    }
    @Test
    public void findsTheCorrectAnnotatedMethodIfThereIsNoWebMethodAnnotationPresent() throws Exception {
    	List<Route> routes = parser.rulesFor(mockery.resource(ClientsController.class));
    	Route route = getRouteMatching(routes, "/clients");

    	assertThat(route, canHandle(ClientsController.class, "list"));

        mockery.assertIsSatisfied();
    }


    @Test
    public void suportsTheDefaultNameForANonAnnotatedMethod() throws SecurityException,
            NoSuchMethodException {
    	List<Route> routes = parser.rulesFor(mockery.resource(ClientsController.class));
    	Route route = getRouteMatching(routes, "/clients/add");

    	assertThat(route, canHandle(ClientsController.class, "add"));

        mockery.assertIsSatisfied();
    }

    @Test
    public void ignoresTheControllerSuffixForANonAnnotatedMethod() throws SecurityException,
            NoSuchMethodException {
    	List<Route> routes = parser.rulesFor(mockery.resource(ClientsController.class));
    	Route route = getRouteMatching(routes, "/clients/add");

    	assertThat(route, canHandle(ClientsController.class, "add"));

        mockery.assertIsSatisfied();
    }
    @Test
    public void addsASlashWhenUserForgotIt() throws SecurityException,  NoSuchMethodException {
    	List<Route> routes = parser.rulesFor(mockery.resource(ClientsController.class));
    	Route route = getRouteMatching(routes, "/noSlash");

    	assertThat(route, canHandle(ClientsController.class, "noSlash"));

    	mockery.assertIsSatisfied();
    }

    @Test
    public void matchesWhenUsingAWildcard() throws SecurityException, NoSuchMethodException {
    	List<Route> routes = parser.rulesFor(mockery.resource(ClientsController.class));
    	Route route = getRouteMatching(routes, "/move/second/child");

    	assertThat(route, canHandle(ClientsController.class, "move"));
        mockery.assertIsSatisfied();
    }

    @Test
    public void dontRegisterRouteIfMethodIsNotPublic() {
    	List<Route> routes = parser.rulesFor(mockery.resource(ClientsController.class));
    	Route route = getRouteMatching(routes, "/protectMe");
    	assertNull(route);
		mockery.assertIsSatisfied();
    }

    @Test
    public void dontRegisterRouteIfMethodIsStatic() {
    	List<Route> routes = parser.rulesFor(mockery.resource(ClientsController.class));
    	Route route = getRouteMatching(routes, "/staticMe");
    	assertNull(route);
    	mockery.assertIsSatisfied();
    }

    @br.com.caelum.vraptor.Resource
    public static class ClientsController {
        @Path("/move/*/child")
        public void move() {
        }

        @Path("noSlash")
        public void noSlash() {
        }


        @Path("/clients")
        public void list() {
        }

        @Path("/clients/remove")
        @Delete
        public void remove() {
        }

        @Path("/clients/head")
        @Head
        public void head() {
        }

        public void add() {
        }

        @Path("/protectMe")
        protected void protectMe() {
        }

        @Path({"/path1", "/path2"})
        public void manyPaths() {
        }


        @Path("/staticMe")
        public static void staticMe() {
        }

        public void toInherit() {
        }
    }



    @br.com.caelum.vraptor.Resource
    public static class NoPath {

        @Path( {})
        public void noPaths() {
        }

    }
    @Test(expected=IllegalArgumentException.class)
    public void shouldThrowExceptionIfPathAnnotationHasEmptyArray()
            throws Exception {
        parser.rulesFor(mockery.resource(NoPath.class));
    }


    @Test
    public void shouldFindNonAnnotatedNonStaticPublicMethodWithComponentNameInVariableCamelCaseConventionAsURI()
            throws Exception {
    	List<Route> routes = parser.rulesFor(mockery.resource(ClientsController.class));
    	Route route = getRouteMatching(routes, "/clients/add");

    	assertThat(route, canHandle(ClientsController.class, "add"));

        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldFindSeveralPathsForMethodWithManyValue()
            throws Exception {
    	List<Route> routes = parser.rulesFor(mockery.resource(ClientsController.class));

    	Route route = getRouteMatching(routes, "/path1");
    	assertThat(route, canHandle(ClientsController.class, "manyPaths"));
    	Route route2 = getRouteMatching(routes, "/path2");
    	assertThat(route2, canHandle(ClientsController.class, "manyPaths"));

        mockery.assertIsSatisfied();
    }




    @Test
    public void shouldNotMatchIfAResourceHasTheWrongWebMethod() throws SecurityException {
    	List<Route> routes = parser.rulesFor(mockery.resource(ClientsController.class));
    	Route route = getRouteMatching(routes, "/clients/remove");

    	assertThat(route.allowedMethods(), not(contains(HttpMethod.POST)));
		mockery.assertIsSatisfied();
    }

    @Test
    public void shouldAcceptAResultWithASpecificWebMethod() throws SecurityException, NoSuchMethodException {
    	List<Route> routes = parser.rulesFor(mockery.resource(ClientsController.class));
    	Route route = getRouteMatching(routes, "/clients/head");

    	assertThat(route.allowedMethods(), is(EnumSet.of(HttpMethod.HEAD)));
        mockery.assertIsSatisfied();
    }

    static class NiceClients extends ClientsController {

    	@Override
    	public void add() {
    		super.add();
    	}
    }

    @Test
    public void findsInheritedMethodsWithDefaultNames() throws SecurityException, NoSuchMethodException {
    	List<Route> routes = parser.rulesFor(mockery.resource(NiceClients.class));
    	Route route = getRouteMatching(routes, "/niceClients/toInherit");

    	assertTrue(route.canHandle(NiceClients.class, ClientsController.class.getDeclaredMethod("toInherit")));

        mockery.assertIsSatisfied();
    }
    @Test
    public void supportMethodOverriding() throws SecurityException, NoSuchMethodException {
    	List<Route> routes = parser.rulesFor(mockery.resource(NiceClients.class));
    	Route route = getRouteMatching(routes, "/niceClients/add");

    	assertThat(route, canHandle(NiceClients.class, "add"));

    	mockery.assertIsSatisfied();
    }

    private Route getRouteMatching(List<Route> routes, String uri) {
    	for (Route route : routes) {
			if (route.canHandle(uri)) {
				return route;
			}
		}
		return null;
	}

    private Matcher<Route> canHandle(final Class<?> type, final String method) {
    	return new TypeSafeMatcher<Route>() {

			@Override
			protected void describeMismatchSafely(Route item, Description mismatchDescription) {
			}

			@Override
			protected boolean matchesSafely(Route item) {
				try {
					return item.canHandle(type, type.getDeclaredMethod(method));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

			public void describeTo(Description description) {
				description.appendText("a route which can handle ").appendValue(type).appendText(".").appendValue(method);
			}
		};
    }

}
