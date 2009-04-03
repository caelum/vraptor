package br.com.caelum.vraptor.http.ognl;

import ognl.OgnlContext;

public interface CustomHandler<T, P, R> {

    R handle(OgnlContext context, T target, P property);
    
    boolean canHandle(T target, P property);

}
