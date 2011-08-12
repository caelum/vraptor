package br.com.caelum.vraptor.serialization.xstream;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;

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
        
        this.serialization = new XStreamJSONPSerialization(response, new DefaultTypeNameExtractor(), new HibernateProxyInitializer(), XStreamBuilderImpl.cleanInstance());
    }

	public static class Address {
		String street;
		public Address(String street) {
			this.street = street;
		}
	}

	public static class Order {
		double price;
		String comments;

		public Order(double price, String comments) {
			this.price = price;
			this.comments = comments;
		}
		public String nice() {
			return "nice output";
		}

	}

	@Test
	public void shouldIncludeCallbackPadding() {
		String expectedResult = "myCallback({\"order\": {\"price\": 15.0,\"comments\": \"pack it nicely, please\"}})";
		Order order = new Order(15.0, "pack it nicely, please");
		serialization.withCallback("myCallback").from(order).serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	private String result() {
		return new String(stream.toByteArray());
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
		String expectedResult = "myCallback({\"genericWrapper\": {\"entityList\": [{\"street\": \"vergueiro street\"},{\"street\": \"vergueiro street\"}],\"total\": 2}})";

		Collection<Address> entityList = new ArrayList<Address>();
		entityList.add(new Address("vergueiro street"));
		entityList.add(new Address("vergueiro street"));

		GenericWrapper<Address> wrapper = new GenericWrapper<Address>(entityList, entityList.size());

        serialization.withCallback("myCallback").from(wrapper).include("entityList").serialize();

        assertThat(result(), is(equalTo(expectedResult)));
    }

}
