package br.com.caelum.vraptor.deserialization;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.resource.DefaultResourceClass;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.test.VRaptorMockery;

public class XStreamXmlDeserializerTest {


	private VRaptorMockery mockery;
	private XStreamXmlDeserializer deserializer;
	private ResourceMethod bark;
	private ParameterNameProvider provider;
	private ResourceMethod jump;
	private DefaultResourceMethod woof;

	@Before
	public void setUp() throws Exception {
		mockery = new VRaptorMockery();

		provider = mockery.mock(ParameterNameProvider.class);

		deserializer = new XStreamXmlDeserializer(provider);
		DefaultResourceClass resourceClass = new DefaultResourceClass(DogController.class);

		woof = new DefaultResourceMethod(resourceClass, DogController.class.getDeclaredMethod("woof"));
		bark = new DefaultResourceMethod(resourceClass, DogController.class.getDeclaredMethod("bark", Dog.class));
		jump = new DefaultResourceMethod(resourceClass, DogController.class.getDeclaredMethod("jump", String.class, String.class));
	}

	static class Dog {
		private String name;
		private Integer age;
	}

	static class DogController {

		public void woof() {
		}
		public void bark(Dog dog) {
		}

		public void jump(String from, String to) {
		}
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAcceptMethodsWithoutArguments() throws Exception {
		deserializer.deserialize(new ByteArrayInputStream(new byte[0]), woof);
	}
	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAcceptMethodsWithMoreThanOneArgument() throws Exception {
		deserializer.deserialize(new ByteArrayInputStream(new byte[0]), jump);
	}
	@Test
	public void shouldBeAbleToDeserializeADog() throws Exception {
		InputStream stream = new ByteArrayInputStream("<dog><name>Brutus</name><age>7</age></dog>".getBytes());


		mockery.checking(new Expectations() {
			{
				one(provider).parameterNamesFor(bark.getMethod());
				will(returnValue(new String[] {"dog"}));
			}
		});
		Object[] deserialized = deserializer.deserialize(stream, bark);

		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(instanceOf(Dog.class)));
		Dog dog = (Dog) deserialized[0];
		assertThat(dog.name, is("Brutus"));
		assertThat(dog.age, is(7));
	}

	@Test
	public void shouldBeAbleToDeserializeADogNamedDifferently() throws Exception {
		InputStream stream = new ByteArrayInputStream("<pet><name>Brutus</name><age>7</age></pet>".getBytes());


		mockery.checking(new Expectations() {
			{
				one(provider).parameterNamesFor(bark.getMethod());
				will(returnValue(new String[] {"pet"}));
			}
		});
		Object[] deserialized = deserializer.deserialize(stream, bark);

		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(instanceOf(Dog.class)));
		Dog dog = (Dog) deserialized[0];
		assertThat(dog.name, is("Brutus"));
		assertThat(dog.age, is(7));
	}

}
