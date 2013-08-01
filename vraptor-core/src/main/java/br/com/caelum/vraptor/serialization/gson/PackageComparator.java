package br.com.caelum.vraptor.serialization.gson;

import java.util.Comparator;

public final class PackageComparator implements Comparator<Object> {

	private static final String CORE_ADAPTER = "br.com.caelum.vraptor.serialization.gson.adapters";
	private static final String ISO8601_ADAPTER = "br.com.caelum.vraptor.serialization.iso8601.gson";

	public int compare(Object o1, Object o2) {
		return giveMorePriorityToISO8601Adapters(o1, o2);
	}
	
	private int giveMorePriorityToISO8601Adapters(Object o1, Object o2) {
		String packageNameO1 = o1.getClass().getPackage().getName();
		String packageNameO2 = o2.getClass().getPackage().getName();
		
		if (packageNameO1.startsWith(CORE_ADAPTER) && packageNameO2.startsWith(ISO8601_ADAPTER)) 
			return -1;

		if (packageNameO1.startsWith(ISO8601_ADAPTER) && packageNameO2.startsWith(CORE_ADAPTER)) 
			return 1;
		
		return packageNameO1.compareTo(packageNameO2);
	}
}
