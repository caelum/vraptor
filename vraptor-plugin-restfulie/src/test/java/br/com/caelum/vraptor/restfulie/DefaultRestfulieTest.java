package br.com.caelum.vraptor.restfulie;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import br.com.caelum.vraptor.restfulie.relation.Relation;
import br.com.caelum.vraptor.restfulie.relation.DefaultRelationBuilder;

public class DefaultRestfulieTest {
	
	@Test
	public void shouldCreateAndRegisterABuilderWhenInvokingRelationOrTransition() {
		final DefaultRelationBuilder builder = mock(DefaultRelationBuilder.class);
		
		DefaultRestfulie restfulie = new DefaultRestfulie(null, null) {
			protected DefaultRelationBuilder createBuilderFor(String name) {
				return builder;
			}
		};
		
		Relation result = mock(Relation.class);
		when(builder.build()).thenReturn(result);
		restfulie.relation("payment");
		assertEquals(Arrays.asList(result), restfulie.getRelations());
	}

	@Test
	public void shouldAllowObjectCleanUp() {
		final DefaultRelationBuilder builder = mock(DefaultRelationBuilder.class);
		
		DefaultRestfulie restfulie = new DefaultRestfulie(null, null) {
			protected DefaultRelationBuilder createBuilderFor(String name) {
				return builder;
			}
		};
		
		Relation result = mock(Relation.class);
		when(builder.build()).thenReturn(result);
		restfulie.relation("payment");
		restfulie.clear();
		assertEquals(new ArrayList<Relation>(), restfulie.getRelations());
	}

}
