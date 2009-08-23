package br.com.caelum.vraptor.util.migration;

import java.util.List;

import br.com.caelum.vraptor.VRaptorException;

/**
 * Represents a database connection providing service.<br/>
 * Some of the methods in this type implies extracting data from the base and
 * releasing any resources used by the method. Those methods should <b>NOT</b>
 * depend on any LAZY behaviour.
 * 
 * @author guilherme silveira
 */
public interface ConnectionProvider {

	List<String> getAppliedMigrations();
	
	void apply(Migrations migrations) throws VRaptorException;

}
