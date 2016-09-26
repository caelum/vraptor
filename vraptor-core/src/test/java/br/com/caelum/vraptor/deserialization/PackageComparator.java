package br.com.caelum.vraptor.deserialization;

import java.util.Comparator;

public class PackageComparator implements Comparator<Class<? extends Deserializer>> {

	public int compare(Class<? extends Deserializer> o1, Class<? extends Deserializer> o2) {
		String o1PackageName = o1.getPackage().getName();
		String o2PackageName = o2.getPackage().getName();
		if (o1PackageName.startsWith("br.com.caelum.vraptor.deserialization") && !o2PackageName.startsWith("br.com.caelum.vraptor.deserialization")) {
			return -1;
		} else if (!o1PackageName.startsWith("br.com.caelum.vraptor.deserialization") && o2PackageName.startsWith("br.com.caelum.vraptor.deserialization")) {
			return 1;
		} else {
			return 0;
		}
	}

}
