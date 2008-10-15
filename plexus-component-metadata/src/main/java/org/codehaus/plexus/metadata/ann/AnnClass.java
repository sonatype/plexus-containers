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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.Type;

/**
 * @author Eugene Kuleshov
 */
public class AnnClass {
  
  private int access;
  private String name;
  private String superName;
  private String[] interfaces;
  
  private Map<String, Ann> anns = new LinkedHashMap<String, Ann>();
  private Map<String, AnnField> fields = new LinkedHashMap<String, AnnField>();
  private Map<String, AnnMethod> methods = new LinkedHashMap<String, AnnMethod>();
  private ClassLoader cl;

  // setters
  
  public AnnClass(ClassLoader cl) {
    this.cl = cl;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setAccess(int access) {
    this.access = access;
  }

  public void setSuperName(String superName) {
    this.superName = superName;
  }

  public void setInterfaces(String[] interfaces) {
    this.interfaces = interfaces;
  }
  
  public void addAnn(Ann ann) {
    anns.put(ann.getDesc(), ann);
  }
  
  public void addField(AnnField field) {
    fields.put(field.getName(), field);
  }
  
  public void addMethod(AnnMethod method) {
    methods.put(method.getName() + method.getDesc(), method);
  }
  
  // getters
  
  public ClassLoader getClassLoader() {
    return cl;
  }
  
  public int getAccess() {
    return access;
  }

  public String getName() {
    return name;
  }
  
  public String getSuperName() {
    return superName;
  }

  public String[] getInterfaces() {
    return interfaces;
  }

  public Map<String, Ann> getAnns() {
    return anns;
  }

  public Map<String, AnnField> getFields() {
    return fields;
  }

  public Map<String, AnnMethod> getMethods() {
    return methods;
  }

  public Set<String> getFieldNames() {
    return fields.keySet();
  }
  
  public Set<String> getMethodKeys() {
    return methods.keySet();
  }
  
  // conversion to java.lang.Annotation
  
  public <T> T getAnnotation(Class<T> c) {
    Ann ann = anns.get(Type.getDescriptor(c));
    return ann == null ? null : ann.getAnnotation(c, cl);
  }

  public <T> T getFieldAnnotation(String fieldName, Class<T> c) {
    AnnField field = fields.get(fieldName);
    return field==null ? null : field.getAnnotation(c);
  }
  
  public <T> T getMethodAnnotation(String methodKey, Class<T> c) {
    AnnMethod method = methods.get(methodKey);
    return method==null ? null : method.getAnnotation(c);
  }

}

