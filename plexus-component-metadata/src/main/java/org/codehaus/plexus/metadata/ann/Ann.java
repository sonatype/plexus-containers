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

import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;

import org.objectweb.asm.Type;

/**
 * @author Eugene Kuleshov
 */
public class Ann {

  private String desc;
  private Map<String, Object> params = new LinkedHashMap<String, Object>();
  
  public Ann(String desc) {
    this.desc = desc;
  }

  public void addParam(String name, Object value) {
    params.put(name, value);
  }
  
  public String getDesc() {
    return desc;
  }
  
  public String getType() {
    return Type.getType(desc).getClassName();
  }

  public Map<String, Object> getParams() {
    return params;
  }

  @SuppressWarnings("unchecked")
  public <T> T getAnnotation(Class<T> c, ClassLoader cl) {
    return (T) Proxy.newProxyInstance(Ann.class.getClassLoader(), new Class[] { c }, //
        new AnnInvocationHandler(this, cl, c));
  }
  
}
