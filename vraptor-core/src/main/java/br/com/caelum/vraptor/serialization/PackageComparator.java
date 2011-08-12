package br.com.caelum.vraptor.serialization;

import java.util.Comparator;

final class PackageComparator implements Comparator<Serialization> {
	private int number(Serialization s) {
		String packageName = s.getClass().getPackage().getName();
		if (packageName.startsWith("br.com.caelum.vraptor.serialization")
		 || packageName.startsWith("br.com.caelum.vraptor.restfulie.serialization")) {
			return 1;
		}
		return 0;
	}
	
	private int giveMorePriorityToRestfulie(Serialization o1, Serialization o2) {
		String packageNameO1 = o1.getClass().getPackage().getName();
		String packageNameO2 = o2.getClass().getPackage().getName();
		
		if(packageNameO1.startsWith("br.com.caelum.vraptor.serialization") 
		&& packageNameO2.startsWith("br.com.caelum.vraptor.restfulie.serialization")) {
			return 1;
		}
		
		return 0;
	}

	public int compare(Serialization o1, Serialization o2) {
		int numberO1 = number(o1);
		int numberO2 = number(o2);
		int compareResult = numberO1 - numberO2;
		
		if(compareResult == 0) {
			return giveMorePriorityToRestfulie(o1, o2);
		}

		return compareResult;
	}
}