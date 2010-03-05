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

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.proxy.ObjenesisProxifier;
import br.com.caelum.vraptor.view.LogicResult;
import br.com.caelum.vraptor.view.PageResult;
import br.com.caelum.vraptor.view.Results;
import br.com.caelum.vraptor.view.ValidationViewsFactory;

public class ValidatorAcceptanceTest {
    private PageResult pageResult;
    private Mockery mockery;
	private LogicResult logicResult;
    private Result result;

    class Student {
        private Long id;
    }

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.result = mockery.mock(Result.class);
        this.pageResult = mockery.mock(PageResult.class);
        this.logicResult = mockery.mock(LogicResult.class);
        mockery.checking(new Expectations() {
            {
                allowing(result).use(Results.page()); will(returnValue(pageResult));
                allowing(result).use(Results.logic()); will(returnValue(logicResult));
            }
        });
    }

    @Test
    public void validDataDoesntThrowException() {
        DefaultValidator validator = new DefaultValidator(result, mockery.mock(ValidationViewsFactory.class), mockery.mock(Outjector.class), new ObjenesisProxifier());
        final Student guilherme = new Student();
        guilherme.id = 15L;
        validator.checking(new Validations() {
            {
                // this is the Assertion itself
                that(guilherme.id, is(notNullValue()), "id");
            }
        });
        assertFalse(validator.hasErrors());
        mockery.assertIsSatisfied();
    }

}
