package br.com.caelum.vraptor.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldGen;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LoadInstruction;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.resource.ResourceMethod;

public class BcelTypeCreator implements TypeCreator, org.apache.bcel.Constants {

    private static final Logger logger = LoggerFactory.getLogger(BcelTypeCreator.class);

    /*
     * we require the class loading counter in order to work under the same
     * classloader during tests. better than forking per tests (which is
     * sooooooo slow)
     */
    private static int classLoadCounter = 0;

    public Class<?> typeFor(ResourceMethod method) {
        Method reflectionMethod = method.getMethod();

        final String newTypeName = reflectionMethod.getDeclaringClass().getName() + "$" + reflectionMethod.getName()
                + "$" + Math.abs(reflectionMethod.hashCode()) + "$" + (++classLoadCounter);
        logger.debug("Trying to make class for " + newTypeName);
        ClassGen cg = new ClassGen(newTypeName, "java.lang.Object", "<generated>", ACC_PUBLIC | ACC_SUPER, null);
        cg.addEmptyConstructor(ACC_PUBLIC);
        InstructionList il = new InstructionList();

        String valueLists = "";
        for (Class type : reflectionMethod.getParameterTypes()) {
            String fieldName = JavassistTypeCreator.extractName(type);
            String definition = extractTypeDefinition(type);
            Type fieldType = Type.getType(definition);
            ConstantPoolGen cp = cg.getConstantPool();
            FieldGen gen = new FieldGen(ACC_PRIVATE, fieldType, fieldName + "_", cp);
            ConstantPool pooly = cp.getConstantPool();
            pooly.setConstant(0, new ConstantUtf8("Signature"));
            pooly.setConstant(0, new ConstantUtf8("Ljava/util/List<Lbr/com/caelum/vraptor/http/Coco;>;"));
            pooly.setConstant(0, new ConstantUtf8("Signature"));
            pooly.setConstant(0, new ConstantUtf8("Ljava/util/List<Lbr/com/caelum/vraptor/http/Coco;>;"));
            //cp.addUtf8("Ljava/util/List<Lbr/com/caelum/vraptor/http/Coco;>;");
            cg.addField(gen.getField());
            //cg.addMethod(createGetter(fieldType, fieldName, newTypeName, il, cp, cg));
            //cg.addMethod(createSetter(fieldType, fieldName, newTypeName, il, cp, cg, type));
            // ctType.addMethod(CtNewMethod.getter("get" + fieldName, field));
            // ctType.addMethod(CtNewMethod.setter("set" + fieldName, field));
            if (!valueLists.equals("")) {
                valueLists += ",";
            }
            if (type.isPrimitive()) {
                valueLists += JavassistTypeCreator.wrapperCodeFor(type, fieldName + "_");
            } else {
                valueLists += fieldName + "_";
            }
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            JavaClass javaClass = cg.getJavaClass();
            CocoNuts.parse(javaClass);
            javaClass.dump(output);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new IllegalArgumentException("unable to compile class", e);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            throw new IllegalArgumentException("unable to compile class", e);
        }
        final byte[] bytes = output.toByteArray();
        ClassLoader loader = new ClassLoader() {
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                if (name.equals(newTypeName)) {
                    this.defineClass(newTypeName, bytes, 0, bytes.length);
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

    private org.apache.bcel.classfile.Method createGetter(Type fieldType, String fieldName, String className,
            InstructionList il, ConstantPoolGen cp, ClassGen cg) {
        MethodGen mg = new MethodGen(ACC_PUBLIC, fieldType, new Type[] {}, new String[] {}, "get" + fieldName,
                className, il, cp);
        InstructionFactory factory = new InstructionFactory(cg);

        il.append(new ALOAD(0));
        il.append(factory.createGetField(className, fieldName + "_", fieldType));
        il.append(factory.createReturn(fieldType));
        mg.setMaxStack(3);
        org.apache.bcel.classfile.Method method = mg.getMethod();
        il.dispose();
        return method;
    }

    private org.apache.bcel.classfile.Method createSetter(Type fieldType, String fieldName, String className,
            InstructionList il, ConstantPoolGen cp, ClassGen cg, Class vmFieldType) {
        MethodGen mg = new MethodGen(ACC_PUBLIC, Type.VOID, new Type[] { fieldType }, new String[] { fieldName + "_" },
                "set" + fieldName, className, il, cp);
        InstructionFactory factory = new InstructionFactory(cg);
        mg.setMaxLocals(3);
        mg.setMaxStack(3);

        il.append(new ALOAD(0));
        il.append(loadFor(vmFieldType));
        il.append(factory.createPutField(className, fieldName + "_", fieldType));
        il.append(InstructionConstants.RETURN);
        org.apache.bcel.classfile.Method method = mg.getMethod();
        il.dispose();
        return method;
    }

    private LoadInstruction loadFor(Class fieldType) {
        if(fieldType.isPrimitive()) {
            return new org.apache.bcel.generic.ILOAD(1);
        }
        return new ALOAD(1);
    }

    private String extractTypeDefinition(Class type) {
        if (type.isPrimitive()) {
            return Type.getType(type).getSignature();
        }
        return 'L' + type.getName().replace('.', '/') + ';';
    }

}
