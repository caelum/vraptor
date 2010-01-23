package br.com.caelum.vraptor.restfulie.relation;

/**
 * Responsible for allowing clients to create relations
 * @author guilherme silveira
 * @author pedro matiello
 */
public interface RelationBuilder {

	<T> T uses(Class<T> type);

	RelationBuilder at(String uri);

	Relation build();

}