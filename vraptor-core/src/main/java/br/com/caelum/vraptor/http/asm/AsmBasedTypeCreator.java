/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.http.asm;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.asm.ClassWriter;
import br.com.caelum.vraptor.asm.FieldVisitor;
import br.com.caelum.vraptor.asm.MethodVisitor;
import br.com.caelum.vraptor.asm.Opcodes;
import br.com.caelum.vraptor.http.AbstractTypeCreator;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.vraptor2.Info;

@ApplicationScoped
public class AsmBasedTypeCreator extends AbstractTypeCreator implements Opcodes {

	private static final Logger logger = LoggerFactory.getLogger(AsmBasedTypeCreator.class);

	private static final SignatureConverter CONVERTER = new SignatureConverter();

	/*
	 * we require the class loading counter in order to work under the same
	 * classloader during tests. better than forking per tests (which is
	 * sooooooo slow)
	 */
	private static int classLoadCounter = 0;

	private final ParameterNameProvider provider;

	public AsmBasedTypeCreator(ParameterNameProvider provider) {
		super(provider);
		this.provider = provider;
	}

	public Class<?> typeFor(ResourceMethod resourceMethod) {
		Method method = resourceMethod.getMethod();

		final String newTypeName = method.getDeclaringClass().getSimpleName().replace('.', '/') + "$"
				+ method.getName() + "$" + Math.abs(method.hashCode()) + "$" + (++classLoadCounter);
		logger.debug("Trying to make class for " + newTypeName);

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

		cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, newTypeName, null, "java/lang/Object", new String[] {"java/io/Serializable"});

		{
			MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
			mv.visitInsn(RETURN);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		StringBuilder valueLists = new StringBuilder();
		java.lang.reflect.Type[] types = method.getGenericParameterTypes();
		String[] names = provider.parameterNamesFor(method);
		for (int i = 0; i < names.length; i++) {
			names[i] = Info.capitalize(names[i]);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Parameter names found for creating type are: " + Arrays.toString(names));
		}
		for (int i = 0; i < types.length; i++) {
			java.lang.reflect.Type type = types[i];
			if (type instanceof ParameterizedType) {
				parse(cw, (ParameterizedType) type, valueLists, newTypeName, names[i]);
			} else if (type instanceof Class<?>) {
				parse(cw, (Class<?>) type, valueLists, newTypeName, names[i]);
			} else if (type instanceof TypeVariable<?>) {
				ParameterizedType superclass = (ParameterizedType) resourceMethod.getResource().getType().getGenericSuperclass();
				parse(cw, (Class<?>) superclass.getActualTypeArguments()[0], valueLists, newTypeName, names[i]);
			} else {
				throw new IllegalArgumentException("Unable to identify field " + type + " of type "
						+ type.getClass().getName());
			}

		}
		cw.visitEnd();
		final byte[] bytes = cw.toByteArray();

		ClassLoader loader = new ClassLoader(this.getClass().getClassLoader()) {
			@Override
			public Class<?> loadClass(String name) throws ClassNotFoundException {
				if (name.equals(newTypeName)) {
					return this.defineClass(newTypeName, bytes, 0, bytes.length);
				}
				return super.loadClass(name);
			}
		};
		try {
			Class<?> found = loader.loadClass(newTypeName);
			if (logger.isDebugEnabled()) {
				logger.debug("Methods: " + Arrays.toString(found.getDeclaredMethods()));
				logger.debug("Fields: " + Arrays.toString(found.getDeclaredFields()));
			}
			return found;
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("unable to create type to inject values", e);
		}
	}

	private void parse(ClassWriter cw, ParameterizedType type, StringBuilder valueLists, String newTypeName,
			String fieldName) {
		String definition = CONVERTER.extractTypeDefinition((Class<?>) type.getRawType());
		String genericDefinition = CONVERTER.extractTypeDefinition(type);
		parse(cw, newTypeName, definition, genericDefinition, fieldName, ALOAD, ARETURN);
		if (valueLists.length() != 0) {
			valueLists.append(',');
		}
        valueLists.append(fieldName).append("_");
	}

	private void parse(ClassWriter cw, String newTypeName, String definition,
			String genericDefinition, String fieldName, int loadKey, int returnKey) {
		if (logger.isDebugEnabled()) {
			logger.debug("Method for field '" + fieldName + "' being defined for type " + definition);
		}

		{
			FieldVisitor fv = cw.visitField(ACC_PRIVATE, fieldName + "_", definition, genericDefinition, null);
			fv.visitEnd();
		}
		{
			MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "set" + fieldName, "(" + definition + ")V",
					genericDefinition == null ? null : "(" + genericDefinition + ")V", null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(loadKey, 1);
			mv.visitFieldInsn(PUTFIELD, newTypeName, fieldName + "_", definition);
			mv.visitInsn(RETURN);
			mv.visitMaxs(3, 3);
			mv.visitEnd();
		}
		{
			MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "get" + fieldName, "()" + definition,
					genericDefinition == null ? null : "()" + genericDefinition, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, newTypeName, fieldName + "_", definition);
			mv.visitInsn(returnKey);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
	}

	private void parse(ClassWriter cw, Class<?> type, StringBuilder valueLists, String newTypeName, String fieldName) {
		String definition = CONVERTER.extractTypeDefinition(type);
		String genericDefinition = null;
		parse(cw, newTypeName, definition, genericDefinition, fieldName, loadFor(type), returnFor(type));

		if (valueLists.length() != 0) {
			valueLists.append(',');
		}
		if (type.isPrimitive()) {
			valueLists.append(wrapperCodeFor(type, fieldName + "_"));
		} else {
            valueLists.append(fieldName).append("_");
		}
	}

	private static final Map<Class<?>, String> wrappers = new HashMap<Class<?>, String>();

	static {
		wrappers.put(int.class, "Integer.valueOf(");
		wrappers.put(boolean.class, "Boolean.valueOf(");
		wrappers.put(short.class, "Short.valueOf(");
		wrappers.put(long.class, "Long.valueOf(");
		wrappers.put(double.class, "Double.valueOf(");
		wrappers.put(float.class, "Float.valueOf(");
		wrappers.put(char.class, "Character.valueOf(");
	}

	private String wrapperCodeFor(Class<?> type, String fieldName) {
		return wrappers.get(type) + fieldName + ")";
	}

	private int returnFor(Class<?> type) {
		if (type.equals(double.class)) {
			return DRETURN;
		}
		if (type.equals(long.class)) {
			return LRETURN;
		}
		if (type.equals(float.class)) {
			return FRETURN;
		}
		return type.isPrimitive() ? IRETURN : ARETURN;
	}

	private int loadFor(Class<?> type) {
		if (type.equals(double.class)) {
			return DLOAD;
		}
		if (type.equals(float.class)) {
			return FLOAD;
		}
		if (type.equals(long.class)) {
			return LLOAD;
		}
		return type.isPrimitive() ? ILOAD : ALOAD;
	}
}
