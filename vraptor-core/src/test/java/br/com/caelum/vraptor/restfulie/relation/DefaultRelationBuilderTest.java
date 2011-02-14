package br.com.caelum.vraptor.restfulie.relation;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.proxy.CglibProxifier;
import br.com.caelum.vraptor.proxy.ObjenesisInstanceCreator;

public class DefaultRelationBuilderTest {

	private @Mock Router router;
	private RelationBuilder builder;
	private Method doIt;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		builder = new DefaultRelationBuilder(router, new CglibProxifier(new ObjenesisInstanceCreator()));
		doIt = Controller.class.getMethod("doIt");
	}
	@Test
	public void shouldAllowRelationCreationToSpecificURIs() {
		builder.relation("payment").at("http://caelumobjects.com");
		assertThat(builder, hasRelation("payment", "http://caelumobjects.com"));
	}

	@Test
	public void shouldBuildRelationsOfControllersUsingMethodNameByDefault() throws Exception {
		when(router.urlFor(Controller.class, doIt)).thenReturn("http://caelumobjects.com");

		builder.relation(Controller.class).doIt();

		assertThat(builder, hasRelation("doIt", "http://caelumobjects.com"));
	}
	@Test
	public void shouldBuildRelationsOfControllersUsingGivenName() throws Exception {
		when(router.urlFor(Controller.class, doIt)).thenReturn("http://caelumobjects.com");

		builder.relation("payment").uses(Controller.class).doIt();

		assertThat(builder, hasRelation("payment", "http://caelumobjects.com"));
	}

	static class Controller {
		public void doIt() {

		}
	}

	private Matcher<RelationBuilder> hasRelation(final String name, final String uri) {
		return new TypeSafeMatcher<RelationBuilder>() {
			@Override
			protected void describeMismatchSafely(RelationBuilder item, Description mismatchDescription) {
			}

			@Override
			protected boolean matchesSafely(RelationBuilder item) {
				if (item.getRelations().isEmpty()) {
					return false;
				}
				Relation relation = item.getRelations().get(0);
				return name.equals(relation.getName()) && uri.equals(relation.getUri());
			}

			public void describeTo(Description description) {
				description.appendText("a builder with the relation ")
					.appendText(name).appendText(" -> ").appendText(uri);
			}
		};
	}
}
