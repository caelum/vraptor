package br.com.caelum.vraptor.ioc;

import br.com.caelum.vraptor.core.RequestInfo;

/**
 * @author Fabio Kung
 */
public interface WhatToDo<T> {
    T execute(RequestInfo request, int counter);
}
