/***
 *
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
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
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.http;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.List;

import ognl.NoSuchPropertyException;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import br.com.caelum.vraptor.converter.OgnlToConvertersController;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.http.ognl.ListAccessor;
import br.com.caelum.vraptor.http.ognl.ReflectionBasedNullHandler;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class OgnlParametersProvider implements ParametersProvider {

    private final TypeCreator creator;

    private final Container container;

    private final Converters converters;

    private final ParameterNameProvider provider;

    private final RequestParameters parameters; 

    public OgnlParametersProvider(TypeCreator creator, Container container, Converters converters, ParameterNameProvider provider, RequestParameters parameters) {
        this.creator = creator;
        this.container = container;
        this.converters = converters;
        this.provider = provider;
        this.parameters = parameters;
        OgnlRuntime.setNullHandler(Object.class, new ReflectionBasedNullHandler());
        OgnlRuntime.setPropertyAccessor(List.class, new ListAccessor());
    }

    public Object[] getParametersFor(ResourceMethod method) {
        try {
            Class<?> type = creator.typeFor(method);
            Object root = type.getDeclaredConstructor().newInstance();
            OgnlContext context = (OgnlContext) Ognl.createDefaultContext(root);
            context.setTraceEvaluations(true);
            context.put(Container.class,this.container);
            
            OgnlToConvertersController controller = new OgnlToConvertersController(converters);
            Ognl.setTypeConverter(context, controller);
            for(String key : parameters.getNames()) {
                String[] values = parameters.get(key);
                try {
                    Ognl.setValue(key, context,root, values.length==1 ? values[0] : values);
                } catch (NoSuchPropertyException ex) {
                    // TODO optimization: be able to ignore or not
                    // ignoring
                }
            }
            Type[] types = method.getMethod().getGenericParameterTypes();
            Object[] result = new Object[types.length];
            String[] names = provider.parameterNamesFor(method.getMethod());
            for (int i = 0; i < types.length; i++) {
                result[i] = root.getClass().getMethod("get" + names[i]).invoke(root);
            }
            return result;
        } catch (InstantiationException e) {
            // TODO validation exception?
            throw new IllegalArgumentException("unable to compile expression",e);
        } catch (IllegalAccessException e) {
            // TODO validation exception?
            throw new IllegalArgumentException("unable to compile expression",e);
        } catch (IllegalArgumentException e) {
            // TODO validation exception?
            throw new IllegalArgumentException("unable to compile expression",e);
        } catch (SecurityException e) {
            // TODO validation exception?
            throw new IllegalArgumentException("unable to compile expression",e);
        } catch (InvocationTargetException e) {
            // TODO validation exception?
            throw new IllegalArgumentException("unable to compile expression",e);
        } catch (NoSuchMethodException e) {
            // TODO validation exception?
            throw new IllegalArgumentException("unable to compile expression",e);
        } catch (OgnlException e) {
            // TODO validation exception?
            throw new IllegalArgumentException("unable to compile expression",e);
        }
    }
}
