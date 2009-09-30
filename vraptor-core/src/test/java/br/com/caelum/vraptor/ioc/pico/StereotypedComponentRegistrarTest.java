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

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.Stereotype;
import br.com.caelum.vraptor.ioc.StereotypeHandler;

/**
 * @author Fabio Kung
 */
public class StereotypedComponentRegistrarTest {

    private StereotypedComponentRegistrar registrar;
    private Mockery mockery;
    private ComponentRegistry registry;
    private Scanner scanner;

    @SuppressWarnings("unchecked")
	@Before
    public void setup() {
        mockery = new Mockery();
        registry = mockery.mock(ComponentRegistry.class);
        scanner = mockery.mock(Scanner.class);
        Arrays.asList(Component.class, Intercepts.class, Convert.class, Resource.class);

        this.registrar = new StereotypedComponentRegistrar(registry, Collections.<StereotypeHandler>singletonList(new ComponentHandler()));
    }

    @SuppressWarnings("unchecked")
	@Test
    public void shouldRegisterComponentsAnnotatedWithAnyStereotypedAnnotations() {
        mockery.checking(new Expectations() {
            {
                one(scanner).getTypesWithMetaAnnotation(Stereotype.class);
                will(returnValue(Arrays.asList(ComponentAnnotated.class, ResourceAnnotated.class)));
                one(registry).deepRegister(ComponentAnnotated.class);
                one(registry).deepRegister(ResourceAnnotated.class);

                allowing(scanner).getTypesWithAnnotation(Component.class);
                allowing(scanner).getTypesWithAnnotation(Resource.class);
                allowing(scanner).getTypesWithAnnotation(Intercepts.class);
                allowing(scanner).getTypesWithAnnotation(Convert.class);
            }
        });
        registrar.registerFrom(scanner);
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldRegisterComponentsAnnotatedWithComponent() {
        mockery.checking(new Expectations() {
            {
                one(scanner).getTypesWithAnnotation(Component.class);
                will(returnValue(Collections.singletonList(ComponentAnnotated.class)));
                one(registry).deepRegister(ComponentAnnotated.class);

                allowing(scanner).getTypesWithAnnotation(Resource.class);
                allowing(scanner).getTypesWithAnnotation(Intercepts.class);
                allowing(scanner).getTypesWithAnnotation(Convert.class);
                allowing(scanner).getTypesWithMetaAnnotation(Stereotype.class);
            }
        });
        registrar.registerFrom(scanner);
        mockery.assertIsSatisfied();
    }

    @Resource
    class ResourceAnnotated {
    }

    @Component
    class ComponentAnnotated {
    }

    @Convert(String.class)
    class ConvertAnnotated {
    }

    @Intercepts
    class InterceptsAnnotated {
    }
}
