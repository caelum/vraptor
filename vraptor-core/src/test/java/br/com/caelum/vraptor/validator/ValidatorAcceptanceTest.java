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

package br.com.caelum.vraptor.validator;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.proxy.CglibProxifier;
import br.com.caelum.vraptor.proxy.ObjenesisInstanceCreator;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.util.EmptyBundle;
import br.com.caelum.vraptor.view.ValidationViewsFactory;

@RunWith(MockitoJUnitRunner.class)
public class ValidatorAcceptanceTest {
    private @Mock Result result;
	private @Mock Localization localization;
	private @Mock ValidationViewsFactory viewsFactory;
	private @Mock Outjector outjector;
	private DefaultValidator validator;

    class Student {
        private Long id;
    }

    @Before
    public void setup() {
        Proxifier proxifier = new CglibProxifier(new ObjenesisInstanceCreator());
        validator = new DefaultValidator(result, viewsFactory, outjector, proxifier, null, localization);
        when(localization.getBundle()).thenReturn(new EmptyBundle());
    }

    @Test
    public void validDataDoesntThrowException() {
        //TODO testing
        final Student guilherme = new Student();
        guilherme.id = 15L;
        validator.checking(new Validations() {
            {
                // this is the Assertion itself
                that(guilherme.id, is(notNullValue()), "id");
            }
        });
        assertFalse(validator.hasErrors());
    }

}
