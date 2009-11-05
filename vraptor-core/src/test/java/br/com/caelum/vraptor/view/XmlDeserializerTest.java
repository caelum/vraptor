package br.com.caelum.vraptor.view;

import java.io.ByteArrayOutputStream;

import org.junit.Before;

public class XmlDeserializerTest {

	private XmlSerializer serializer;
	private ByteArrayOutputStream stream;

	@Before
    public void setup() {
        this.stream = new ByteArrayOutputStream();
        this.serializer = new XmlSerializer(stream);
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

}
