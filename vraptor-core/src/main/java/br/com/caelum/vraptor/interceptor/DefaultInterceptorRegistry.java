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

package br.com.caelum.vraptor.interceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.util.collections.Filters;
import br.com.caelum.vraptor.util.collections.Functions;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/**
 * A registry filled with interceptors to intercept requests.<br/>
 * Interceptors are queried wether they want to intercept a request through
 * their accepts method.
 *
 * @author Guilherme Silveira
 * @author Fabio Kung
 */
@ApplicationScoped
public class DefaultInterceptorRegistry implements InterceptorRegistry {

    private final List<Class<? extends Interceptor>> interceptors = new ArrayList<Class<? extends Interceptor>>();

    public Interceptor[] interceptorsFor(ResourceMethod method, Container container) {
		List<Interceptor> list = Lists.transform(interceptors, Functions.<Interceptor>instanceWith(container));
		Collection<Interceptor> filtered = Collections2.filter(list, Filters.accepts(method));
		return filtered.toArray(new Interceptor[0]);
    }

    public void register(Class<? extends Interceptor>... interceptors) {
        this.interceptors.addAll(Arrays.asList(interceptors));
    }

    public List<Class<? extends Interceptor>> all() {
        return interceptors;
    }

}
