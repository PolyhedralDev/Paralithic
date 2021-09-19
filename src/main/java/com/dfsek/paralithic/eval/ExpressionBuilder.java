package com.dfsek.paralithic.eval;

import com.dfsek.paralithic.DynamicClassLoader;
import com.dfsek.paralithic.Expression;
import com.dfsek.paralithic.functions.dynamic.Context;
import com.dfsek.paralithic.functions.dynamic.DynamicFunction;
import com.dfsek.paralithic.node.Node;
import com.dfsek.paralithic.node.OperationUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

public class ExpressionBuilder {
    private static long builds = 0;
    private static final boolean DUMP = "true".equals(System.getProperty("paralithic.debug.dump"));
    public static final String EXPRESSION_CLASS_NAME = dynamicName(Expression.class);
    public static final String DYNAMIC_FUNCTION_CLASS_NAME = dynamicName(DynamicFunction.class);
    public static final String CONTEXT_CLASS_NAME = dynamicName(Context.class);
    public static final String OBJECT_CLASS_NAME = dynamicName(Object.class);

    private final Map<String, DynamicFunction> functions;

    public ExpressionBuilder(Map<String, DynamicFunction> functions) {
        this.functions = functions;
    }

    public Expression get(Node op) {
        String implementationClassName = EXPRESSION_CLASS_NAME + "IMPL_" + builds;

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);

        functions.forEach((id, function) -> writer.visitField(ACC_PUBLIC, id, "L" + DYNAMIC_FUNCTION_CLASS_NAME + ";", null, null));

        writer.visit(V1_8,
                ACC_PUBLIC,
                implementationClassName,
                null,
                OBJECT_CLASS_NAME,
                new String[]{EXPRESSION_CLASS_NAME});

        MethodVisitor constructor = writer.visitMethod(ACC_PUBLIC,
                "<init>", // Constructor method name is <init>
                "()V",
                null,
                null);

        constructor.visitCode();
        constructor.visitVarInsn(ALOAD, 0); // Put this reference on stack
        constructor.visitMethodInsn(INVOKESPECIAL, // Invoke Object super constructor
                OBJECT_CLASS_NAME,
                "<init>",
                "()V",
                false);
        constructor.visitInsn(RETURN); // Void return
        constructor.visitMaxs(0, 0); // Set stack and local variable size (zero because it is handled automatically by ASM)

        MethodVisitor absMethod = writer.visitMethod(ACC_PUBLIC,
                "evaluate", // Method name
                "(L" + CONTEXT_CLASS_NAME + ";[D)D", // Method descriptor (context & double array args, return double)
                null,
                null);
        absMethod.visitCode();

        OperationUtils.simplify(op).apply(absMethod, implementationClassName); // Apply operation to method.

        absMethod.visitInsn(DRETURN); // Return double at top of stack (operations leaves one double on stack)

        absMethod.visitMaxs(0, 0); // Set stack and local variable size (zero because it is handled automatically by ASM)

        DynamicClassLoader loader = new DynamicClassLoader(); // Instantiate a new loader every time so classes can be GC'ed when they are no longer used. (Classes cannot be GC'ed until their loaders are).

        byte[] bytes = writer.toByteArray();

        builds++;
        Class<?> clazz = loader.defineClass(implementationClassName.replace('/', '.'), writer.toByteArray());

        if(DUMP) {
            File dump = new File("./.paralithic/out/classes/ExpressionIMPL_" + builds  + ".class");
            dump.getParentFile().mkdirs();
            System.out.println("Dumping class " + clazz.getCanonicalName() + "to " + dump.getAbsolutePath());
            try(FileOutputStream out = new FileOutputStream(dump)) {
                out.write(bytes);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            for (Map.Entry<String, DynamicFunction> entry : functions.entrySet()) {
                clazz.getDeclaredField(entry.getKey()).set(instance, entry.getValue()); // Inject fields
            }
            return (Expression) instance;
        } catch(ReflectiveOperationException e) {
            throw new Error(e); // Should literally never happen
        }
    }

    /**
     * Dynamically get name to account for possibility of shading
     * @param clazz Class instance
     * @return Internal class name
     */
    public static String dynamicName(Class<?> clazz) {
        return clazz.getCanonicalName().replace('.', '/');
    }
}
