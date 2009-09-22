package br.com.caelum.vraptor.mydvds.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.mydvds.model.Dvd;

/**
 * Default implementation for DvdDao.
 *
 * Annotating this class with @Component we have the dependency injection
 * support either on this class and on other classes that depend on
 * DvdDao or DefaultDvdDao
 *
 * @author Lucas Cavalcanti
 */
@Component
public class DefaultDvdDao implements DvdDao {

	// current hibernate session
	private final Session session;

	/**
	 * Creates a new DvdDao.
	 *
	 * @param session hibernate session.
	 */
	public DefaultDvdDao(Session session) {
		this.session = session;
	}

	/* (non-Javadoc)
	 * @see br.com.caelum.vraptor.mydvds.dao.DvdDao#add(br.com.caelum.vraptor.mydvds.model.Dvd)
	 */
	public void add(Dvd dvd) {
		session.save(dvd);
	}

	/* (non-Javadoc)
	 * @see br.com.caelum.vraptor.mydvds.dao.DvdDao#searchSimilarTitle(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<Dvd> searchSimilarTitle(String title) {
		// creates a criteria based on the Dvd class and adds
		// the "title" restriction and then returns the list.
		return session.createCriteria(Dvd.class).add(
				Restrictions.ilike("title", "%" + title + "%")).list();
	}

}
