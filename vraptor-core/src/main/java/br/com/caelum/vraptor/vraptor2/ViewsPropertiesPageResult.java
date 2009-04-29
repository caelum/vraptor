package br.com.caelum.vraptor.vraptor2;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.resource.Resource;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.PathResolver;
import br.com.caelum.vraptor.view.ResultException;
import br.com.caelum.vraptor.view.jsp.PageResult;

public class ViewsPropertiesPageResult implements PageResult {

	private final Config config;
	private final HttpServletRequest request;
	private final PathResolver resolver;
	private final ResourceMethod method;
	private final HttpServletResponse response;
	private final ExpressionEvaluator evaluator = new ExpressionEvaluator();

	private static final Logger logger = LoggerFactory.getLogger(ViewsPropertiesPageResult.class);
	private final VRaptorRequest webRequest;

	public ViewsPropertiesPageResult(Config config, PathResolver resolver, RequestInfo requestInfo,
			VRaptorRequest webRequest) {
		this.config = config;
		this.webRequest = webRequest;
		this.request = webRequest.getRequest();
		this.resolver = resolver;
		this.method = requestInfo.getResourceMethod();
		this.response = webRequest.getResponse();
	}

	public void forward(String result) {
		try {
			Resource resource = method.getResource();
			if (!Info.isOldComponent(resource)) {
				String forwardPath = resolver.pathFor(method, result);
				request.getRequestDispatcher(forwardPath).forward(request, response);
				return;
			}
			String key = Info.getComponentName(resource.getType()) + "." + Info.getLogicName(method.getMethod()) + "."
					+ result;

			String path = config.getForwardFor(key);

			if (path == null) {
				String forwardPath = resolver.pathFor(method, result);
				request.getRequestDispatcher(forwardPath).forward(request, response);
			} else {
				try {
					result = evaluator.parseExpression(path, webRequest);
				} catch (ExpressionEvaluationException e) {
					throw new ServletException("Unable to redirect while evaluating expression '" + path + "'.", e);
				}
				if (logger.isDebugEnabled()) {
					logger.debug("overriden view found for " + key + " : " + path + " expressed as " + result);
				}
				if (result.startsWith("redirect:")) {
					response.sendRedirect(result.substring(9));
				} else {
					request.getRequestDispatcher(result).forward(request, response);
				}
			}
		} catch (ServletException e) {
			throw new ResultException(e);
		} catch (IOException e) {
			throw new ResultException(e);
		}
	}

	public void include(String result) {
		try {
			request.getRequestDispatcher(resolver.pathFor(method, result)).include(request, response);
		} catch (ServletException e) {
			throw new ResultException(e);
		} catch (IOException e) {
			throw new ResultException(e);
		}
	}

	public void include(String key, Object value) {
		request.setAttribute(key, value);
	}

	public void redirect(String url) {
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			throw new ResultException(e);
		}
	}

}
