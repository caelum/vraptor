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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.EnumSet;
import java.util.List;

import javax.annotation.Resource;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import br.com.caelum.vraptor.Delete;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Head;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.proxy.JavassistProxifier;
import br.com.caelum.vraptor.proxy.ObjenesisInstanceCreator;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.resource.DefaultResourceClass;
import br.com.caelum.vraptor.resource.HttpMethod;

public class PathAnnotationRoutesParserTest {

    private Proxifier proxifier;
	private @Mock Converters converters;
	private NoTypeFinder typeFinder;
	private @Mock Router router;
	private @Mock ParameterNameProvider nameProvider;

	private PathAnnotationRoutesParser parser;

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

        parser = new PathAnnotationRoutesParser(router);
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
    @Path("/prefix")
    public static class GetAnnotatedController {
    	public void withoutPath() {
    	}
    	@Get("/absolutePath")
    	public void withAbsolutePath() {
    	}
    	@Get("relativePath")
    	public void withRelativePath() {
    	}
    	@Get("")
    	public void withEmptyPath() {
    	}
    }

    @Resource
    public static class WrongGetAnnotatedController {
    	@Path("/some") @Get("/other")
    	public void withAmbiguousDeclaration() {
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
    @Path("/endSlash/")
    public static class EndSlashAnnotatedGetController {
    	public void withoutPath() {
    	}
    	@Get("/absolutePath")
    	public void withAbsolutePath() {
    	}
    	@Get("relativePath")
    	public void withRelativePath() {
    	}
    	@Get("")
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
    	parser.rulesFor(new DefaultResourceClass(MoreThanOnePathAnnotatedController.class));
    }

    @Test
    public void addsAPrefixToMethodsWhenTheControllerAndTheMethodAreAnnotatedWithRelativePath() throws Exception {
    	List<Route> routes = parser.rulesFor(new DefaultResourceClass(PathAnnotatedController.class));
    	Route route = getRouteMatching(routes, "/prefix/relativePath");

    	assertThat(route, canHandle(PathAnnotatedController.class, "withRelativePath"));

    }

	@Test
    public void addsAPrefixToMethodsWhenTheControllerEndsWithSlashAndTheMethodAreAnnotatedWithRelativePath() throws Exception {
		List<Route> routes = parser.rulesFor(new DefaultResourceClass(EndSlashAnnotatedController.class));
		Route route = getRouteMatching(routes, "/endSlash/relativePath");

		assertThat(route, canHandle(EndSlashAnnotatedController.class, "withRelativePath"));

    }
    @Test
    public void addsAPrefixToMethodsWhenTheControllerEndsWithSlashAndTheMethodAreAnnotatedWithAbsolutePath() throws Exception {
    	List<Route> routes = parser.rulesFor(new DefaultResourceClass(EndSlashAnnotatedController.class));
    	Route route = getRouteMatching(routes, "/endSlash/absolutePath");

    	assertThat(route, canHandle(EndSlashAnnotatedController.class, "withAbsolutePath"));

    }
    @Test
    public void addsAPrefixToMethodsWhenTheControllerEndsWithSlashAndTheMethodAreAnnotatedWithEmptyPath() throws Exception {
    	List<Route> routes = parser.rulesFor(new DefaultResourceClass(EndSlashAnnotatedController.class));
    	Route route = getRouteMatching(routes, "/endSlash/");

    	assertThat(route, canHandle(EndSlashAnnotatedController.class, "withEmptyPath"));

    }
    public void addsAPrefixToMethodsWhenTheControllerEndsWithSlashAndTheMethodAreNotAnnotated() throws Exception {
    	List<Route> routes = parser.rulesFor(new DefaultResourceClass(EndSlashAnnotatedController.class));
    	Route route = getRouteMatching(routes, "/endSlash/withoutPath");

    	assertThat(route, canHandle(EndSlashAnnotatedController.class, "withoutPath"));

    }
    @Test
    public void addsAPrefixToMethodsWhenTheControllerAndTheMethodAreAnnotatedWithAbsolutePath() throws Exception {
    	List<Route> routes = parser.rulesFor(new DefaultResourceClass(PathAnnotatedController.class));
    	Route route = getRouteMatching(routes, "/prefix/absolutePath");

    	assertThat(route, canHandle(PathAnnotatedController.class, "withAbsolutePath"));


    }
    @Test
    public void addsAPrefixToMethodsWhenTheControllerAndTheMethodAreAnnotatedWithEmptyPath() throws Exception {
    	List<Route> routes = parser.rulesFor(new DefaultResourceClass(PathAnnotatedController.class));
    	Route route = getRouteMatching(routes, "/prefix");

    	assertThat(route, canHandle(PathAnnotatedController.class, "withEmptyPath"));


    }
    @Test
    public void addsAPrefixToMethodsWhenTheControllerIsAnnotatedWithPath() throws Exception {
    	List<Route> routes = parser.rulesFor(new DefaultResourceClass(PathAnnotatedController.class));
    	Route route = getRouteMatching(routes, "/prefix/withoutPath");

    	assertThat(route, canHandle(PathAnnotatedController.class, "withoutPath"));

    }
    @Test
    public void findsTheCorrectAnnotatedMethodIfThereIsNoWebMethodAnnotationPresent() throws Exception {
    	List<Route> routes = parser.rulesFor(new DefaultResourceClass(ClientsController.class));
    	Route route = getRouteMatching(routes, "/clients");

    	assertThat(route, canHandle(ClientsController.class, "list"));


    }


    @Test
    public void suportsTheDefaultNameForANonAnnotatedMethod() throws SecurityException,
            NoSuchMethodException {
    	List<Route> routes = parser.rulesFor(new DefaultResourceClass(ClientsController.class));
    	Route route = getRouteMatching(routes, "/clients/add");

    	assertThat(route, canHandle(ClientsController.class, "add"));


    }

    @Test
    public void ignoresTheControllerSuffixForANonAnnotatedMethod() throws SecurityException,
            NoSuchMethodException {
    	List<Route> routes = parser.rulesFor(new DefaultResourceClass(ClientsController.class));
    	Route route = getRouteMatching(routes, "/clients/add");

    	assertThat(route, canHandle(ClientsController.class, "add"));


    }
    @Test
    public void addsASlashWhenUserForgotIt() throws SecurityException,  NoSuchMethodException {
    	List<Route> routes = parser.rulesFor(new DefaultResourceClass(ClientsController.class));
    	Route route = getRouteMatching(routes, "/noSlash");

    	assertThat(route, canHandle(ClientsController.class, "noSlash"));


    }

    @Test
    public void matchesWhenUsingAWildcard() throws SecurityException, NoSuchMethodException {
    	List<Route> routes = parser.rulesFor(new DefaultResourceClass(ClientsController.class));
    	Route route = getRouteMatching(routes, "/move/second/child");

    	assertThat(route, canHandle(ClientsController.class, "move"));

    }

    @Test
    public void dontRegisterRouteIfMethodIsNotPublic() {
    	List<Route> routes = parser.rulesFor(new DefaultResourceClass(ClientsController.class));
    	Route route = getRouteMatching(routes, "/protectMe");
    	assertNull(route);

    }

    @Test
    public void dontRegisterRouteIfMethodIsStatic() {
    	List<Route> routes = parser.rulesFor(new DefaultResourceClass(ClientsController.class));
    	Route route = getRouteMatching(routes, "/staticMe");
    	assertNull(route);

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
        parser.rulesFor(new DefaultResourceClass(NoPath.class));
    }


    @Test
    public void shouldFindNonAnnotatedNonStaticPublicMethodWithComponentNameInVariableCamelCaseConventionAsURI()
            throws Exception {
    	List<Route> routes = parser.rulesFor(new DefaultResourceClass(ClientsController.class));
    	Route route = getRouteMatching(routes, "/clients/add");

    	assertThat(route, canHandle(ClientsController.class, "add"));


    }

    @Test
    public void shouldFindSeveralPathsForMethodWithManyValue()
            throws Exception {
    	List<Route> routes = parser.rulesFor(new DefaultResourceClass(ClientsController.class));

    	Route route = getRouteMatching(routes, "/path1");
    	assertThat(route, canHandle(ClientsController.class, "manyPaths"));
    	Route route2 = getRouteMatching(routes, "/path2");
    	assertThat(route2, canHandle(ClientsController.class, "manyPaths"));


    }




    @Test
    public void shouldNotMatchIfAResourceHasTheWrongWebMethod() throws SecurityException {
    	List<Route> routes = parser.rulesFor(new DefaultResourceClass(ClientsController.class));
    	Route route = getRouteMatching(routes, "/clients/remove");

    	assertThat(route.allowedMethods(), not(contains(HttpMethod.POST)));

    }

    @Test
    public void shouldAcceptAResultWithASpecificWebMethod() throws SecurityException, NoSuchMethodException {
    	List<Route> routes = parser.rulesFor(new DefaultResourceClass(ClientsController.class));
    	Route route = getRouteMatching(routes, "/clients/head");

    	assertThat(route.allowedMethods(), is(EnumSet.of(HttpMethod.HEAD)));

    }

    static class NiceClients extends ClientsController {

    	@Override
    	public void add() {
    		super.add();
    	}
    }

    @Test
    public void findsInheritedMethodsWithDefaultNames() throws SecurityException, NoSuchMethodException {
    	List<Route> routes = parser.rulesFor(new DefaultResourceClass(NiceClients.class));
    	Route route = getRouteMatching(routes, "/niceClients/toInherit");

    	assertTrue(route.canHandle(NiceClients.class, ClientsController.class.getDeclaredMethod("toInherit")));
    }
    @Test
    public void supportMethodOverriding() throws SecurityException, NoSuchMethodException {
    	List<Route> routes = parser.rulesFor(new DefaultResourceClass(NiceClients.class));
    	Route route = getRouteMatching(routes, "/niceClients/add");

    	assertThat(route, canHandle(NiceClients.class, "add"));
    }

    @Post
    static class AnnotatedController {

    	public void test() {}
    	@Get
    	public void overridden() {}
    }

    @Test
    public void supportTypeHttpMethodAnnotation() throws SecurityException, NoSuchMethodException {
    	List<Route> routes = parser.rulesFor(new DefaultResourceClass(AnnotatedController.class));
    	Route route = getRouteMatching(routes, "/annotated/test");
    	assertThat(route.allowedMethods(), is(EnumSet.of(HttpMethod.POST)));
    }

    @Test
    public void supportOverrideTypeHttpMethodAnnotation() throws SecurityException, NoSuchMethodException {
    	List<Route> routes = parser.rulesFor(new DefaultResourceClass(AnnotatedController.class));
    	Route route = getRouteMatching(routes, "/annotated/overridden");
    	assertThat(route.allowedMethods(), is(EnumSet.of(HttpMethod.GET)));
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

    @Test
    public void addsAPrefixToMethodsWhenTheGetControllerAndTheMethodAreAnnotatedWithRelativePath() throws Exception {
    	List<Route> routes = parser.rulesFor(new DefaultResourceClass(GetAnnotatedController.class));
    	Route route = getRouteMatching(routes, "/prefix/relativePath");

    	assertThat(route, canHandle(GetAnnotatedController.class, "withRelativePath"));
    }

    @Test
    public void priorityForGetAnnotationShouldBeDefault() throws Exception {
    	List<Route> routes = parser.rulesFor(new DefaultResourceClass(GetAnnotatedController.class));
    	Route route = getRouteMatching(routes, "/prefix/relativePath");

    	assertThat(route.getPriority(), is(Path.DEFAULT));
    }

	@Test
    public void addsAPrefixToMethodsWhenTheGetControllerEndsWithSlashAndTheMethodAreAnnotatedWithRelativePath() throws Exception {
		List<Route> routes = parser.rulesFor(new DefaultResourceClass(EndSlashAnnotatedGetController.class));
		Route route = getRouteMatching(routes, "/endSlash/relativePath");

		assertThat(route, canHandle(EndSlashAnnotatedGetController.class, "withRelativePath"));
    }


    @Test
    public void addsAPrefixToMethodsWhenTheGetControllerEndsWithSlashAndTheMethodAreAnnotatedWithAbsolutePath() throws Exception {
    	List<Route> routes = parser.rulesFor(new DefaultResourceClass(EndSlashAnnotatedGetController.class));
    	Route route = getRouteMatching(routes, "/endSlash/absolutePath");

    	assertThat(route, canHandle(EndSlashAnnotatedGetController.class, "withAbsolutePath"));
    }


    @Test
    public void addsAPrefixToMethodsWhenTheGetControllerAndTheMethodAreAnnotatedWithAbsolutePath() throws Exception {
    	List<Route> routes = parser.rulesFor(new DefaultResourceClass(GetAnnotatedController.class));
    	Route route = getRouteMatching(routes, "/prefix/absolutePath");

    	assertThat(route, canHandle(GetAnnotatedController.class, "withAbsolutePath"));
    }

    @Test
    public void addsAPrefixToMethodsWhenTheGetControllerIsAnnotatedWithPath() throws Exception {
    	List<Route> routes = parser.rulesFor(new DefaultResourceClass(GetAnnotatedController.class));
    	Route route = getRouteMatching(routes, "/prefix/withoutPath");

    	assertThat(route, canHandle(GetAnnotatedController.class, "withoutPath"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void throwsExceptionWhenTheGetControllerHasAmbiguousDeclaration() throws Exception {
    	parser.rulesFor(new DefaultResourceClass(WrongGetAnnotatedController.class));
    }

}