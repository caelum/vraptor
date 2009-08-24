package br.com.caelum.vraptor.util.migration;

import java.util.Comparator;

/**
 * Comparator used to sort migrations by their id.
 *
 * @author guilherme silveira
 */
@SuppressWarnings("unchecked")
public class MigrationComparator implements Comparator<Migration> {

	public int compare(Migration o1, Migration o2) {
		return o1.getId().compareTo(o2.getId());
	}

}
