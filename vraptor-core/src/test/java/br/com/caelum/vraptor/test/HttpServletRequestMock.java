package br.com.caelum.vraptor.test;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * @author Fabio Kung
 */
public class HttpServletRequestMock implements HttpServletRequest {
    private Map<String, Object> attributes = new HashMap<String, Object>();
    private Map<String, String> parameters = new HashMap<String, String>();

    private String authType;
    private Cookie[] cookies;
    private String method;
    private String pathInfo;
    private String pathTranslated;
    private String contextPath;
    private String queryString;
    private String remoteUser;
    private Principal userPrincipal;
    private String requestedSessionId;
    private String requestURI;
    private StringBuffer requestURL;
    private String servletPath;
    private boolean requestedSessionIdValid = true;
    private boolean requestedSessionIdFromCookie = true;
    private boolean requestedSessionIdFromURL;
    private String characterEncoding;
    private int contentLength;
    private String contentType;
    private ServletInputStream inputStream;
    private String protocol;
    private String scheme;
    private String serverName;
    private int serverPort;
    private BufferedReader reader;
    private String remoteAddr;
    private String remoteHost;
    private Locale locale;
    private boolean secure;
    private RequestDispatcher requestDispatcher;
    private String realPath;
    private int remotePort;
    private String localName;
    private String localAddr;
    private int localPort;

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public Cookie[] getCookies() {
        return cookies;
    }

    public void setCookies(Cookie[] cookies) {
        this.cookies = cookies;
    }

    public long getDateHeader(String s) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getHeader(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Enumeration getHeaders(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Enumeration getHeaderNames() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getIntHeader(String s) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getMethod() {
        return method;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPathInfo() {
        return pathInfo;
    }

    public void setPathInfo(String pathInfo) {
        this.pathInfo = pathInfo;
    }

    public String getPathTranslated() {
        return pathTranslated;
    }

    public void setPathTranslated(String pathTranslated) {
        this.pathTranslated = pathTranslated;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getRemoteUser() {
        return remoteUser;
    }

    public void setRemoteUser(String remoteUser) {
        this.remoteUser = remoteUser;
    }

    public boolean isUserInRole(String s) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Principal getUserPrincipal() {
        return userPrincipal;
    }

    public void setUserPrincipal(Principal userPrincipal) {
        this.userPrincipal = userPrincipal;
    }

    public String getRequestedSessionId() {
        return requestedSessionId;
    }

    public void setRequestedSessionId(String requestedSessionId) {
        this.requestedSessionId = requestedSessionId;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public StringBuffer getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(StringBuffer requestURL) {
        this.requestURL = requestURL;
    }

    public String getServletPath() {
        return servletPath;
    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    public HttpSession getSession(boolean b) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public HttpSession getSession() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isRequestedSessionIdValid() {
        return requestedSessionIdValid;
    }

    public void setRequestedSessionIdValid(boolean requestedSessionIdValid) {
        this.requestedSessionIdValid = requestedSessionIdValid;
    }

    public boolean isRequestedSessionIdFromCookie() {
        return requestedSessionIdFromCookie;
    }

    public void setRequestedSessionIdFromCookie(boolean requestedSessionIdFromCookie) {
        this.requestedSessionIdFromCookie = requestedSessionIdFromCookie;
    }

    public boolean isRequestedSessionIdFromURL() {
        return requestedSessionIdFromURL;
    }

    public void setRequestedSessionIdFromURL(boolean requestedSessionIdFromURL) {
        this.requestedSessionIdFromURL = requestedSessionIdFromURL;
    }

    public boolean isRequestedSessionIdFromUrl() {
        return requestedSessionIdFromURL;
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public Enumeration getAttributeNames() {
        return new IteratorToEnumerationAdapter(attributes.keySet().iterator());
    }

    public String getCharacterEncoding() {
        return characterEncoding;
    }

    public void setCharacterEncoding(String s) throws UnsupportedEncodingException {
        this.characterEncoding = s;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public ServletInputStream getInputStream() throws IOException {
        return inputStream;
    }

    public void setInputStream(ServletInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public void setParameter(String key, String value) {
        parameters.put(key, value);
    }

    public Enumeration getParameterNames() {
        return new IteratorToEnumerationAdapter(parameters.keySet().iterator());
    }

    public String[] getParameterValues(String keys) {
        return new String[]{parameters.get(keys)};
    }

    public Map getParameterMap() {
        return parameters;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public BufferedReader getReader() throws IOException {
        return reader;
    }

    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public void removeAttribute(String key) {
        attributes.remove(key);
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Enumeration getLocales() {
        Iterator<Locale> localeIterator = Arrays.asList(Locale.getAvailableLocales()).iterator();
        return new IteratorToEnumerationAdapter(localeIterator);
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public RequestDispatcher getRequestDispatcher(String s) {
        return requestDispatcher;
    }

    public void setRequestDispatcher(RequestDispatcher requestDispatcher) {
        this.requestDispatcher = requestDispatcher;
    }

    public String getRealPath(String path) {
        return realPath + path;
    }

    public void setRealPath(String realPath) {
        this.realPath = realPath;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getLocalAddr() {
        return localAddr;
    }

    public void setLocalAddr(String localAddr) {
        this.localAddr = localAddr;
    }

    public int getLocalPort() {
        return localPort;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }
}
