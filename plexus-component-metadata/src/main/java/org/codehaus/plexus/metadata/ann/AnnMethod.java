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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.objectweb.asm.Type;

/**
 * @author Eugene Kuleshov
 */
public class AnnMethod {

  private final AnnClass owner;
  private final int access;
  private final String name;
  private final String desc;
  private Map<String, Ann> anns = new LinkedHashMap<String, Ann>();
  private Map<Integer, Map<String, Ann>> paramAnns = new HashMap<Integer, Map<String,Ann>>();

  public AnnMethod(AnnClass owner, int access, String name, String desc) {
    this.owner = owner;
    this.access = access;
    this.name = name;
    this.desc = desc;
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
  
  public Map<Integer, Map<String, Ann>> getParamAnns() {
    return paramAnns;
  }
  
  public void addAnn(Ann ann) {
    anns.put(ann.getDesc(), ann);
  }

  public void addParamAnn(int parameter, Ann ann) {
    Map<String, Ann> anns = paramAnns.get(parameter);
    if(anns==null) {
      anns = new LinkedHashMap<String, Ann>();
      paramAnns.put(parameter, anns);
    }
    anns.put(ann.getDesc(), ann);
  }
  
  public <T> T getAnnotation(Class<T> c) {
    Ann ann = anns.get(Type.getDescriptor(c));
    return ann == null ? null : ann.getAnnotation(c, owner.getClassLoader());
  }
  
  public <T> T getParameterAnnotation(int parameter, Class<T> c) {
    Map<String, Ann> anns = paramAnns.get(parameter);
    if (anns == null) {
      return null;
    }
    Ann ann = anns.get(Type.getDescriptor(c));
    return ann == null ? null : ann.getAnnotation(c, owner.getClassLoader());
  }

}

