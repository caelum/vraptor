package br.com.caelum.vraptor.restfulie.relation;

public interface RelationBuilder {

	<T> T uses(Class<T> type);

	RelationBuilder at(String uri);

	Relation build();

}