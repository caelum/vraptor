package br.com.caelum.vraptor.deserialization;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.resource.DefaultResourceClass;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.serialization.xstream.XStreamBuilderImpl;

public class XStreamXmlDeserializerTest {

	private XStreamXMLDeserializer deserializer;
	private ResourceMethod bark;
	private ParameterNameProvider provider;
	private ResourceMethod jump;
	private DefaultResourceMethod woof;
	private DefaultResourceMethod dropDead;
	private DefaultResourceMethod annotated;
	private DefaultResourceMethod walk;

	@Before
	public void setUp() throws Exception {
		provider = mock(ParameterNameProvider.class);

		deserializer = new XStreamXMLDeserializer(provider, XStreamBuilderImpl.cleanInstance());
		DefaultResourceClass resourceClass = new DefaultResourceClass(DogController.class);
		DefaultResourceClass resourcePersonClass = new DefaultResourceClass(PersonController.class);

		woof = new DefaultResourceMethod(resourceClass, DogController.class.getDeclaredMethod("woof"));
		bark = new DefaultResourceMethod(resourceClass, DogController.class.getDeclaredMethod("bark", Dog.class));
		jump = new DefaultResourceMethod(resourceClass, DogController.class.getDeclaredMethod("jump", Dog.class, Integer.class));
		dropDead = new DefaultResourceMethod(resourceClass, DogController.class.getDeclaredMethod("dropDead", Integer.class, Dog.class));
		annotated = new DefaultResourceMethod(resourceClass, DogController.class.getDeclaredMethod("annotated", DogWithAnnotations.class));
		walk = new DefaultResourceMethod(resourcePersonClass, PersonController.class.getDeclaredMethod("walk", Person.class));
	}

	static class Person {
		private String name;
		private Dog dog;
	}
	
	static class PersonController {
		public void walk(Person person){}
	}
	
	@XStreamAlias("dogAnnotated")
	static class DogWithAnnotations {
		
		@XStreamAlias("nameAnnotated")
		private String name;
		
		@XStreamAlias("ageAnnotated")
		private Integer age;
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
		public void annotated(DogWithAnnotations dog){
		}

	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAcceptMethodsWithoutArguments() throws Exception {
		deserializer.deserialize(new ByteArrayInputStream(new byte[0]), woof);
	}

	@Test
	public void shouldBeAbleToDeserializeAPersonWithDog() throws Exception {
		InputStream stream = new ByteArrayInputStream("<person><name>Renan</name><dog><name>Brutus</name><age>7</age></dog></person>".getBytes());
		
		
		when(provider.parameterNamesFor(walk.getMethod())).thenReturn(new String[] {"person"});
		
		Object[] deserialized = deserializer.deserialize(stream, walk);
		
		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(instanceOf(Person.class)));
		Person person = (Person) deserialized[0];
		assertThat(person.name, is("Renan"));
		assertThat(person.dog.name, is("Brutus"));
		assertThat(person.dog.age, is(7));
	}
	
	@Test
	public void shouldBeAbleToDeserializeADog() throws Exception {
		InputStream stream = new ByteArrayInputStream("<dog><name>Brutus</name><age>7</age></dog>".getBytes());


		when(provider.parameterNamesFor(bark.getMethod())).thenReturn(new String[] {"dog"});

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

		when(provider.parameterNamesFor(jump.getMethod())).thenReturn(new String[] {"dog", "times"});

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

		when(provider.parameterNamesFor(dropDead.getMethod())).thenReturn(new String[] {"times", "dog"});

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

		when(provider.parameterNamesFor(bark.getMethod())).thenReturn(new String[] {"pet"});

		Object[] deserialized = deserializer.deserialize(stream, bark);

		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(instanceOf(Dog.class)));
		Dog dog = (Dog) deserialized[0];
		assertThat(dog.name, is("Brutus"));
		assertThat(dog.age, is(7));
	}
	
	@Test
	public void shouldBeAbleToDeserializeADogWhenAliasConfiguredByAnnotations() {
	
		InputStream stream = new ByteArrayInputStream("<dogAnnotated><nameAnnotated>Lubi</nameAnnotated><ageAnnotated>8</ageAnnotated></dogAnnotated>".getBytes());
		
		when(provider.parameterNamesFor(annotated.getMethod())).thenReturn(new String[] {"dog"});
		
		Object[] deserialized = deserializer.deserialize(stream, annotated);
		
		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(instanceOf(DogWithAnnotations.class)));
		
		DogWithAnnotations dog = (DogWithAnnotations) deserialized[0];
		assertThat(dog.name, is("Lubi"));
		assertThat(dog.age, is(8));
	}

}
