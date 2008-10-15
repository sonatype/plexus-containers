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

import org.objectweb.asm.Type;

/**
 * @author Eugene Kuleshov
 */
public class AnnField {

  private final AnnClass owner;
  private final int access;
  private final String name;
  private final String desc;
  private Map<String, Ann> anns = new LinkedHashMap<String,Ann>();
  
  public AnnField(AnnClass owner, int access, String name, String desc) {
    this.owner = owner;
    this.access = access;
    this.desc = desc;
    this.name = name;
  }

  public int getAccess() {
    return access;
  }
  
  public String getName() {
    return name;
  }
  
  public String getDesc() {
    return desc;
  }
  
  public Map<String, Ann> getAnns() {
    return anns;
  }
  
  public String getType() {
    return Type.getType(desc).getClassName();
  }

  public void addAnn(Ann ann) {
    anns.put(ann.getDesc(), ann);
  }
  
  public <T> T getAnnotation(Class<T> c) {
    Ann ann = anns.get(Type.getDescriptor(c));
    return ann == null ? null : ann.getAnnotation(c, owner.getClassLoader());
  }

}

