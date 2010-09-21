/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.caelum.vraptor.scan;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;

import javax.annotation.Generated;

/**
 * A BoostrapGenerator based on Javassist.
 *
 * @author Sérgio Lopes
 * @since 3.2
 */
public class JavassistBootstrapGenerator implements BootstrapGenerator {

	public void generate(Collection<String> components, ClasspathResolver resolver) {
		// many initial variables
		final String fullName = WebAppBootstrap.STATIC_BOOTSTRAP_NAME;
		final String simpleName = fullName.substring(WebAppBootstrap.STATIC_BOOTSTRAP_NAME.lastIndexOf('.') + 1);
		final String packageName = fullName.substring(0, fullName.lastIndexOf('.'));
		final String webInfClasses = resolver.findWebInfClassesLocation().getPath();
		final String path = webInfClasses + "/" + packageName.replace('.', '/') + "/";
		final String filename = path + simpleName + ".class";

		// create the entire package path
		new File(path).mkdirs();

		// construct the method implementation
		StringBuilder methodDef = new StringBuilder()
			.append("public void configure (br.com.caelum.vraptor.ComponentRegistry registry){");

		for (String componentName : components) {
			methodDef.append("registry.deepRegister(")
					 .append(componentName).append(".class")
					 .append(");");
		}

		methodDef.append("}");

		// generate class file
		try {
			// new class
			ClassPool pool = ClassPool.getDefault();
			CtClass clazz = pool.makeClass(fullName);

			// add a default constructor
			CtConstructor constructor = new CtConstructor(null, clazz);
			constructor.setBody("{super();}");
			clazz.addConstructor(constructor);

			// add the method implementation
			CtMethod m = CtNewMethod.make(methodDef.toString(), clazz);
			clazz.addMethod(m);

			// make this class implements WebAppBootstrap
			ClassFile cf = clazz.getClassFile();
			cf.setVersionToJava5();
			cf.setInterfaces(new String[]{WebAppBootstrap.class.getName()});

			// add @Generated
			ConstPool cp = cf.getConstPool();
			AnnotationsAttribute attr = new AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag);
			attr.setAnnotation(new Annotation(Generated.class.getName(), cp));
			cf.addAttribute(attr);

			// write the file
			cf.write(new DataOutputStream(new FileOutputStream(filename)));
		} catch (Exception e) {
			throw new ScannerException("Error while generating the class file", e);
		}
	}
}
