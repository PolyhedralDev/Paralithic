package com.dfsek.paralithic.eval.parser;

import com.dfsek.paralithic.DynamicClassLoader;
import com.dfsek.paralithic.Expression;
import com.dfsek.paralithic.function.dynamic.DynamicFunction;
import com.dfsek.paralithic.ops.Operation;
import com.dfsek.paralithic.ops.OperationUtils;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

public class ExpressionBuilder {
    private static int builds = 0;
    private static final boolean DUMP = "true".equals(System.getProperty("ASMDumpClasses"));

    private final Map<String, DynamicFunction> functions;

    public ExpressionBuilder(Map<String, DynamicFunction> functions) {
        this.functions = functions;
    }

    public Expression get(Operation op) {
        op = OperationUtils.simplify(op); // Simplify operation

        String interfaceClassName = Expression.class.getCanonicalName().replace('.', '/');
        String implementationClassName = interfaceClassName + "IMPL_" + builds;

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);

        functions.forEach((id, function) -> writer.visitField(ACC_PUBLIC, id, "L" + DynamicFunction.class.getCanonicalName().replace('.', '/') + ";", null, null));

        writer.visit(V1_8,
                ACC_PUBLIC,
                implementationClassName,
                null,
                "java/lang/Object",
                new String[]{interfaceClassName});
        builds++;

        MethodVisitor constructor = writer.visitMethod(ACC_PUBLIC,
                "<init>", // Constructor method name is <init>
                "()V",
                null,
                null);

        constructor.visitCode();
        constructor.visitVarInsn(ALOAD, 0); // Put this reference on stack
        constructor.visitMethodInsn(INVOKESPECIAL, // Invoke Object super constructor
                "java/lang/Object",
                "<init>",
                "()V",
                false);
        constructor.visitInsn(RETURN); // Void return
        constructor.visitMaxs(0, 0); // Set stack and local variable size (zero because it is handled automatically by ASM)

        MethodVisitor absMethod = writer.visitMethod(ACC_PUBLIC,
                "evaluate", // Method name
                "([D)D", // Method descriptor (no args, return double)
                null,
                null);
        absMethod.visitCode();

        op.apply(absMethod, implementationClassName); // Apply operation to method.

        absMethod.visitInsn(DRETURN); // Return double at top of stack (operation leaves one double on stack)


        absMethod.visitMaxs(0, 0); // Set stack and local variable size (zero because it is handled automatically by ASM)


        DynamicClassLoader loader = new DynamicClassLoader();

        byte[] bytes = writer.toByteArray();

        if(DUMP) {
            File dump = new File("./dumps/ExpressionIMPL_" + (builds - 1) + ".class");
            dump.getParentFile().mkdirs();
            System.out.println("Dumping to " + dump.getAbsolutePath());
            try {
                IOUtils.write(bytes, new FileOutputStream(dump));
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        Class<?> clazz = loader.defineClass(implementationClassName.replace('/', '.'), writer.toByteArray());

        try {
            Object instance = clazz.newInstance();
            for (Map.Entry<String, DynamicFunction> entry : functions.entrySet()) {
                clazz.getDeclaredField(entry.getKey()).set(instance, entry.getValue()); // Inject fields
            }
            return (Expression) instance;
        } catch(InstantiationException | IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
