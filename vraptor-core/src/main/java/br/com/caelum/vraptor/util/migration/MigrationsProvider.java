package br.com.caelum.vraptor.util.migration;

import java.util.Collection;

/**
 * Interface which define how migrations are looked up on the system.
 * 
 * @author guilherme silveira
 */
public interface MigrationsProvider {

	/**
	 * Returns a collection containing all known migrations.
	 */
	Collection<? extends Migration> all();

}
