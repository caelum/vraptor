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

import java.io.IOException;

import javax.servlet.ServletException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.view.LogicResult;
import br.com.caelum.vraptor.view.PageResult;

public class ValidatorAcceptanceTest {
    private PageResult result;
    private Mockery mockery;
	private LogicResult logic;

    @Before
    public void setupMocks() {
        mockery = new Mockery();
        result = mockery.mock(PageResult.class);
    }

    class Student {
        private Long id;
    }

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.result = mockery.mock(PageResult.class, "result");
        this.logic = mockery.mock(LogicResult.class);
    }

    @Test
    public void cheksThatValidationWorks() throws ServletException, IOException {
        DefaultValidator validator = new DefaultValidator(proxifier, result,logic);
        final Student guilherme = new Student();
        mockery.checking(new Expectations() {
            {
                one(result).include((String) with(an(String.class)), with(an(Object.class)));
                one(result).forward("invalid");
            }
        });
        try {
            validator.checking(new Validations() {
                {
                    that("id",guilherme.id, is(notNullValue()));
                }
            });
            Assert.fail();
        } catch (ValidationError e) {
            // should be here to check mockery values
            // DO NOT use (expected=...)
            mockery.assertIsSatisfied();
        }
    }

    @Test
    public void validDataDoesntThrowException() {
        DefaultValidator validator = new DefaultValidator(proxifier, result,logic);
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
