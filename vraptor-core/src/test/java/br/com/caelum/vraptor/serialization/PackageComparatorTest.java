package br.com.caelum.vraptor.serialization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.caelum.vraptor.other.pack4ge.DumbSerialization;
import junit.framework.Assert;

import org.junit.Test;

import br.com.caelum.vraptor.restfulie.serialization.RestfulSerialization;
import br.com.caelum.vraptor.serialization.xstream.XStreamJSONSerialization;
import br.com.caelum.vraptor.serialization.xstream.XStreamXMLSerialization;

public class PackageComparatorTest {

	@Test
	public void shouldSortBasedOnPackageNamesLessPriorityToCaelumInitialList3rdPartyFirst() {
		List<Serialization> serializers = new ArrayList<Serialization>();

		serializers.add(new DumbSerialization());
		serializers.add(new XStreamXMLSerialization(null, null, null, null));
		serializers.add(new XStreamJSONSerialization(null, null, null, null));
		serializers.add(new HTMLSerialization(null, null));
		serializers.add(new RestfulSerialization(null, null, null, null, null, null));

		Collections.sort(serializers, new PackageComparator());
		
		Assert.assertEquals("br.com.caelum.vraptor.other.pack4ge", serializers.get(0).getClass().getPackage().getName());

	}

	@Test
	public void shouldSortBasedOnPackageNamesLessPriorityToCaelumInitialList3rdPartyLast() {
		List<Serialization> serializers = new ArrayList<Serialization>();
		
		serializers.add(new XStreamXMLSerialization(null, null, null, null));
		serializers.add(new XStreamJSONSerialization(null, null, null, null));
		serializers.add(new HTMLSerialization(null, null));
		serializers.add(new RestfulSerialization(null, null, null, null, null, null));
		serializers.add(new DumbSerialization());
		
		Collections.sort(serializers, new PackageComparator());
		
		Assert.assertEquals("br.com.caelum.vraptor.other.pack4ge", serializers.get(0).getClass().getPackage().getName());
	}

	@Test
	public void shouldSortBasedOnPackageNamesLessPriorityToCaelumMoreToRestfulieInitialList3rdPartyLast() {
		List<Serialization> serializers = new ArrayList<Serialization>();
		
		serializers.add(new XStreamXMLSerialization(null, null, null, null));
		serializers.add(new XStreamJSONSerialization(null, null, null, null));
		serializers.add(new HTMLSerialization(null, null));
		serializers.add(new RestfulSerialization(null, null, null, null, null, null));
		serializers.add(new DumbSerialization());
		
		Collections.sort(serializers, new PackageComparator());
		
		Assert.assertEquals("br.com.caelum.vraptor.other.pack4ge", serializers.get(0).getClass().getPackage().getName());
		Assert.assertEquals("br.com.caelum.vraptor.restfulie.serialization", serializers.get(1).getClass().getPackage().getName());
	}
}