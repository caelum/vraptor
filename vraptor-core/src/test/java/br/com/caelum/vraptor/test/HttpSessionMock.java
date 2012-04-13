/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.caelum.vraptor.test;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

/**
 * @author Fabio Kung
 */
@SuppressWarnings({"deprecation", "unchecked"})
public class HttpSessionMock implements HttpSession {
    private final ServletContext context;
    private final String id;

    private long creationTime;
    private long lastAccessedTime;
    private int maxInactiveInterval;
    private final Map<String, Object> attributes = new HashMap<String, Object>();
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
        return Collections.enumeration(attributes.keySet());
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

    @Deprecated
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
