package br.com.caelum.vraptor.serialization.xstream;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import br.com.caelum.vraptor.interceptor.DefaultTypeNameExtractor;
import br.com.caelum.vraptor.serialization.NullProxyInitializer;
import br.com.caelum.vraptor.serialization.Serialization;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class XStreamXMLSerializationTest {

	protected Serialization serialization;
	protected ByteArrayOutputStream stream;

	@Before
    public void setup() throws Exception {
        this.stream = new ByteArrayOutputStream();

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(stream));
        
		this.serialization = new XStreamXMLSerialization(response, new DefaultTypeNameExtractor(), new NullProxyInitializer(), XStreamBuilderImpl.cleanInstance());
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
	public static class Properties {
		Map<String, String> map;
		public Properties(String key, String value) {
			map = new HashMap<String, String>(Collections.singletonMap(key, value));
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

	public static class GenericWrapper<T> {

		Collection<T> entityList;
		Integer total;

		public GenericWrapper(Collection<T> entityList, Integer total) {
			this.entityList = entityList;
			this.total = total;
		}

	}

	@Test
    public void shouldSerializeGenericClass() {
		String expectedResult = "<genericWrapper>\n  <entityList class=\"list\">\n    <client>\n      <name>washington botelho</name>\n    </client>\n    <client>\n      <name>washington botelho</name>\n    </client>\n  </entityList>\n  <total>2</total>\n</genericWrapper>";

		Collection<Client> entityList = new ArrayList<Client>();
		entityList.add(new Client("washington botelho"));
		entityList.add(new Client("washington botelho"));

		GenericWrapper<Client> wrapper = new GenericWrapper<Client>(entityList, entityList.size());

        serialization.from(wrapper).include("entityList").serialize();

        assertThat(result(), is(equalTo(expectedResult)));
    }

	@Test
	public void shouldSerializeMaps() {
		String expectedResult = "<properties>\n  <map>\n    <entry>\n      <string>test</string>\n      <string>true</string>\n    </entry>\n  </map>\n</properties>";
		serialization.from(new Properties("test", "true")).include("map").serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldSerializeAllBasicFields() {
		String expectedResult = "<order>\n  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</order>";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serialization.from(order).serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}
	@Test
	public void shouldUseAlias() {
		String expectedResult = "<customOrder>\n  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</customOrder>";
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
		Order order = new BasicOrder(new Client("guilherme silveira"), 15.0, "pack it nicely, please", Type.basic);
		serialization.from(order).serialize();
		String result = result();
		assertThat(result, containsString("<type>basic</type>"));
	}


	@Test
	public void shouldSerializeCollection() {
		String expectedResult = "  <order>\n    <price>15.0</price>\n    <comments>pack it nicely, please</comments>\n  </order>\n";
		expectedResult += expectedResult;
		expectedResult = "<list>\n" + expectedResult + "</list>";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serialization.from(Arrays.asList(order, order)).serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldSerializeCollectionWithPrefixTag() {
		String expectedResult = "  <order>\n    <price>15.0</price>\n    <comments>pack it nicely, please</comments>\n  </order>\n";
		expectedResult += expectedResult;
		expectedResult = "<orders>\n" + expectedResult + "</orders>";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serialization.from(Arrays.asList(order, order), "orders").serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}
	@Test
	public void shouldIncludeFieldsFromACollection() {
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please",
				new Item("name", 12.99));
		serialization.from(Arrays.asList(order, order), "orders").include("items").serialize();

		assertThat(result(), containsString("<items>"));
		assertThat(result(), containsString("<name>name</name>"));
		assertThat(result(), containsString("<price>12.99</price>"));
		assertThat(result(), containsString("</items>"));
	}
	
	@Test
	public void shouldWorkWithEmptyCollections() {
		serialization.from(new ArrayList<Order>(), "orders").serialize();
		
		assertThat(result(), containsString("<orders/>"));
	}
	@Test
	public void shouldIncludeAllFieldsWhenRecursive() {
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please",
				new Item("name", 12.99));
		serialization.from(order).recursive().serialize();

		assertThat(result(), containsString("<items>"));
		assertThat(result(), containsString("<name>name</name>"));
		assertThat(result(), containsString("<price>12.99</price>"));
	}
	@Test
	public void shouldExcludeFieldsFromACollection() {
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serialization.from(Arrays.asList(order, order), "orders").exclude("price").serialize();

		assertThat(result(), not(containsString("<price>")));
	}
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowAnExceptionWhenYouIncludeANonExistantField() {
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please",
				new Item("name", 12.99));
		serialization.from(order).include("wrongFieldName").serialize();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowAnExceptionWhenYouIncludeANonExistantFieldInsideOther() {
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please",
				new Item("name", 12.99));
		serialization.from(order).include("wrongFieldName.another").serialize();
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
		Order order = new AdvancedOrder(null, 15.0, "pack it nicely, please", "complex package");
		serialization.from(order).serialize();
		assertThat(result(), containsString("<notes>complex package</notes>"));
	}

	@Test
	public void shouldOptionallyExcludeFields() {
		String expectedResult = "<order>\n  <comments>pack it nicely, please</comments>\n</order>";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serialization.from(order).exclude("price").serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldOptionallyIncludeFieldAndNotItsNonPrimitiveFields() {
//		String expectedResult = "<order>\n<client>\n  <name>guilherme silveira</name>\n </client>  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</order>";
		Order order = new Order(new Client("guilherme silveira", new Address("R. Vergueiro")), 15.0, "pack it nicely, please");
		serialization.from(order).include("client").serialize();
		assertThat(result(), containsString("<name>guilherme silveira</name>"));
		assertThat(result(), not(containsString("R. Vergueiro")));
	}
	@Test
	public void shouldOptionallyIncludeChildField() {
//		String expectedResult = "<order>\n<client>\n  <name>guilherme silveira</name>\n </client>  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</order>";
		Order order = new Order(new Client("guilherme silveira", new Address("R. Vergueiro")), 15.0, "pack it nicely, please");
		serialization.from(order).include("client", "client.address").serialize();
		assertThat(result(), containsString("<street>R. Vergueiro</street>"));
	}

	@Test
	public void shouldOptionallyExcludeChildField() {
//		String expectedResult = "<order>\n<client>\n</client>  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</order>";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serialization.from(order).include("client").exclude("client.name").serialize();
		assertThat(result(), containsString("<client/>"));
		assertThat(result(), not(containsString("<name>guilherme silveira</name>")));
	}
	@Test
	public void shouldOptionallyIncludeListChildFields() {
//		String expectedResult = "<order>\n<client>\n</client>  <price>15.0</price>\n  <comments>pack it nicely, please</comments>\n</order>";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please",
				new Item("any item", 12.99));
		serialization.from(order).include("items").serialize();
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
		serialization.from(order).include("items").exclude("items.price").serialize();
		assertThat(result(), containsString("<items>"));
		assertThat(result(), containsString("<name>any item</name>"));
		assertThat(result(), not(containsString("12.99")));
		assertThat(result(), containsString("</items>"));
	}

	static class WithAlias {
		@SuppressWarnings("unused")
		@XStreamAlias("def")
		private String abc;
	}

	static class WithAliasedAttribute {
		@SuppressWarnings("unused")
		private WithAlias aliased;
	}

	@Test
	public void shouldAutomaticallyReadXStreamAnnotations() {
		WithAlias alias = new WithAlias();
		alias.abc = "Duh!";
		serialization.from(alias).serialize();
		assertThat(result(), is("<withAlias>\n  <def>Duh!</def>\n</withAlias>"));
	}

	@Test
	public void shouldAutomaticallyReadXStreamAnnotationsForIncludedAttributes() {
		WithAlias alias = new WithAlias();
		alias.abc = "Duh!";

		WithAliasedAttribute attribute = new WithAliasedAttribute();
		attribute.aliased = alias;

		serialization.from(attribute).include("aliased").serialize();
		assertThat(result(), is("<withAliasedAttribute>\n  <aliased>\n    <def>Duh!</def>\n  </aliased>\n</withAliasedAttribute>"));
	}

	private String result() {
		return new String(stream.toByteArray());
	}
	
	/**
	 * @bug #400
	 */
	class A {
		C field1 = new C();
	}

	class B extends A {
		C field2 = new C();
	}
	class C {
		
	}
	
	@Test
	public void shouldBeAbleToIncludeSubclassesFields() throws Exception {
		serialization.from(new B()).include("field2").serialize();
		assertThat(result(), is("<b>\n  <field2/>\n</b>"));
	}

}

