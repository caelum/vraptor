package br.com.caelum.vraptor.serialization.xstream;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.interceptor.DefaultTypeNameExtractor;
import br.com.caelum.vraptor.serialization.HibernateProxyInitializer;

public class XStreamJSONPSerializationTest {


	private XStreamJSONPSerialization serialization;
	private ByteArrayOutputStream stream;

	@Before
    public void setup() throws Exception {
        this.stream = new ByteArrayOutputStream();

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(stream));

        this.serialization = new XStreamJSONPSerialization(response, new DefaultTypeNameExtractor(), new HibernateProxyInitializer());
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

	@Test
	public void shouldIncludeCallbackPadding() {
		String expectedResult = "myCallback({\"order\": {\n  \"price\": 15.0,\n  \"comments\": \"pack it nicely, please\"\n}})";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serialization.withCallback("myCallback").from(order).serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	private String result() {
		return new String(stream.toByteArray());
	}


}
