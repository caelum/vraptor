package br.com.caelum.vraptor.serialization;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import br.com.caelum.vraptor.interceptor.DefaultTypeNameExtractor;

import com.thoughtworks.xstream.XStream;

public class XStreamXmlSerializerTest {


	private Serializer serializer;
	private ByteArrayOutputStream stream;

	@Before
    public void setup() {
        this.stream = new ByteArrayOutputStream();
        this.serializer = new XStreamXmlSerializer(new XStream(), new OutputStreamWriter(stream), new DefaultTypeNameExtractor());
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

		private final String notes;

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
		private final Type type;
	}

	@Test
	public void shouldSerializeEnumFields() {
//		String expectedResult = "<basicOrder>\n  <type>basic</type>\n  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</basicOrder>";
		Order order = new BasicOrder(new Client("guilherme silveira"), 15.0, "pack it nicely, please", Type.basic);
		serializer.from(order).serialize();
		String result = result();
		assertThat(result, containsString("<type>basic</type>"));
	}


	@Test
	@Ignore("It makes sense?")
	public void shouldSerializeCollection() {
		String expectedResult = "<order>\n  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</order>";
		expectedResult += expectedResult;
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serializer.from(Arrays.asList(order, order)).serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	@Ignore("not supported yet")
	public void shouldSerializeCollectionWithPrefixTag() {
		String expectedResult = "<order>\n  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</order>";
		expectedResult += expectedResult;
		expectedResult = "<orders>" + expectedResult + "</orders>";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		//serializer.from("orders", Arrays.asList(order, order)).serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	@Ignore("not supported yet")
	public void shouldSerializeCollectionWithPrefixTagAndNamespace() {
		String expectedResult = "<o:order>\n  <o:price>15.0</o:price>\n  <o:comments>pack it nicely, please</o:comments>\n</o:order>";
		expectedResult += expectedResult;
		expectedResult = "<o:orders xmlns:o=\"http://www.caelum.com.br/order\">" + expectedResult + "</o:orders>";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
//		serializer.from("orders", Arrays.asList(order, order)).namespace("http://www.caelum.com.br/order","o").serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldSerializeParentFields() {
//		String expectedResult = "<advancedOrder>\n  <notes>complex package</notes>\n  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</advancedOrder>";
		Order order = new AdvancedOrder(null, 15.0, "pack it nicely, please", "complex package");
		serializer.from(order).serialize();
		assertThat(result(), containsString("<notes>complex package</notes>"));
	}

	@Test
	public void shouldOptionallyExcludeFields() {
		String expectedResult = "<order>\n  <comments>pack it nicely, please</comments>\n</order>";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serializer.from(order).exclude("price").serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldOptionallyIncludeFieldAndNotItsNonPrimitiveFields() {
//		String expectedResult = "<order>\n<client>\n  <name>guilherme silveira</name>\n </client>  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</order>";
		Order order = new Order(new Client("guilherme silveira", new Address("R. Vergueiro")), 15.0, "pack it nicely, please");
		serializer.from(order).include("client").serialize();
		assertThat(result(), containsString("<name>guilherme silveira</name>"));
		assertThat(result(), not(containsString("R. Vergueiro")));
	}
	@Test
	public void shouldOptionallyIncludeChildField() {
//		String expectedResult = "<order>\n<client>\n  <name>guilherme silveira</name>\n </client>  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</order>";
		Order order = new Order(new Client("guilherme silveira", new Address("R. Vergueiro")), 15.0, "pack it nicely, please");
		serializer.from(order).include("client", "client.address").serialize();
		assertThat(result(), containsString("<street>R. Vergueiro</street>"));
	}

	@Test
	public void shouldOptionallyExcludeChildField() {
//		String expectedResult = "<order>\n<client>\n</client>  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</order>";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serializer.from(order).include("client").exclude("client.name").serialize();
		assertThat(result(), containsString("<client/>"));
		assertThat(result(), not(containsString("<name>guilherme silveira</name>")));
	}
	@Test
	public void shouldOptionallyIncludeListChildFields() {
//		String expectedResult = "<order>\n<client>\n</client>  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</order>";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please",
				new Item("any item", 12.99));
		serializer.from(order).include("items").serialize();
		assertThat(result(), containsString("<items>"));
		assertThat(result(), containsString("<name>any item</name>"));
		assertThat(result(), containsString("<price>12.99</price>"));
		assertThat(result(), containsString("</items>"));
	}
	@Test
	public void shouldOptionallyExcludeFieldsFromIncludedListChildFields() {
//		String expectedResult = "<order>\n<client>\n</client>  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</order>";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please",
				new Item("any item", 12.99));
		serializer.from(order).include("items").exclude("items.price").serialize();
		assertThat(result(), containsString("<items>"));
		assertThat(result(), containsString("<name>any item</name>"));
		assertThat(result(), not(containsString("12.99")));
		assertThat(result(), containsString("</items>"));
	}

	private String result() {
		return new String(stream.toByteArray());
	}


}
