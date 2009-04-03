package br.com.caelum.vraptor.http;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.asm.ClassWriter;
import br.com.caelum.vraptor.asm.FieldVisitor;
import br.com.caelum.vraptor.asm.MethodVisitor;
import br.com.caelum.vraptor.asm.Opcodes;
import br.com.caelum.vraptor.http.asm.NameCreator;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class AsmBasedTypeCreator implements TypeCreator, Opcodes {

    private static final Logger logger = LoggerFactory.getLogger(AsmBasedTypeCreator.class);
    
    private static final SignatureConverter CONVERTER = new SignatureConverter();
    private static final NameCreator NAMER = new NameCreator();

    /*
     * we require the class loading counter in order to work under the same
     * classloader during tests. better than forking per tests (which is
     * sooooooo slow)
     */
    private static int classLoadCounter = 0;

    public Class<?> typeFor(ResourceMethod method) {
        Method reflectionMethod = method.getMethod();

        final String newTypeName = reflectionMethod.getDeclaringClass().getSimpleName().replace('.','/') + "$" + reflectionMethod.getName()
                + "$" + Math.abs(reflectionMethod.hashCode()) + "$" + (++classLoadCounter);
        logger.debug("Trying to make class for " + newTypeName);

        ClassWriter cw = new ClassWriter(0);

        cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, newTypeName, null, "java/lang/Object", null);

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
        java.lang.reflect.Type[] types = reflectionMethod.getGenericParameterTypes();
        for (java.lang.reflect.Type type : types) {

            if (type instanceof ParameterizedType) {
                parse(cw, (ParameterizedType) type, valueLists, newTypeName);
            } else if (type instanceof Class) {
                parse(cw, (Class<?>) type, valueLists, newTypeName);
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
            System.out.println(Arrays.toString(found.getDeclaredMethods()));
            System.out.println(Arrays.toString(found.getDeclaredFields()));
            return found;
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            throw new IllegalArgumentException("unable to compile class", e);
        }
    }

    private void parse(ClassWriter cw, ParameterizedType type, StringBuilder valueLists, String newTypeName) {
        String fieldName = NAMER.extractName(type);
        String definition = CONVERTER.extractTypeDefinition((Class<?>) type.getRawType());
        String genericDefinition = CONVERTER.extractTypeDefinition(type);
        parse(cw,valueLists,newTypeName, definition, genericDefinition, fieldName, ALOAD, ARETURN);
        if (valueLists.length() != 0) {
            valueLists.append(',');
        }
        valueLists.append(fieldName + "_");
    }

    private void parse(ClassWriter cw, StringBuilder valueLists, String newTypeName, String definition,
            String genericDefinition, String fieldName, int loadKey, int returnKey) {
        System.out.println("Method definition " + definition);

        {
            FieldVisitor fv = cw.visitField(ACC_PRIVATE, fieldName + "_", definition, genericDefinition, null);
            fv.visitEnd();
        }
        {
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "set" + fieldName, "(" + definition + ")V",
                    genericDefinition==null?null: "("+genericDefinition+")V", null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(loadKey, 1);
            mv.visitFieldInsn(PUTFIELD, newTypeName, fieldName + "_", definition);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            MethodVisitor mv = cw
                    .visitMethod(ACC_PUBLIC, "get" + fieldName, "()" + definition, genericDefinition==null?null: "()"+genericDefinition, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, newTypeName, fieldName + "_", definition);
            mv.visitInsn(returnKey);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
    }

    private void parse(ClassWriter cw, Class type, StringBuilder valueLists, String newTypeName) {
        String fieldName = NAMER.extractName(type);
        String definition = CONVERTER.extractTypeDefinition(type);
        String genericDefinition = null;
        parse(cw,valueLists,newTypeName,definition, genericDefinition, fieldName, loadFor(type), returnFor(type));
        
        if (valueLists.length() != 0) {
            valueLists.append(',');
        }
        if (type.isPrimitive()) {
            valueLists.append(wrapperCodeFor(type, fieldName + "_"));
        } else {
            valueLists.append(fieldName + "_");
        }
    }
    
    private static final Map<Class<?>, String> wrappers = new HashMap<Class<?>,String>();
    
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
        return type.isPrimitive()? IRETURN : ARETURN;
    }

    private int loadFor(Class<?> type) {
        return type.isPrimitive() ? ILOAD : ALOAD;
    }

    public String nameFor(Type type) {
        if(type instanceof ParameterizedType) {
            return NAMER.extractName((ParameterizedType) type);
        }
        return NAMER.extractName((Class) type);
    }

}
