package br.com.caelum.vraptor.http.ognl;

import ognl.OgnlContext;

public class GenericObjectHandler implements CustomHandler {
    
    public Object handle(OgnlContext context, Object target, Object property) {
        return null;
    }

    public boolean canHandle(Object target, Object property) {
        return false;
    }

}
