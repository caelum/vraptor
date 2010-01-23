package br.com.caelum.vraptor.restfulie.relation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DefaultRelationBuilderTest {

	@Test
	public void shouldAllowRelationCreationToSpecificURIs() {
		RelationBuilder builder = new DefaultRelationBuilder("payment", null, null);
		builder.at("http://caelumobjects.com");
		Relation relation = builder.build();
		assertEquals("http://caelumobjects.com", relation.getUri());
		assertEquals("payment", relation.getName());
	}

	@Test(expected=IllegalStateException.class)
	public void shouldComplainIfTryingToBuildAnUncompleteRelation() {
		RelationBuilder builder = new DefaultRelationBuilder("payment", null, null);
		builder.build();
	}

}
