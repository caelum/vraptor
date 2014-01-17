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
import br.com.caelum.vraptor.interceptor.DefaultTypeNameExtractor;
import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.resource.DefaultResourceClass;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.ResourceClass;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.serialization.xstream.XStreamBuilderImpl;

public class JsonDeserializerTest {

	private JsonDeserializer deserializer;
	private ParameterNameProvider provider;
	private TypeNameExtractor extractor;
	
	private ResourceMethod meow;
	private ResourceMethod roll;
	private ResourceMethod jump;
	private ResourceMethod sleep;
	private ResourceMethod annotated;

	@Before
	public void setUp() throws Exception {
		provider = mock(ParameterNameProvider.class);

		extractor = new DefaultTypeNameExtractor();
		
	deserializer = new JsonDeserializer(provider, extractor, XStreamBuilderImpl.cleanInstance());
	
		ResourceClass resourceClass = new DefaultResourceClass(CatController.class);

		meow = new DefaultResourceMethod(resourceClass, CatController.class.getDeclaredMethod("meow"));
		roll = new DefaultResourceMethod(resourceClass, CatController.class.getDeclaredMethod("roll", Cat.class));
		jump = new DefaultResourceMethod(resourceClass, CatController.class.getDeclaredMethod("jump", Cat.class, Integer.class));
		sleep = new DefaultResourceMethod(resourceClass, CatController.class.getDeclaredMethod("sleep", Integer.class, Cat.class));
		annotated = new DefaultResourceMethod(resourceClass, CatController.class.getDeclaredMethod("annotated", CatWithAnnotations.class));
	}

	@XStreamAlias("catAnnotated")
	static class CatWithAnnotations {
		
		@XStreamAlias("nameAnnotated")
		private String name;
		
		@XStreamAlias("ageAnnotated")
		private Integer age;
	}
	
	static class Cat {
		private String name;
		private Integer age;
	}

	static class CatController {

		public void meow() {
		}
		public void roll(Cat cat) {
		}

		public void jump(Cat cat, Integer times) {
		}
		public void sleep(Integer hours, Cat cat) {
		}
		public void annotated(CatWithAnnotations cat){
		}

	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAcceptMethodsWithoutArguments() throws Exception {
		deserializer.deserialize(new ByteArrayInputStream(new byte[0]), meow);
	}
	@Test
	public void shouldBeAbleToDeserializeACat() throws Exception {
		InputStream stream = new ByteArrayInputStream("{\"cat\":{\"name\": \"Samantha\", \"age\": 2}}".getBytes());


		when(provider.parameterNamesFor(roll.getMethod())).thenReturn(new String[] {"cat"});

		Object[] deserialized = deserializer.deserialize(stream, roll);

		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(instanceOf(Cat.class)));
		Cat cat = (Cat) deserialized[0];
		assertThat(cat.name, is("Samantha"));
		assertThat(cat.age, is(2));
	}
	@Test
	public void shouldBeAbleToDeserializeACatWhenMethodHasMoreThanOneArgument() throws Exception {
		InputStream stream = new ByteArrayInputStream("{\"cat\":{\"name\": \"Zulu\", \"age\": 1}}".getBytes());

		when(provider.parameterNamesFor(jump.getMethod())).thenReturn(new String[] {"cat", "times"});

		Object[] deserialized = deserializer.deserialize(stream, jump);

		assertThat(deserialized.length, is(2));
		assertThat(deserialized[0], is(instanceOf(Cat.class)));
		Cat cat = (Cat) deserialized[0];
		assertThat(cat.name, is("Zulu"));
		assertThat(cat.age, is(1));
	}
	@Test
	public void shouldBeAbleToDeserializeACatWhenMethodHasMoreThanOneArgumentAndTheXmlIsTheLastOne() throws Exception {
		InputStream stream = new ByteArrayInputStream("{\"cat\":{\"name\": \"Tigre\", \"age\": 3}}".getBytes());

		when(provider.parameterNamesFor(sleep.getMethod())).thenReturn(new String[] {"hours", "cat"});

		Object[] deserialized = deserializer.deserialize(stream, sleep);

		assertThat(deserialized.length, is(2));
		assertThat(deserialized[1], is(instanceOf(Cat.class)));
		Cat cat = (Cat) deserialized[1];
		assertThat(cat.name, is("Tigre"));
		assertThat(cat.age, is(3));
	}

	@Test
	public void shouldBeAbleToDeserializeACatNamedDifferently() throws Exception {
		InputStream stream = new ByteArrayInputStream("{\"wrong\":{\"name\": \"Samantha\", \"age\": 2}}".getBytes());

		when(provider.parameterNamesFor(roll.getMethod())).thenReturn(new String[] {"wrong"});

		Object[] deserialized = deserializer.deserialize(stream, roll);

		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(instanceOf(Cat.class)));
		Cat cat = (Cat) deserialized[0];
		assertThat(cat.name, is("Samantha"));
		assertThat(cat.age, is(2));
	}
	
	@Test
	public void shouldBeAbleToDeserializeACatWhenAliasConfiguredByAnnotations() {
	
		InputStream stream = new ByteArrayInputStream("{\"catAnnotated\":{\"nameAnnotated\": \"Zulu\", \"ageAnnotated\": 1}}".getBytes());
		
		when(provider.parameterNamesFor(annotated.getMethod())).thenReturn(new String[] {"cat"});
		
		Object[] deserialized = deserializer.deserialize(stream, annotated);
		
		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(instanceOf(CatWithAnnotations.class)));
		
		CatWithAnnotations cat = (CatWithAnnotations) deserialized[0];
		assertThat(cat.name, is("Zulu"));
		assertThat(cat.age, is(1));
	}

}
