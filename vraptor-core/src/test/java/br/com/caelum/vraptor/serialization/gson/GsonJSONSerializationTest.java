package br.com.caelum.vraptor.serialization.gson;

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
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import br.com.caelum.vraptor.interceptor.DefaultTypeNameExtractor;
import br.com.caelum.vraptor.serialization.HibernateProxyInitializer;
import br.com.caelum.vraptor.serialization.gson.adapters.CalendarSerializer;

import com.google.common.collect.ForwardingCollection;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class GsonJSONSerializationTest {

	private GsonJSONSerialization serialization;

	private ByteArrayOutputStream stream;

	private HttpServletResponse response;

	private DefaultTypeNameExtractor extractor;

	private HibernateProxyInitializer initializer;

	@Before
	public void setup() throws Exception {
		this.stream = new ByteArrayOutputStream();

		response = mock(HttpServletResponse.class);
		when(response.getWriter()).thenReturn(new PrintWriter(stream));
		extractor = new DefaultTypeNameExtractor();
		initializer = new HibernateProxyInitializer();

		this.serialization = new GsonJSONSerialization(response, extractor, initializer,
				Collections.<JsonSerializer<?>> emptyList(), Collections.<ExclusionStrategy> emptyList());
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

		Calendar included;

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

	public static class AdvancedOrder extends Order {

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

	public static class ClientAddressExclusion implements ExclusionStrategy {

		public boolean shouldSkipField(FieldAttributes f) {
			return f.getName().equals("address");
		}

		public boolean shouldSkipClass(Class<?> clazz) {
			return false;
		}

	}

	@Test
	public void shouldSerializeGenericClass() {
		String expectedResult = "{\"genericWrapper\":{\"entityList\":[{\"name\":\"washington botelho\"},{\"name\":\"washington botelho\"}],\"total\":2}}";

		Collection<Client> entityList = new ArrayList<Client>();
		entityList.add(new Client("washington botelho"));
		entityList.add(new Client("washington botelho"));

		GenericWrapper<Client> wrapper = new GenericWrapper<Client>(entityList, entityList.size());

		// serialization.from(wrapper).include("entityList").include("entityList.name").serialize();
		serialization.from(wrapper).include("entityList").serialize();

		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldSerializeAllBasicFields() {
		String expectedResult = "{\"order\":{\"price\":15.0,\"comments\":\"pack it nicely, please\"}}";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serialization.from(order).serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldSerializeAllBasicFieldsIdented() {
		String expectedResult = "{\n  \"order\": {\n    \"price\": 15.0,\n    \"comments\": \"pack it nicely, please\"\n  }\n}";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serialization.indented().from(order).serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldUseAlias() {
		String expectedResult = "{\"customOrder\":{\"price\":15.0,\"comments\":\"pack it nicely, please\"}}";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serialization.from(order, "customOrder").serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	public static enum Type {
		basic, advanced
	}

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
		// String expectedResult =
		// "<basicOrder><type>basic</type><price>15.0</price><comments>pack it nicely, please</comments></basicOrder>";
		Order order = new BasicOrder(new Client("guilherme silveira"), 15.0, "pack it nicely, please",
				Type.basic);
		serialization.from(order).serialize();
		String result = result();
		assertThat(result, containsString("\"type\":\"basic\""));
	}

	@Test
	public void shouldSerializeCollection() {
		String expectedResult = "{\"price\":15.0,\"comments\":\"pack it nicely, please\"}";
		expectedResult += "," + expectedResult;
		expectedResult = "{\"list\":[" + expectedResult + "]}";

		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serialization.from(Arrays.asList(order, order)).serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldSerializeCollectionWithPrefixTag() {
		String expectedResult = "{\"price\":15.0,\"comments\":\"pack it nicely, please\"}";
		expectedResult += "," + expectedResult;
		expectedResult = "{\"orders\":[" + expectedResult + "]}";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serialization.from(Arrays.asList(order, order), "orders").serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldExcludeNonPrimitiveFieldsFromACollection() {
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please", new Item(
				"name", 12.99));
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
		String expectedResult = "<o:order><o:price>15.0</o:price><o:comments>pack it nicely, please</o:comments></o:order>";
		expectedResult += expectedResult;
		expectedResult = "<o:orders xmlns:o=\"http://www.caelum.com.br/order\">" + expectedResult
				+ "</o:orders>";
		// Order order = new Order(new Client("guilherme silveira"), 15.0,
		// "pack it nicely, please");
		// serializer.from("orders", Arrays.asList(order,
		// order)).namespace("http://www.caelum.com.br/order","o").serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	static class WithAdvanced {
		AdvancedOrder order;
	}

	@Test
	public void shouldSerializeParentFields() {
		// String expectedResult =
		// "<advancedOrder><notes>complex package</notes><price>15.0</price><comments>pack it nicely, please</comments></advancedOrder>";
		Order order = new AdvancedOrder(null, 15.0, "pack it nicely, please", "complex package");
		serialization.from(order).serialize();
		assertThat(result(), containsString("\"notes\":\"complex package\""));
	}

	@Test
	public void shouldExcludeNonPrimitiveParentFields() {
		// String expectedResult =
		// "<advancedOrder><notes>complex package</notes><price>15.0</price><comments>pack it nicely, please</comments></advancedOrder>";
		WithAdvanced advanced = new WithAdvanced();
		advanced.order = new AdvancedOrder(new Client("john"), 15.0, "pack it nicely, please",
				"complex package");
		serialization.from(advanced).include("order").serialize();
		assertThat(result(), not(containsString("\"client\"")));
	}

	@Test
	public void shouldExcludeParentFields() {
		Order order = new AdvancedOrder(null, 15.0, "pack it nicely, please", "complex package");
		serialization.from(order).exclude("comments").serialize();
		assertThat(result(), not(containsString("\"comments\"")));
	}

	@Test
	public void shouldOptionallyExcludeFields() {
		String expectedResult = "{\"order\":{\"comments\":\"pack it nicely, please\"}}";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serialization.from(order).exclude("price").serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldOptionallyIncludeFieldAndNotItsNonPrimitiveFields() {
		// String expectedResult =
		// "<order><client><name>guilherme silveira</name> </client>  <price>15.0</price><comments>pack it nicely, please</comments></order>";
		Order order = new Order(new Client("guilherme silveira", new Address("R. Vergueiro")), 15.0,
				"pack it nicely, please");
		serialization.from(order).include("client").serialize();
		assertThat(result(), containsString("\"name\":\"guilherme silveira\""));
		assertThat(result(), not(containsString("R. Vergueiro")));
	}

	@Test
	public void shouldOptionallyIncludeChildField() {
		// String expectedResult =
		// "<order><client><name>guilherme silveira</name> </client>  <price>15.0</price><comments>pack it nicely, please</comments></order>";
		Order order = new Order(new Client("guilherme silveira", new Address("R. Vergueiro")), 15.0,
				"pack it nicely, please");
		serialization.from(order).include("client", "client.address").serialize();
		assertThat(result(), containsString("\"street\":\"R. Vergueiro\""));
	}

	@Test
	public void shouldOptionallyExcludeChildField() {
		// String expectedResult =
		// "<order><client></client>  <price>15.0</price><comments>pack it nicely, please</comments></order>";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serialization.from(order).include("client").exclude("client.name").serialize();
		assertThat(result(), containsString("\"client\""));
		assertThat(result(), not(containsString("guilherme silveira")));
	}

	@Test
	public void shouldOptionallyIncludeListChildFields() {
		// String expectedResult =
		// "<order><client></client>  <price>15.0</price><comments>pack it nicely, please</comments></order>";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please", new Item(
				"any item", 12.99));
		serialization.from(order).include("items").serialize();
		assertThat(result(), containsString("\"items\""));
		assertThat(result(), containsString("\"name\":\"any item\""));
		assertThat(result(), containsString("\"price\":12.99"));
	}

	@Test
	public void shouldOptionallyExcludeFieldsFromIncludedListChildFields() {
		// String expectedResult =
		// "<order><client></client>  <price>15.0</price><comments>pack it nicely, please</comments></order>";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please", new Item(
				"any item", 12.99));
		serialization.from(order).include("items").exclude("items.price").serialize();
		assertThat(result(), containsString("\"items\""));
		assertThat(result(), containsString("\"name\":\"any item\""));
		assertThat(result(), not(containsString("12.99")));
	}

	@Test
	public void shouldOptionallyRemoveRoot() {
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please", new Item(
				"any item", 12.99));
		serialization.withoutRoot().from(order).include("items").exclude("items.price").serialize();
		assertThat(result(), containsString("\"items\""));
		assertThat(result(), containsString("\"name\":\"any item\""));
		assertThat(result(), not(containsString("12.99")));
		assertThat(result(), not(containsString("\"order\":")));
	}

	@Test
	public void shouldOptionallyRemoveRootIdented() {
		String expected = "{\n  \"price\": 15.0,\n  \"comments\": \"pack it nicely, please\",\n  \"items\": [\n    {\n      \"name\": \"any item\"\n    }\n  ]\n}";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please", new Item(
				"any item", 12.99));
		serialization.indented().withoutRoot().from(order).include("items").exclude("items.price")
				.serialize();
		assertThat(result(), equalTo(expected));
	}

	private String result() {
		return new String(stream.toByteArray());
	}

	public static class SomeProxy extends Client implements HibernateProxy {
		private static final long serialVersionUID = 1L;

		private String aField;

		private transient LazyInitializer initializer;

		public SomeProxy(LazyInitializer initializer) {
			super("name");
			this.initializer = initializer;
		}

		public LazyInitializer getHibernateLazyInitializer() {
			return initializer;
		}

		public String getaField() {
			return aField;
		}

		public Object writeReplace() {
			return this;
		}

	}

	@Test
	public void shouldRunHibernateLazyInitialization() throws Exception {
		LazyInitializer initializer = mock(LazyInitializer.class);

		SomeProxy proxy = new SomeProxy(initializer);
		proxy.name = "my name";
		proxy.aField = "abc";

		when(initializer.getPersistentClass()).thenReturn(Client.class);
		when(proxy.getHibernateLazyInitializer().getImplementation()).thenReturn(proxy);

		serialization.from(proxy).serialize();

		assertThat(result(), is("{\"client\":{\"aField\":\"abc\",\"name\":\"my name\"}}"));
	}

	static class MyCollection extends ForwardingCollection<Order> {
		@Override
		protected Collection<Order> delegate() {
			return Arrays.asList(new Order(new Client("client"), 12.22, "hoay"));
		}

	}

	static class CollectionSerializer implements JsonSerializer<MyCollection> {
		public JsonElement serialize(MyCollection myColl, java.lang.reflect.Type typeOfSrc,
				JsonSerializationContext context) {
			return new JsonParser().parse("[testing]").getAsJsonArray();
		}
	}

	@Test
	public void shouldUseCollectionConverterWhenItExists() {
		String expectedResult = "[\"testing\"]";

		List<JsonSerializer<?>> adapters = new ArrayList<JsonSerializer<?>>();
		adapters.add(new CollectionSerializer());

		GsonJSONSerialization serialization = new GsonJSONSerialization(response, extractor, initializer,
				adapters, Collections.<ExclusionStrategy> emptyList());

		serialization.withoutRoot().from(new MyCollection()).serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldSerializeCalendarLikeXstream() {
		List<JsonSerializer<?>> adapters = new ArrayList<JsonSerializer<?>>();
		adapters.add(new CalendarSerializer());

		GsonJSONSerialization serialization = new GsonJSONSerialization(response, extractor, initializer,
				adapters, Collections.<ExclusionStrategy> emptyList());

		Client c = new Client("renan");
		c.included = new GregorianCalendar(2012, 8, 3);

		serialization.from(c).serialize();
		String result = result();

		String expectedResult = "{\"client\":{\"name\":\"renan\",\"included\":{\"time\":\""
				+ c.included.getTimeInMillis()
				+ "\",\"timezone\":\"" + c.included.getTimeZone().getID() + "\"}}}";

		assertThat(result, is(equalTo(expectedResult)));
	}

	@Test
	public void shouldExcludeAttributeUsingExclusionStrategy() {
		List<ExclusionStrategy> exclusions = new ArrayList<ExclusionStrategy>();
		exclusions.add(new ClientAddressExclusion());

		GsonJSONSerialization serialization = new GsonJSONSerialization(response, extractor, initializer,
				Collections.<JsonSerializer<?>> emptyList(), exclusions);

		serialization.withoutRoot().from(new Client("renan", new Address("rua joao sbarai"))).include("address")
				.serialize();

		assertThat(result(), not(containsString("address")));
	}
	
	@Test
	public void shouldExcludeAllPrimitiveFields() {
		String expectedResult = "{\"order\":{}}";
		Order order = new Order(new Client("nykolas lima"), 15.0, "gift bags, please");
		serialization.from(order).excludeAll().serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}
	
	@Test
	public void shouldExcludeAllPrimitiveParentFields() {
		String expectedResult = "{\"advancedOrder\":{}}";
		Order order = new AdvancedOrder(null, 15.0, "pack it nicely, please", "complex package");
		serialization.from(order).excludeAll().serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}
	
	@Test
	public void shouldExcludeAllThanIncludeAndSerialize() {
		String expectedResult = "{\"order\":{\"price\":15.0}}";
		Order order = new Order(new Client("nykolas lima"), 15.0, "gift bags, please");
		serialization.from(order).excludeAll().include("price").serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

}