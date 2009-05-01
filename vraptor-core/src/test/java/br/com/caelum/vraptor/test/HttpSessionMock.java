package br.com.caelum.vraptor.test;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

/**
 * @author Fabio Kung
 */
public class HttpSessionMock implements HttpSession {
    private final ServletContext context;
    private final String id;

    private long creationTime;
    private long lastAccessedTime;
    private int maxInactiveInterval;
    private Map<String, Object> attributes = new HashMap<String, Object>();
    private boolean isNew;

    public HttpSessionMock(ServletContext context, String id) {
        this.context = context;
        this.id = id;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public String getId() {
        return id;
    }

    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    public void setLastAccessedTime(long lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }

    public ServletContext getServletContext() {
        return context;
    }

    public void setMaxInactiveInterval(int interval) {
        this.maxInactiveInterval = interval;
    }

    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    public HttpSessionContext getSessionContext() {
        return new HttpSessionContext() {
            public HttpSession getSession(String s) {
                return HttpSessionMock.this;
            }

            public Enumeration getIds() {
                return new Enumeration() {
                    private boolean hasNext = true;

                    public boolean hasMoreElements() {
                        return hasNext;
                    }

                    public Object nextElement() {
                        hasNext = false;
                        return getId();
                    }
                };
            }
        };
    }

    public Object getAttribute(String name) {
        return attributes.get(name);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object getValue(String name) {
        return getAttribute(name);
    }

    public Enumeration getAttributeNames() {
        Iterator<String> names = attributes.keySet().iterator();
        return new IteratorToEnumerationAdapter(names);
    }

    public String[] getValueNames() {
        Set<String> names = attributes.keySet();
        return names.toArray(new String[names.size()]);
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    public void removeValue(String name) {
        removeAttribute(name);
    }

    public void invalidate() {
        attributes.clear();
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }
}
