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
	private XStreamXMLDeserializer deserializer;
	private ResourceMethod bark;
	private ParameterNameProvider provider;
	private ResourceMethod jump;
	private DefaultResourceMethod woof;
	private DefaultResourceMethod dropDead;

	@Before
	public void setUp() throws Exception {
		mockery = new VRaptorMockery();

		provider = mockery.mock(ParameterNameProvider.class);

		deserializer = new XStreamXMLDeserializer(provider);
		DefaultResourceClass resourceClass = new DefaultResourceClass(DogController.class);

		woof = new DefaultResourceMethod(resourceClass, DogController.class.getDeclaredMethod("woof"));
		bark = new DefaultResourceMethod(resourceClass, DogController.class.getDeclaredMethod("bark", Dog.class));
		jump = new DefaultResourceMethod(resourceClass, DogController.class.getDeclaredMethod("jump", Dog.class, Integer.class));
		dropDead = new DefaultResourceMethod(resourceClass, DogController.class.getDeclaredMethod("dropDead", Integer.class, Dog.class));
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

		public void jump(Dog dog, Integer times) {
		}
		public void dropDead(Integer times, Dog dog) {
		}

	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAcceptMethodsWithoutArguments() throws Exception {
		deserializer.deserialize(new ByteArrayInputStream(new byte[0]), woof);
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
	public void shouldBeAbleToDeserializeADogWhenMethodHasMoreThanOneArgument() throws Exception {
		InputStream stream = new ByteArrayInputStream("<dog><name>Brutus</name><age>7</age></dog>".getBytes());


		mockery.checking(new Expectations() {
			{
				one(provider).parameterNamesFor(jump.getMethod());
				will(returnValue(new String[] {"dog", "times"}));
			}
		});
		Object[] deserialized = deserializer.deserialize(stream, jump);

		assertThat(deserialized.length, is(2));
		assertThat(deserialized[0], is(instanceOf(Dog.class)));
		Dog dog = (Dog) deserialized[0];
		assertThat(dog.name, is("Brutus"));
		assertThat(dog.age, is(7));
	}
	@Test
	public void shouldBeAbleToDeserializeADogWhenMethodHasMoreThanOneArgumentAndTheXmlIsTheLastOne() throws Exception {
		InputStream stream = new ByteArrayInputStream("<dog><name>Brutus</name><age>7</age></dog>".getBytes());

		mockery.checking(new Expectations() {
			{
				one(provider).parameterNamesFor(dropDead.getMethod());
				will(returnValue(new String[] {"times", "dog"}));
			}
		});

		Object[] deserialized = deserializer.deserialize(stream, dropDead);

		assertThat(deserialized.length, is(2));
		assertThat(deserialized[1], is(instanceOf(Dog.class)));
		Dog dog = (Dog) deserialized[1];
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
