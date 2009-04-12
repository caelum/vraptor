package br.com.caelum.vraptor.core;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultStaticContentHandler implements StaticContentHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(DefaultStaticContentHandler.class);
    
    private final ServletContext context;

    public DefaultStaticContentHandler(ServletContext context) {
        this.context = context;
    }

    public boolean requestingStaticFile(HttpServletRequest request) throws MalformedURLException {
        URL resourceUrl = context.getResource(uriRelativeToContextRoot(request));
        return resourceUrl != null && isAFile(resourceUrl);
    }

    private String uriRelativeToContextRoot(HttpServletRequest request) {
        return request.getRequestURI().substring(request.getContextPath().length());
    }

    private boolean isAFile(URL resourceUrl) {
        return !resourceUrl.toString().endsWith("/");
    }

    public void deferProcessingToContainer(FilterChain filterChain, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {
        if (logger.isDebugEnabled()) {
            logger.debug("deferring URI to container: " + request.getRequestURI());
        }
        filterChain.doFilter(request, response);
    }

}
