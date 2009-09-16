package org.vraptor.mydvds.util;

import org.hibernate.SessionFactory;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.ComponentFactory;

@Component
@ApplicationScoped
public class SessionFactoryCreator
        extends br.com.caelum.vraptor.util.hibernate.SessionFactoryCreator
        implements ComponentFactory<SessionFactory> {
}
