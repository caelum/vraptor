package br.com.caelum.vraptor.core;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.config.BasicConfiguration;
import br.com.caelum.vraptor.http.EncodingHandler;
import br.com.caelum.vraptor.http.VRaptorRequest;
import br.com.caelum.vraptor.http.VRaptorResponse;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.ContainerProvider;

/**
 * A Vraptor application context independent of a filter or servlet.
 * 
 * @author guilherme silveira
 * @author fabio kung
 * @author jose donizetti
 */
public class DefaultApplication implements Application {

	private ContainerProvider provider;
	private final ServletContext context;

	private StaticContentHandler staticContent;

	private static final Logger logger = LoggerFactory
			.getLogger(DefaultApplication.class);

	public DefaultApplication(ServletContext servletContext) {
		this.context = servletContext;
	}

	public void stop() {
		logger.debug("Stopping VRaptor");
		provider.stop();
		provider = null;
		logger.info("VRaptor stopped");
	}

	public void parse(final HttpServletRequest baseRequest,
			final HttpServletResponse baseResponse, FilterChain chain)
			throws IOException, ServletException {

		if (staticContent.requestingStaticFile(baseRequest)) {
			staticContent.deferProcessingToContainer(chain, baseRequest,
					baseResponse);
		} else {
			logger.debug("VRaptor received a new request");
			logger.trace("Request: {}", baseRequest);

			VRaptorRequest mutableRequest = new VRaptorRequest(baseRequest);
			VRaptorResponse mutableResponse = new VRaptorResponse(baseResponse);

			final RequestInfo request = new RequestInfo(context, chain,
					mutableRequest, mutableResponse);
			provider.provideForRequest(request, new Execution<Object>() {
				public Object insideRequest(Container container) {
					container.instanceFor(EncodingHandler.class).setEncoding(
							baseRequest, baseResponse);
					container.instanceFor(RequestExecution.class).execute();
					return null;
				}
			});
			logger.debug("VRaptor ended the request");
		}
	}

	public void restart() {
		stop();
		start();
	}

	public void start() {
		logger.info("Starting VRaptor 3.3.2-SNAPSHOT");
		BasicConfiguration config = new BasicConfiguration(context);
		init(config.getProvider(), new DefaultStaticContentHandler(context));
		logger.info("VRaptor successfuly initialized");
	}

	protected void init(ContainerProvider provider, StaticContentHandler handler) {
		this.provider = provider;
		this.staticContent = handler;
	}

}
