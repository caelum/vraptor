/***
 *
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.http.asm;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.asm.ClassWriter;
import br.com.caelum.vraptor.asm.FieldVisitor;
import br.com.caelum.vraptor.asm.MethodVisitor;
import br.com.caelum.vraptor.asm.Opcodes;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.TypeCreator;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.vraptor2.Info;

@ApplicationScoped
public class AsmBasedTypeCreator implements TypeCreator, Opcodes {

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
		this.provider = provider;
	}

	public Class<?> typeFor(ResourceMethod resourceMethod) {
		Method method = resourceMethod.getMethod();

		final String newTypeName = method.getDeclaringClass().getSimpleName().replace('.', '/') + "$"
				+ method.getName() + "$" + Math.abs(method.hashCode()) + "$" + (++classLoadCounter);
		logger.debug("Trying to make class for " + newTypeName);

		ClassWriter cw = new ClassWriter(0);

		cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, newTypeName, null, "java/lang/Object", null);

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
			} else if (type instanceof Class) {
				parse(cw, (Class<?>) type, valueLists, newTypeName, names[i]);
			} else {
				throw new IllegalArgumentException("Unable to identify field " + type + " of type "
						+ type.getClass().getName());
			}

		}
		cw.visitEnd();
		final byte[] bytes = cw.toByteArray();

		ClassLoader loader = new ClassLoader(this.getClass().getClassLoader()) {
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
			mv.visitMaxs(2, 2);
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
		return type.isPrimitive() ? IRETURN : ARETURN;
	}

	private int loadFor(Class<?> type) {
		return type.isPrimitive() ? ILOAD : ALOAD;
	}

}
