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

import static org.hamcrest.Matchers.notNullValue;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.view.LogicResult;
import br.com.caelum.vraptor.view.PageResult;
import br.com.caelum.vraptor.view.Results;

public class ValidatorAcceptanceTest {
    private PageResult pageResult;
    private Mockery mockery;
	private LogicResult logicResult;
    private Proxifier proxifier;
    private Result result;
	private MutableRequest request;

    class Student {
        private Long id;
    }

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.result = mockery.mock(Result.class);
        this.pageResult = mockery.mock(PageResult.class);
        this.logicResult = mockery.mock(LogicResult.class);
        this.proxifier = mockery.mock(Proxifier.class);
        this.request = mockery.mock(MutableRequest.class);
        mockery.checking(new Expectations() {
            {
                allowing(result).use(Results.page()); will(returnValue(pageResult));
                allowing(result).use(Results.logic()); will(returnValue(logicResult));
            }
        });
    }

    @Test
    public void validDataDoesntThrowException() {
        DefaultValidator validator = new DefaultValidator(proxifier, result, request);
        final Student guilherme = new Student();
        guilherme.id = 15L;
        validator.checking(new Validations() {
            {
                // this is the Assertion itself
                that("id", guilherme.id, is(notNullValue()));
            }
        });
        mockery.assertIsSatisfied();
    }

}
