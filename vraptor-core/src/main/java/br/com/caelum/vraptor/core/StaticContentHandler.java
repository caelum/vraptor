package br.com.caelum.vraptor.core;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface StaticContentHandler {

    public boolean requestingStaticFile(HttpServletRequest request) throws MalformedURLException;
    public void deferProcessingToContainer(FilterChain filterChain, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException;


}
