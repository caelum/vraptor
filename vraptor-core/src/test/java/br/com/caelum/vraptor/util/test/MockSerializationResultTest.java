package br.com.caelum.vraptor.util.test;

import static br.com.caelum.vraptor.view.Results.json;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MockSerializationResultTest {


	private MockSerializationResult result;

	@Before
	public void setUp() throws Exception {
		result = new MockSerializationResult();
	}
	
	public static class Car {
		String licensePlate;
		String owner;
		String make;
		String model;

		public Car(String licensePlate,	String owner, String make,String model) {
			this.licensePlate = licensePlate;
			this.owner = owner;
			this.make = make;
			this.model = model;
		}
	}
	
	@Test 
	public void shouldReturnStringWithObjectSerialized() throws Exception {
		Car car = new Car("XXU-5569", "Caelum", "VW", "Polo");
		String expectedResult = "{\"car\": {\"licensePlate\": \"XXU-5569\",\"owner\": \"Caelum\",\"make\": \"VW\",\"model\": \"Polo\"}}";
		result.use(json()).from(car).serialize();
		Assert.assertThat(result.serializedResult(), is(equalTo(expectedResult)));
	}
}
