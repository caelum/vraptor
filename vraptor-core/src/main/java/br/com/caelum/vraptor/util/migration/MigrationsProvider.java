package br.com.caelum.vraptor.util.migration;

/**
 * Interface which define how migrations are looked up on the system.
 * 
 * @author guilherme silveira
 */
public interface MigrationsProvider {

	/**
	 * Returns a collection containing all known migrations.
	 */
	Migrations all();

}
