/**
 *    Copyright 2009-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.plugin;

import java.util.Properties;

/**
 * @author Clinton Begin
 * 拦截器，在目标对象方法前后执行拦截器的逻辑
 */
public interface Interceptor {

  /**
   * 执行拦截逻辑的方法
   * @param invocation
   * @return
   * @throws Throwable
   */
  Object intercept(Invocation invocation) throws Throwable;

  /**
   * 给目标对象添加当前的拦截器
   * @param target
   * @return
   */
  default Object plugin(Object target) {
    return Plugin.wrap(target, this);
  }

  /**
   * 根据配置初始化Interceptor对象
   * @param properties
   */
  default void setProperties(Properties properties) {
    // NOP
  }

}
