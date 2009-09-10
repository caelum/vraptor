package br.com.caelum.vraptor.view;

import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.proxy.DefaultProxifier;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.util.test.MockedLogic;
import br.com.caelum.vraptor.util.test.MockedPage;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationError;

public class DefaultValidationViewsFactoryTest {


	private Mockery mockery;
	private Result result;
	private Proxifier proxifier;
	private DefaultValidationViewsFactory factory;
	private List<Message> errors;

	@Before
	public void setUp() throws Exception {
		mockery = new Mockery();
		result = mockery.mock(Result.class);
		proxifier = new DefaultProxifier();
		factory = new DefaultValidationViewsFactory(result, proxifier);
		errors = Collections.emptyList();

	}


	public static class RandomComponent {
		public void random() {

		}
	}
	@Test(expected=ValidationError.class)
	public void shouldUseValidationVersionOfLogicResult() throws Exception {

		mockery.checking(new Expectations() {
			{
				one(result).use(LogicResult.class);
				will(returnValue(new MockedLogic()));
			}
		});
		factory.instanceFor(LogicResult.class, errors).forwardTo(RandomComponent.class).random();
	}
	@Test
	public void shouldThrowExceptionOnlyAtTheEndOfValidationCall() throws Exception {

		mockery.checking(new Expectations() {
			{
				exactly(2).of(result).use(LogicResult.class);
				will(returnValue(new MockedLogic()));
				exactly(2).of(result).use(PageResult.class);
				will(returnValue(new MockedPage()));
			}
		});
		factory.instanceFor(LogicResult.class, errors);
		factory.instanceFor(LogicResult.class, errors).forwardTo(RandomComponent.class);
		factory.instanceFor(PageResult.class, errors);
		factory.instanceFor(PageResult.class, errors).of(RandomComponent.class);

		mockery.assertIsSatisfied();
	}
	@Test(expected=ValidationError.class)
	public void shouldUseValidationVersionOfPageResult() throws Exception {

		mockery.checking(new Expectations() {
			{
				one(result).use(PageResult.class);
				will(returnValue(new MockedPage()));
			}
		});
		factory.instanceFor(PageResult.class, errors).forward("any uri");
	}
	@Test(expected=ValidationError.class)
	public void shouldUseValidationVersionOfEmptyResult() throws Exception {

		mockery.checking(new Expectations() {
			{
				one(result).use(EmptyResult.class);
				will(returnValue(new EmptyResult()));
			}
		});
		factory.instanceFor(EmptyResult.class, errors);
	}
}
