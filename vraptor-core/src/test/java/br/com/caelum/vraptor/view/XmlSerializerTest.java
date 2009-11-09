package br.com.caelum.vraptor.view;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.core.Routes;
import br.com.caelum.vraptor.rest.DefaultStateControl;
import br.com.caelum.vraptor.rest.Transition;

public class XmlSerializerTest {


	private XmlSerializer serializer;
	private ByteArrayOutputStream stream;
	private Mockery mockery;

	@Before
    public void setup() {
        this.stream = new ByteArrayOutputStream();
        this.serializer = new XmlSerializer(stream);
        this.mockery = new Mockery();
    }
	
	
	public static class Client {
		String name;
		public Client(String name) {
			this.name = name;
		}
	}
	public static class Order {
		Client client;
		double price;
		String comments;
		public Order(Client client, double price, String comments) {
			this.client = client;
			this.price = price;
			this.comments = comments;
		}
		public String nice() {
			return "nice output";
		}
		
	}
	public static class AdvancedOrder extends Order{

		private String notes;

		public AdvancedOrder(Client client, double price, String comments, String notes) {
			super(client, price, comments);
			this.notes = notes;
		}
		
	}

	@Test
	public void shouldSerializeAllBasicFields() {
		String expectedResult = "<order>\n  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</order>";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serializer.from(order).serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}
	
	public static enum Type { basic, advanced }
	class BasicOrder extends Order {
		public BasicOrder(Client client, double price, String comments, Type type) {
			super(client, price, comments);
			this.type = type;
		}
		private Type type;
	}

	@Test
	public void shouldSerializeEnumFields() {
		String expectedResult = "<basic_order>\n  <type>basic</type>\n  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</basic_order>";
		Order order = new BasicOrder(new Client("guilherme silveira"), 15.0, "pack it nicely, please", Type.basic);
		serializer.from(order).serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}


	@Test
	public void shouldSerializeCollection() {
		String expectedResult = "<order>\n  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</order>";
		expectedResult += expectedResult;
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serializer.from(Arrays.asList(order, order)).serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldSerializeCollectionWithPrefixTag() {
		String expectedResult = "<order>\n  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</order>";
		expectedResult += expectedResult;
		expectedResult = "<orders>" + expectedResult + "</orders>";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serializer.from("orders", Arrays.asList(order, order)).serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldSerializeCollectionWithPrefixTagAndNamespace() {
		String expectedResult = "<o:order>\n  <o:price>15.0</o:price>\n  <o:comments>pack it nicely, please</o:comments>\n</o:order>";
		expectedResult += expectedResult;
		expectedResult = "<o:orders xmlns:o=\"http://www.caelum.com.br/order\">" + expectedResult + "</o:orders>";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serializer.from("orders", Arrays.asList(order, order)).namespace("http://www.caelum.com.br/order","o").serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldSerializeParentFields() {
		String expectedResult = "<advanced_order>\n  <notes>complex package</notes>\n  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</advanced_order>";
		Order order = new AdvancedOrder(null, 15.0, "pack it nicely, please", "complex package");
		serializer.from(order).serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldOptionallyExcludeFields() {
		String expectedResult = "<order>\n  <comments>pack it nicely, please</comments>\n</order>";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serializer.from(order).exclude("price").serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldOptionallyIncludeMethodReturn() {
		String expectedResult = "<order>\n<nice>nice output</nice></order>";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serializer.from(order).exclude("price","comments").addMethod("nice").serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldOptionallyIncludeChildField() {
		String expectedResult = "<order>\n<client>\n  <name>guilherme silveira</name>\n</client>  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</order>";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serializer.from(order).include("client").serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldOptionallyExcludeChildField() {
		String expectedResult = "<order>\n<client>\n</client>  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</order>";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serializer.from(order).include("client").exclude("name").serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldIncludeNamespaces() {
		String expectedResult = "<o:order xmlns:o=\"http://www.caelum.com.br/order\">\n  <o:price>15.0</o:price>\n  <o:comments>pack it nicely, please</o:comments>\n</o:order>";
		Order order = new Order(null, 15.0, "pack it nicely, please");
		serializer.from(order).namespace("http://www.caelum.com.br/order","o").serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	private String result() {
		return new String(stream.toByteArray());
	}
	
	class Process implements StateResource {
		private String status;
		private Routes routes;
		public Process(String status, Routes routes) {
			this.status = status;
			this.routes = routes;
		}
		public List<Transition> getFollowingTransitions() {
			DefaultStateControl control = new DefaultStateControl(routes);
			control.transition("initialize").uses(ProcessController.class);
			return control.getTransitions();
		}
	}
	
	class ProcessController {
		public boolean initialized;
		public void initialize() {
			this.initialized = true;
		}
	}
	
	interface StateResource {
		public List<Transition> getFollowingTransitions();
	}
	
	@Test
	public void shouldSerializeAtomLinksIfStateControlExists() {
		final Routes routes = mockery.mock(Routes.class);
		final ProcessController process = new ProcessController();
		mockery.checking(new Expectations(){{
			one(routes).uriFor(ProcessController.class); will(returnValue(process));
			one(routes).getUri(); will(returnValue("/my_link"));
		}});
		String expectedResult = "<process>\n  <status>created</status>\n  <atom:link href=\"/my_link\" rel=\"initialize\" xmlns:atom=\"http://www.w3.org/2005/Atom\" /></process>";
		Process p = new Process("created", routes);
		serializer.from(p).serialize();
		assertThat(result(), is(equalTo(expectedResult)));
		assertThat(process.initialized, is(equalTo(true)));
	}
	

}
