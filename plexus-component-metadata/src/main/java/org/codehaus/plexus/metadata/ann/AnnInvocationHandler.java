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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import org.objectweb.asm.Type;

/**
 * @author Eugene Kuleshov
 */
public class AnnInvocationHandler implements InvocationHandler {
  private final Ann ann;
  private final ClassLoader cl;
  private final Class<?> c;

  public AnnInvocationHandler(Ann ann, ClassLoader cl, Class<?> c) {
    this.ann = ann;
    this.cl = cl;
    this.c = c;
  }

  public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
    String name = m.getName();

    if("toString".equals(name)) {
      StringBuffer sb = new StringBuffer(ann.getType());
      sb.append("[");
      String sep = "";
      for(Map.Entry<String, Object> e : ann.getParams().entrySet()) {
        // TODO conversion for class, array, enum, and annotation types
        sb.append(sep).append(e.getKey()+"="+e.getValue());
        sep = "; ";
      }
      sb.append("]");
      return sb.toString();
    }
    
    Object value = ann.getParams().get(name);
    if(value!=null) {
      if(value instanceof Type) {
        return Class.forName(((Type) value).getClassName(), false, cl);
      }
      // TODO conversion for class, array, enum, and annotation types
      return value;
    } else {
      Method am = c.getDeclaredMethod(m.getName(), m.getParameterTypes());
      return am.getDefaultValue();
    }
  }

}

