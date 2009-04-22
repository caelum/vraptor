package br.com.caelum.vraptor.validator;

import br.com.caelum.vraptor.view.jsp.PageResult;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import java.io.IOException;

public class ValidatorAcceptanceTest {
    private PageResult result;
    private Mockery mockery;

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
    }

    @Test
    public void cheksThatValidationWorks() throws ServletException, IOException {
        DefaultValidator validator = new DefaultValidator(result);
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
                    that(guilherme.id, is(notNullValue()));
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
        DefaultValidator validator = new DefaultValidator(result);
        final Student guilherme = new Student();
        guilherme.id = 15L;
        validator.checking(new Validations() {
            {
                // this is the Assertion itself
                that(guilherme.id, is(notNullValue()));
            }
        });
        mockery.assertIsSatisfied();
    }

}
