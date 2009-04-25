/***
 * 
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of the
 * copyright holders nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.validator;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

public class Validations {

    private final List<String> errors = new ArrayList<String>();
    private Object lastUsed;

    public <T> void that(T id, Matcher<T> matcher) {
        that(null, id, matcher);
    }

    public <T> void that(String reason, T actual, Matcher<? super T> matcher) {
        if (!matcher.matches(actual)) {
            if (reason != null) {
                errors.add(reason);
            } else {
                Description description = new StringDescription();
                description.appendDescriptionOf(matcher);
                errors.add(description.toString());
            }
        }
    }

    public void that(String reason, boolean assertion) {
        if (!assertion) {
            errors.add(reason);
        }
    }

    public List<String> getErrors() {
        return errors;
    }

    public void and(List<String> errors) {
        this.errors.addAll(errors);
    }

    class MyMethodInterceptor implements MethodInterceptor {

        private final Object instance;

        public MyMethodInterceptor(Object instance) {
            this.instance = instance;
        }

        public Object intercept(Object instance, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            if (Modifier.isFinal(method.getReturnType().getModifiers())) {
                // cant invoke any further methods
                Object result = proxy.invoke(this.instance, args);
                used(result);
                return result;
            }
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(method.getReturnType());
            enhancer.setCallback(this);
            return enhancer.create();
        }

    };

    @SuppressWarnings("unchecked")
    public <T> T that(T instance) {
        if (instance == null) {
            // cant mock it... already null
            used(null);
            return null;
        }
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(instance.getClass());
        enhancer.setCallback(new MyMethodInterceptor(instance));
        used(instance);
        return (T) enhancer.create();
    }

    public void used(Object object) {
        this.lastUsed = object;
    }

    public <T> void shouldBe(Matcher<T> matcher) {
        if (!matcher.matches(lastUsed)) {
            Description description = new StringDescription();
            description.appendDescriptionOf(matcher);
            errors.add(description.toString());
        }
    }

}
