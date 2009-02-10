/*
 * Copyright (C) 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.plexus.metadata.ann;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * @author Eugene Kuleshov
 */
public class AnnReader implements ClassVisitor {

  private final AnnClass annClass;

  private AnnReader(AnnClass annClass) {
    this.annClass = annClass;
  }

  public static AnnClass read(InputStream is, ClassLoader cl) throws IOException {
    AnnClass annClass = new AnnClass(cl);
    AnnReader cv = new AnnReader(annClass);
    ClassReader r = new ClassReader(is);
    r.accept(cv, ClassReader.SKIP_FRAMES | ClassReader.SKIP_CODE);
    return annClass;
  }

  public void visit(int version, int access, String name, String signature,
      String superName, String[] interfaces) {
    annClass.setName(name);
    annClass.setAccess(access);
    annClass.setSuperName(superName);
    annClass.setInterfaces(interfaces);
  }
  
  public AnnotationVisitor visitAnnotation(final String desc, boolean visible) {
    Ann ann = new Ann(desc);
    annClass.addAnn(ann);
    return new AnnAnnReader(ann);
  }
  
  public FieldVisitor visitField(int access, final String name, final String desc, String signature, Object value) {
    final AnnField field = new AnnField(annClass, access, name, desc);
    annClass.addField(field);
    return new FieldVisitor() {

      public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        Ann ann = new Ann(desc);
        field.addAnn(ann);
        return new AnnAnnReader(ann);
      }

      public void visitAttribute(Attribute attr) {
      }

      public void visitEnd() {
      }
    };
  }

  public MethodVisitor visitMethod(int access, final String mname, final String mdesc,
      String signature, String[] exceptions) {
    final AnnMethod method = new AnnMethod(annClass, access, mname, mdesc);
    annClass.addMethod(method);
    
    return new MethodVisitor() {

      public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        Ann ann = new Ann(desc);
        method.addAnn(ann);
        return new AnnAnnReader(ann);
      }

      public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
        Ann ann = new Ann(desc);
        method.addParamAnn(parameter, ann);
        return new AnnAnnReader(ann);
      }
      
      public AnnotationVisitor visitAnnotationDefault() {
        // TODO
        return null;
      }
      
      public void visitAttribute(Attribute attr) {
      }

      public void visitCode() {
      }

      public void visitFieldInsn(int opcode, String owner, String name, String desc) {
      }

      public void visitFrame(int type, int local, Object[] local2, int stack, Object[] stack2) {
      }

      public void visitIincInsn(int var, int increment) {
      }

      public void visitInsn(int opcode) {
      }

      public void visitIntInsn(int opcode, int operand) {
      }

      public void visitJumpInsn(int opcode, Label label) {
      }

      public void visitLabel(Label label) {
      }

      public void visitLdcInsn(Object cst) {
      }

      public void visitMethodInsn(int opcode, String owner, String name, String desc) {
      }

      public void visitMultiANewArrayInsn(String desc, int dims) {
      }

      public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
      }
      
      public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) {
      }

      public void visitTypeInsn(int opcode, String type) {
      }

      public void visitVarInsn(int opcode, int var) {
      }
      
      public void visitMaxs(int maxStack, int maxLocals) {
      }
      
      public void visitLocalVariable(String name, String desc,
          String signature, Label start, Label end, int index) {
      }
      
      public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
      }
      
      public void visitLineNumber(int line, Label start) {
      }
      
      public void visitEnd() {
      }
      
    };
  }

  public void visitInnerClass(String name, String outer, String inner, int access) {
  }

  public void visitOuterClass(String owner, String name, String desc) {
  }
  
  public void visitAttribute(Attribute attr) {
  }
  
  public void visitSource(String source, String debug) {
  }
  
  public void visitEnd() {
  }

  static class AnnAnnReader implements AnnotationVisitor {
    private Ann ann;

    public AnnAnnReader(Ann ann) {
      this.ann = ann;
    }

    public void visit(String name, Object value) {
      ann.addParam(name, value);
    }

    public void visitEnum(String name, String desc, String value) {
      ann.addParam(name, new AnnEnum(desc, value));
    }
    
    public AnnotationVisitor visitAnnotation(String name, String desc) {
      Ann ann = new Ann(desc);
      this.ann.addParam(name, ann);
      return new AnnAnnReader(ann);
    }

    public AnnotationVisitor visitArray(String name) {
      return new AnnAnnArrayReader(ann, name);
    }

    public void visitEnd() {
    }
    
  }
  
  static class AnnAnnArrayReader implements AnnotationVisitor {

    private Ann ann;

    private String name;

    // TODO good enough for now, but does not cover general case
    private ArrayList<String> array = new ArrayList<String>();

    public AnnAnnArrayReader(Ann ann, String name) {
      this.ann = ann;
      this.name = name;
    }

    public void visit(String name, Object value) {
      if(value instanceof String) {
        array.add((String) value);
      }
    }

    public AnnotationVisitor visitAnnotation(String name, String value) {
      return null;
    }

    public AnnotationVisitor visitArray(String arg0) {
      return null;
    }

    public void visitEnd() {
      ann.addParam(name, array.toArray(new String[array.size()]));
    }

    public void visitEnum(String arg0, String arg1, String arg2) {
    }

  }

}
