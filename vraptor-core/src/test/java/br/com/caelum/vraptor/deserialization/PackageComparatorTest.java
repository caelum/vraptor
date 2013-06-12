package br.com.caelum.vraptor.deserialization;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import br.com.caelum.vraptor.deserialization.gson.GsonDeserialization;
import br.com.caelum.vraptor.other.pack4ge.DumbDeserialization;

public class PackageComparatorTest {
	
	@Test
	public void shouldSortBasedOnPackageNamesMorePriorityToCaelumInitialList3rdPartyFirst() {
		List<Class<? extends Deserializer>> deserializers = new ArrayList<Class<? extends Deserializer>>();
		
		deserializers.add(DumbDeserialization.class);
		deserializers.add(GsonDeserialization.class);
		deserializers.add(JsonDeserializer.class);
		
		Collections.sort(deserializers, new PackageComparator());
		
		assertTrue(deserializers.get(0).getPackage().getName().startsWith("br.com.caelum.vraptor.deserialization"));
		assertTrue(deserializers.get(2).getPackage().getName().equals("br.com.caelum.vraptor.other.pack4ge"));
	}
	
	@Test
	public void shouldSortBasedOnPackageNamesMorePriorityToCaelumInitialList3rdPartyLast() {
		List<Class<? extends Deserializer>> deserializers = new ArrayList<Class<? extends Deserializer>>();
		
		deserializers.add(GsonDeserialization.class);
		deserializers.add(JsonDeserializer.class);
		deserializers.add(DumbDeserialization.class);
		
		Collections.sort(deserializers, new PackageComparator());
		
		assertTrue(deserializers.get(0).getPackage().getName().startsWith("br.com.caelum.vraptor.deserialization"));
		assertTrue(deserializers.get(2).getPackage().getName().equals("br.com.caelum.vraptor.other.pack4ge"));
	}
	
}
