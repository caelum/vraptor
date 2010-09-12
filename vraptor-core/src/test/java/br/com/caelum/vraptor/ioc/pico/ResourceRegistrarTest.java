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
package br.com.caelum.vraptor.ioc.pico;

import java.util.Arrays;
import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.http.route.RoutesParser;
import br.com.caelum.vraptor.resource.DefaultResourceClass;

/**
 * @author Fabio Kung
 */
public class ResourceRegistrarTest {

    private Mockery mockery;
    private ResourceRegistrar registrar;
    private Router registry;
    private Scanner scanner;
	private RoutesParser parser;

    @Before
    public void setup() {
        mockery = new Mockery();
        registry = mockery.mock(Router.class);
        scanner = mockery.mock(Scanner.class);
        parser = mockery.mock(RoutesParser.class);
        this.registrar = new ResourceRegistrar(registry, parser);
    }

    @SuppressWarnings("unchecked")
	@Test
    public void shouldRegisterResourcesAnnotatedWithResource() {
        mockery.checking(new Expectations() {
            {
                one(scanner).getTypesWithAnnotation(Resource.class);
                will(returnValue(Arrays.asList(ResourceAnnotated.class)));

                one(parser).rulesFor(new DefaultResourceClass(ResourceAnnotated.class));
                will(returnValue(Collections.emptyList()));
            }
        });
        registrar.registerFrom(scanner);
        mockery.assertIsSatisfied();
    }

    @Resource
    class ResourceAnnotated {
    }
}
