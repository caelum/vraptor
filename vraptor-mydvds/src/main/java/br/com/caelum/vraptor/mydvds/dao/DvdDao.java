package br.com.caelum.vraptor.mydvds.dao;

import java.util.List;

import br.com.caelum.vraptor.mydvds.model.Dvd;

/**
 * Data Access Object for the Dvd entity.
 *
 * @author Lucas Cavalcanti
 */
public interface DvdDao {

	/**
	 * Add a new dvd to the database.
	 *
	 * @param dvd
	 */
	void add(Dvd dvd);

	/**
	 * Returns a list of DVDs containing the specified title.
	 *
	 * @param title title to search for.
	 * @return DVD list.
	 */
	List<Dvd> searchSimilarTitle(String title);

}