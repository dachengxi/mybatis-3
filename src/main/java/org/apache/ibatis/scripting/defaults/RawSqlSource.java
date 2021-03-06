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
package org.apache.ibatis.scripting.defaults;

import java.util.HashMap;

import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.DynamicContext;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.session.Configuration;

/**
 * Static SqlSource. It is faster than {@link DynamicSqlSource} because mappings are
 * calculated during startup.
 * 原始静态sql语句封装，加载的时候就已经确定了sql语句，没有动态标签以及${}等的处理
 *
 * 处理静态sql语句，最后会将处理后的sql语句封装成StaticSqlSource返回，在MyBatis初始化时完成sql语句的解析
 *
 * 如果只包含'#{}'占位符，而不包含动态sql或未解析的'${}'占位符，则不是动态sql语句
 *
 * 静态sql也是动态SQL的一种
 *
 * @since 3.2.0
 * @author Eduardo Macarron
 */
public class RawSqlSource implements SqlSource {

  /**
   * 是一个StaticSqlSource，其中封装了占位符被替换成?的sql语句以及参数对应的ParameterMapping集合
   */
  private final SqlSource sqlSource;

  /**
   *
   * @param configuration
   * @param rootSqlNode 一个select、update、delete、insert节点下面的所有的SqlNode集合
   * @param parameterType 参数类型
   */
  public RawSqlSource(Configuration configuration, SqlNode rootSqlNode, Class<?> parameterType) {
    /**
     * 先调用getSql方法，其中会调用SqlNode.apply方法完成sql语句的拼装和初步处理
     * getSql方法返回的sql是拼装好的原始的sql，包含#{}占位符
     *
     * rootSqlNode中包含的各种SqlNode都是从xml中解析出来的原始的节点数据
     */
    this(configuration, getSql(configuration, rootSqlNode), parameterType);
  }

  public RawSqlSource(Configuration configuration, String sql, Class<?> parameterType) {
    SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
    Class<?> clazz = parameterType == null ? Object.class : parameterType;
    /**
     * 完成占位符的替换和ParameterMapping的创建
     * 会将sql中#{}替换为?，并且记录?对应的参数类型
     *
     * 返回的是一个StaticSqlSource
     */
    sqlSource = sqlSourceParser.parse(sql, clazz, new HashMap<>());
  }

  private static String getSql(Configuration configuration, SqlNode rootSqlNode) {
    // 静态sql文本，没有参数
    DynamicContext context = new DynamicContext(configuration, null);
    // 不同类型的SqlNode执行apply方法，最后都会是StaticTextSqlNode，直接返回sql
    rootSqlNode.apply(context);
    return context.getSql();
  }

  @Override
  public BoundSql getBoundSql(Object parameterObject) {
    return sqlSource.getBoundSql(parameterObject);
  }

}
