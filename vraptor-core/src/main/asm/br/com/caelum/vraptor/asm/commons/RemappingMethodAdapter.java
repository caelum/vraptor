/***
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2007 INRIA, France Telecom
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

package br.com.caelum.vraptor.asm.commons;

import br.com.caelum.vraptor.asm.AnnotationVisitor;
import br.com.caelum.vraptor.asm.Label;
import br.com.caelum.vraptor.asm.MethodVisitor;

/**
 * A <code>MethodAdapter</code> for type mapping.
 * 
 * @author Eugene Kuleshov
 */
public class RemappingMethodAdapter extends LocalVariablesSorter {

    protected final Remapper remapper;

    public RemappingMethodAdapter(final int access, final String desc, final MethodVisitor mv, final Remapper renamer) {
        super(access, desc, mv);
        remapper = renamer;
    }

    public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
        super.visitFieldInsn(opcode, remapper.mapType(owner), remapper.mapFieldName(owner, name, desc),
                remapper.mapDesc(desc));
    }

    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc) {
        super.visitMethodInsn(opcode, remapper.mapType(owner), remapper.mapMethodName(owner, name, desc),
                remapper.mapMethodDesc(desc));
    }

    public void visitTypeInsn(final int opcode, final String type) {
        super.visitTypeInsn(opcode, remapper.mapType(type));
    }

    public void visitLdcInsn(final Object cst) {
        super.visitLdcInsn(remapper.mapValue(cst));
    }

    public void visitMultiANewArrayInsn(final String desc, final int dims) {
        super.visitMultiANewArrayInsn(remapper.mapDesc(desc), dims);
    }

    public void visitTryCatchBlock(final Label start, final Label end, final Label handler, final String type) {
        super.visitTryCatchBlock(start, end, handler, // 
                type == null ? null : remapper.mapType(type));
    }

    @Override
    public void visitLocalVariable(final String name, final String desc, final String signature, final Label start,
            final Label end, final int index) {
        super.visitLocalVariable(name, remapper.mapDesc(desc), remapper.mapSignature(signature, true), start, end,
                index);
    }

    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        AnnotationVisitor av = mv.visitAnnotation(desc, visible);
        return av == null ? av : new RemappingAnnotationAdapter(av, remapper);
    }

    public AnnotationVisitor visitAnnotationDefault() {
        AnnotationVisitor av = mv.visitAnnotationDefault();
        return av == null ? av : new RemappingAnnotationAdapter(av, remapper);
    }

    public AnnotationVisitor visitParameterAnnotation(final int parameter, final String desc, final boolean visible) {
        AnnotationVisitor av = mv.visitParameterAnnotation(parameter, desc, visible);
        return av == null ? av : new RemappingAnnotationAdapter(av, remapper);
    }

    @Override
    public void visitFrame(final int type, final int nLocal, final Object[] local, final int nStack,
            final Object[] stack) {
        super.visitFrame(type, nLocal, remapEntries(nLocal, local), nStack, remapEntries(nStack, stack));
    }

    private Object[] remapEntries(final int n, final Object[] entries) {
        for (int i = 0; i < n; i++) {
            if (entries[i] instanceof String) {
                Object[] newEntries = new Object[n];
                if (i > 0) {
                    System.arraycopy(entries, 0, newEntries, 0, i);
                }
                do {
                    Object t = entries[i];
                    newEntries[i++] = t instanceof String ? remapper.mapType((String) t) : t;
                } while (i < n);
                return newEntries;
            }
        }
        return entries;
    }

}
