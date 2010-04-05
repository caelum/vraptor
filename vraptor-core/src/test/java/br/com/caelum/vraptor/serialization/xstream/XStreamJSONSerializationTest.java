package br.com.caelum.vraptor.serialization.xstream;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import br.com.caelum.vraptor.interceptor.DefaultTypeNameExtractor;

public class XStreamJSONSerializationTest {


	private XStreamJSONSerialization serialization;
	private ByteArrayOutputStream stream;

	@Before
    public void setup() throws Exception {
        this.stream = new ByteArrayOutputStream();

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(stream));

        this.serialization = new XStreamJSONSerialization(response, new DefaultTypeNameExtractor());
    }

	public static class Address {
		String street;
		public Address(String street) {
			this.street = street;
		}
	}
	public static class Client {
		String name;
		Address address;
		public Client(String name) {
			this.name = name;
		}
		public Client(String name, Address address) {
			this.name = name;
			this.address = address;
		}
	}
	public static class Item {
		String name;
		double price;
		public Item(String name, double price) {
			this.name = name;
			this.price = price;
		}
	}
	public static class Order {
		Client client;
		double price;
		String comments;
		List<Item> items;

		public Order(Client client, double price, String comments, Item... items) {
			this.client = client;
			this.price = price;
			this.comments = comments;
			this.items = new ArrayList<Item>(Arrays.asList(items));
		}
		public String nice() {
			return "nice output";
		}

	}
	public static class AdvancedOrder extends Order{

		@SuppressWarnings("unused")
		private final String notes;

		public AdvancedOrder(Client client, double price, String comments, String notes) {
			super(client, price, comments);
			this.notes = notes;
		}

	}

	@Test
	public void shouldSerializeAllBasicFields() {
		String expectedResult = "{\"order\": {\n  \"price\": 15.0,\n  \"comments\": \"pack it nicely, please\"\n}}";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serialization.from(order).serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldUseAlias() {
		String expectedResult = "{\"customOrder\": {\n  \"price\": 15.0,\n  \"comments\": \"pack it nicely, please\"\n}}";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serialization.from(order, "customOrder").serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	public static enum Type { basic, advanced }
	class BasicOrder extends Order {
		public BasicOrder(Client client, double price, String comments, Type type) {
			super(client, price, comments);
			this.type = type;
		}
		@SuppressWarnings("unused")
		private final Type type;
	}

	@Test
	public void shouldSerializeEnumFields() {
//		String expectedResult = "<basicOrder>\n  <type>basic</type>\n  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</basicOrder>";
		Order order = new BasicOrder(new Client("guilherme silveira"), 15.0, "pack it nicely, please", Type.basic);
		serialization.from(order).serialize();
		String result = result();
		assertThat(result, containsString("\"type\": \"basic\""));
	}


	@Test
	public void shouldSerializeCollection() {
		String expectedResult = "  {\n    \"price\": 15.0,\n    \"comments\": \"pack it nicely, please\"\n  }";
		expectedResult += ",\n" + expectedResult;
		expectedResult = "{\"list\": [\n" + expectedResult + "\n]}";

		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serialization.from(Arrays.asList(order, order)).serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldSerializeCollectionWithPrefixTag() {
		String expectedResult = "  {\n    \"price\": 15.0,\n    \"comments\": \"pack it nicely, please\"\n  }";
		expectedResult += ",\n" + expectedResult;
		expectedResult = "{\"orders\": [\n" + expectedResult + "\n]}";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serialization.from(Arrays.asList(order, order), "orders").serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldExcludeNonPrimitiveFieldsFromACollection() {
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please",
				new Item("name", 12.99));
		serialization.from(Arrays.asList(order, order), "orders").exclude("price").serialize();

		assertThat(result(), not(containsString("\"items\"")));
		assertThat(result(), not(containsString("name")));
		assertThat(result(), not(containsString("\"price\"")));
		assertThat(result(), not(containsString("12.99")));
		assertThat(result(), not(containsString("15.0")));
	}

	@Test
	@Ignore("not supported yet")
	public void shouldSerializeCollectionWithPrefixTagAndNamespace() {
		String expectedResult = "<o:order>\n  <o:price>15.0</o:price>\n  <o:comments>pack it nicely, please</o:comments>\n</o:order>";
		expectedResult += expectedResult;
		expectedResult = "<o:orders xmlns:o=\"http://www.caelum.com.br/order\">" + expectedResult + "</o:orders>";
//		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
//		serializer.from("orders", Arrays.asList(order, order)).namespace("http://www.caelum.com.br/order","o").serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldSerializeParentFields() {
//		String expectedResult = "<advancedOrder>\n  <notes>complex package</notes>\n  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</advancedOrder>";
		Order order = new AdvancedOrder(null, 15.0, "pack it nicely, please", "complex package");
		serialization.from(order).serialize();
		assertThat(result(), containsString("\"notes\": \"complex package\""));
	}

	@Test
	public void shouldOptionallyExcludeFields() {
		String expectedResult = "{\"order\": {\n  \"comments\": \"pack it nicely, please\"\n}}";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serialization.from(order).exclude("price").serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldOptionallyIncludeFieldAndNotItsNonPrimitiveFields() {
//		String expectedResult = "<order>\n<client>\n  <name>guilherme silveira</name>\n </client>  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</order>";
		Order order = new Order(new Client("guilherme silveira", new Address("R. Vergueiro")), 15.0, "pack it nicely, please");
		serialization.from(order).include("client").serialize();
		assertThat(result(), containsString("\"name\": \"guilherme silveira\""));
		assertThat(result(), not(containsString("R. Vergueiro")));
	}
	@Test
	public void shouldOptionallyIncludeChildField() {
//		String expectedResult = "<order>\n<client>\n  <name>guilherme silveira</name>\n </client>  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</order>";
		Order order = new Order(new Client("guilherme silveira", new Address("R. Vergueiro")), 15.0, "pack it nicely, please");
		serialization.from(order).include("client", "client.address").serialize();
		assertThat(result(), containsString("\"street\": \"R. Vergueiro\""));
	}

	@Test
	public void shouldOptionallyExcludeChildField() {
//		String expectedResult = "<order>\n<client>\n</client>  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</order>";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serialization.from(order).include("client").exclude("client.name").serialize();
		assertThat(result(), containsString("\"client\""));
		assertThat(result(), not(containsString("guilherme silveira")));
	}

	@Test
	public void shouldOptionallyIncludeListChildFields() {
//		String expectedResult = "<order>\n<client>\n</client>  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</order>";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please",
				new Item("any item", 12.99));
		serialization.from(order).include("items").serialize();
		assertThat(result(), containsString("\"items\""));
		assertThat(result(), containsString("\"name\": \"any item\""));
		assertThat(result(), containsString("\"price\": 12.99"));
	}
	@Test
	public void shouldOptionallyExcludeFieldsFromIncludedListChildFields() {
//		String expectedResult = "<order>\n<client>\n</client>  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</order>";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please",
				new Item("any item", 12.99));
		serialization.from(order).include("items").exclude("items.price").serialize();
		assertThat(result(), containsString("\"items\""));
		assertThat(result(), containsString("\"name\": \"any item\""));
		assertThat(result(), not(containsString("12.99")));
	}
	
	@Test
	public void shouldOptionallyRemoveRoot() {
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please",
				new Item("any item", 12.99));
		serialization.withoutRoot().from(order).include("items").exclude("items.price").serialize();
		assertThat(result(), containsString("\"items\""));
		assertThat(result(), containsString("\"name\": \"any item\""));
		assertThat(result(), not(containsString("12.99")));
		assertThat(result(), not(containsString("{\"order\": {")));
	}

	private String result() {
		return new String(stream.toByteArray());
	}

	public class SomeProxy implements HibernateProxy {
		private String aField;

		private transient LazyInitializer initializer;

		public SomeProxy(LazyInitializer initializer) {
			this.initializer = initializer;
		}
		public LazyInitializer getHibernateLazyInitializer() {
			return initializer;
		}

		public Object writeReplace() {
			return this;
		}

	}
	@Test
	public void shouldRunHibernateLazyInitialization() throws Exception {
		LazyInitializer initializer = mock(LazyInitializer.class);

		SomeProxy proxy = new SomeProxy(initializer);
		proxy.aField = "abc";

		when(initializer.getPersistentClass()).thenReturn(SomeProxy.class);

		serialization.from(proxy).serialize();

		assertThat(result(), is("{\"someProxy\": {\n  \"aField\": \"abc\"\n}}"));

		verify(initializer).initialize();
	}


}
