package br.com.caelum.vraptor.util.migration;

/**
 * Executes a migration based on a database api.<br/>
 * Migrations should be annotated 
 * @author guilherme silveira
 *
 * @param <T> the database api access object
 */
public interface Migration {
	
	<T> void execute(T control);
	
	String getId();

}
