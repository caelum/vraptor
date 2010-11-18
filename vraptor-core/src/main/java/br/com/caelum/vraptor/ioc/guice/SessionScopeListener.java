/**
 * 
 */
package br.com.caelum.vraptor.ioc.guice;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionScopeListener implements HttpSessionListener {
	private static final Logger logger = LoggerFactory.getLogger(SessionScopeListener.class);
	public void sessionCreated(HttpSessionEvent event) {
		logger.debug("starting session {}", event.getSession().getId());
		GuiceProvider.SESSION.start(event.getSession());
	}

	public void sessionDestroyed(HttpSessionEvent event) {
		logger.debug("stopping session {}", event.getSession().getId());
		GuiceProvider.SESSION.stop(event.getSession());
	}
}