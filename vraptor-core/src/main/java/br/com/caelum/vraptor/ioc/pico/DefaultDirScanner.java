package br.com.caelum.vraptor.ioc.pico;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.resource.DefaultResource;
import br.com.caelum.vraptor.resource.Resource;

public class DefaultDirScanner implements DirScanner {

	public List<Resource> scan(File dir) {
		List<Resource> resources = new ArrayList<Resource>();
		search(dir, "", resources);
		return resources;
	}

	private void search(File baseDir, String currentPackage,
			List<Resource> resources) {
		if (baseDir.listFiles() == null) {
			return;
		}
		if (!currentPackage.equals("")) {
			currentPackage += ".";
		}
		for (File child : baseDir.listFiles()) {
			if (child.isHidden()) {
				continue;
			}
			if (child.isDirectory()) {
				search(child, currentPackage + child.getName(), resources);
			} else if (child.isFile() && child.getName().endsWith(".class")) {
				String typeName = currentPackage + child.getName();
				typeName = typeName.substring(0, typeName.length()-6);
				try {
					Class<?> type = Class.forName(typeName, false, this.getClass()
							.getClassLoader());
					if (type.isAnnotationPresent(br.com.caelum.vraptor.Resource.class)) {
						resources.add(new DefaultResource(type));
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
