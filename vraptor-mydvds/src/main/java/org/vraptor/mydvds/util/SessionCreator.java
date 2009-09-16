package org.vraptor.mydvds.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.RequestScoped;

@Component
@RequestScoped
public class SessionCreator
        extends br.com.caelum.vraptor.util.hibernate.SessionCreator
        implements ComponentFactory<Session>{

    public SessionCreator(SessionFactory factory) {
        super(factory);
    }

}
