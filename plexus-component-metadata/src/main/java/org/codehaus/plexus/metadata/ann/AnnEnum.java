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

import org.objectweb.asm.Type;

/**
 * @author Eugene Kuleshov
 */
public class AnnEnum {

  private final String desc;
  private final String value;

  public AnnEnum(String desc, String value) {
    this.desc = desc;
    this.value = value;
  }

  public String getDesc() {
    return desc;
  }

  public String getValue() {
    return value;
  }
  
  public String getType() {
    return Type.getType(desc).getClassName(); 
  }

  public int hashCode() {
    return 31 * (31 + desc.hashCode()) + value.hashCode();
  }

  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    AnnEnum other = (AnnEnum) obj;
    if (desc == null) {
      if (other.desc != null) {
        return false;
      }
    } else if (!desc.equals(other.desc)) {
      return false;
    }
    if (value == null) {
      if (other.value != null) {
        return false;
      }
    } else if (!value.equals(other.value)) {
      return false;
    }
    return true;
  }
  
}
