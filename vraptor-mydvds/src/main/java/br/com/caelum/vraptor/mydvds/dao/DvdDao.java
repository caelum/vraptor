package br.com.caelum.vraptor.mydvds.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import br.com.caelum.vraptor.mydvds.model.Dvd;

/**
 * Data Access Object for the Dvd entity.
 */
public class DvdDao {

	// current hibernate session
	private final Session session;

	/**
	 * Creates a new DvdDao.
	 *
	 * @param session hibernate session.
	 */
	DvdDao(Session session) {
		this.session = session;
	}

	/**
	 * Add a new dvd to the database.
	 *
	 * @param dvd
	 */
	public void add(Dvd dvd) {
		session.save(dvd);
	}

	/**
	 * Returns a list of DVDs containing the specified title.
	 *
	 * @param title title to search for.
	 * @return DVD list.
	 */
	@SuppressWarnings("unchecked")
	public List<Dvd> searchSimilarTitle(String title) {
		// creates a criteria based on the Dvd class and adds
		// the "title" restriction and then returns the list.
		return session.createCriteria(Dvd.class).add(
				Restrictions.ilike("title", "%" + title + "%")).list();
	}

}
