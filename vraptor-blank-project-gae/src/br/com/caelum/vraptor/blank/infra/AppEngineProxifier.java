package br.com.caelum.vraptor.blank.infra;

import ognl.OgnlRuntime;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.proxy.DefaultProxifier;

@Component
@ApplicationScoped
public class AppEngineProxifier extends DefaultProxifier {

	static {
		// AppEngine OGNL workaround
		OgnlRuntime.setSecurityManager(null);
	}
	
}
