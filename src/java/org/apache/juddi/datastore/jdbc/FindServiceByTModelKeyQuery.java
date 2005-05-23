/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.juddi.datastore.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.datatype.TModelBag;
import org.apache.juddi.datatype.request.FindQualifiers;
import org.apache.juddi.util.jdbc.DynamicQuery;

/**
 * tModelBag: This is a list of tModel uuid_key values that represent the 
 * technical fingerprint of a bindingTemplate structure to find. All 
 * bindingTemplate structures within any businessService associated with the 
 * businessEntity specified by the businessKey argument will be searched. 
 * 
 * If more than one tModel key is specified in this structure, only 
 * businessService structures that contain bindingTemplate structures with 
 * fingerprint information that matches all of the tModel keys specified will 
 * be returned (logical AND only).
 * 
 * @author Steve Viens (sviens@apache.org)
 */
class FindServiceByTModelKeyQuery
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(FindServiceByTModelKeyQuery.class);

  static String selectSQL;
  static
  {
    // build selectSQL
    StringBuffer sql = new StringBuffer(200);
    sql.append("SELECT S.SERVICE_KEY,S.LAST_UPDATE ");
    sql.append("FROM BUSINESS_SERVICE S,BINDING_TEMPLATE T,TMODEL_INSTANCE_INFO I ");
    selectSQL = sql.toString();
  }

  /**
   * Select ...
   *
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector select(String businessKey,TModelBag tModelBag,Vector keysIn,FindQualifiers qualifiers,Connection connection)
    throws java.sql.SQLException
  {
    // if there is a keysIn vector but it doesn't contain
    // any keys then the previous query has exhausted
    // all possibilities of a match so skip this call.
    if ((keysIn != null) && (keysIn.size() == 0))
      return keysIn;

    Vector keysOut = new Vector();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    // construct the SQL statement
    DynamicQuery sql = new DynamicQuery(selectSQL);
    appendWhere(sql,businessKey,tModelBag,qualifiers);
    appendIn(sql,keysIn);
    appendOrderBy(sql,qualifiers);

    try
    {
      log.debug(sql.toString());

      statement = sql.buildPreparedStatement(connection);
      resultSet = statement.executeQuery();

      while (resultSet.next())
        keysOut.addElement(resultSet.getString(1));//("SERVICE_KEY"));

      return keysOut;
    }
    finally
    {
      try {
        resultSet.close();
      }
      catch (Exception e)
      {
        log.warn("An Exception was encountered while attempting to close " +
          "the Find BusinessService ResultSet: "+e.getMessage(),e);
      }

      try {
        statement.close();
      }
      catch (Exception e)
      {
        log.warn("An Exception was encountered while attempting to close " +
          "the Find BusinessService Statement: "+e.getMessage(),e);
      }
    }
  }

  /**
   *
   */
  private static void appendWhere(DynamicQuery sql,String businessKey,TModelBag tModelBag,FindQualifiers qualifiers)
  {
    sql.append("WHERE I.BINDING_KEY = T.BINDING_KEY ");
    sql.append("AND T.SERVICE_KEY = S.SERVICE_KEY ");

    if (businessKey != null)
    {
      sql.append("AND S.BUSINESS_KEY = ? ");
      sql.addValue(businessKey);
    }
    
    Vector keyVector = tModelBag.getTModelKeyVector();

    int vectorSize = keyVector.size();
    if (vectorSize > 0)
    {
      sql.append("AND (");

      for (int i=0; i<vectorSize; i++)
      {
        String key = (String)keyVector.elementAt(i);

        sql.append("I.TMODEL_KEY = ? ");
        sql.addValue(key);
        
        if (i+1 < vectorSize)
          sql.append(" OR ");
      }

      sql.append(") ");
    }
  }

  /**
   * Select ...
   *
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector select(String businessKey,String tModelKey,Vector keysIn,FindQualifiers qualifiers,Connection connection)
    throws java.sql.SQLException
  {
    // if there is a keysIn vector but it doesn't contain
    // any keys then the previous query has exhausted
    // all possibilities of a match so skip this call.
    if ((keysIn != null) && (keysIn.size() == 0))
      return keysIn;

    Vector keysOut = new Vector();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    // construct the SQL statement
    DynamicQuery sql = new DynamicQuery(selectSQL);
    appendWhere(sql,businessKey,tModelKey,qualifiers);
    appendIn(sql,keysIn);
    appendOrderBy(sql,qualifiers);

    try
    {
      log.debug(sql.toString());

      statement = sql.buildPreparedStatement(connection);
      resultSet = statement.executeQuery();

      while (resultSet.next())
        keysOut.addElement(resultSet.getString(1));//("SERVICE_KEY"));

      return keysOut;
    }
    finally
    {
      try {
        resultSet.close();
      }
      catch (Exception e)
      {
        log.warn("An Exception was encountered while attempting to close " +
          "the Find BusinessService ResultSet: "+e.getMessage(),e);
      }

      try {
        statement.close();
      }
      catch (Exception e)
      {
        log.warn("An Exception was encountered while attempting to close " +
          "the Find BusinessService Statement: "+e.getMessage(),e);
      }
    }
  }

  /**
   *
   */
  private static void appendWhere(DynamicQuery sql,String businessKey,String tModelKey,FindQualifiers qualifiers)
  {
    sql.append("WHERE I.BINDING_KEY = T.BINDING_KEY ");
    sql.append("AND T.SERVICE_KEY = S.SERVICE_KEY ");

    if ((businessKey != null) && (businessKey.trim().length() > 0))
    {
      sql.append("AND S.BUSINESS_KEY = ? ");
      sql.addValue(businessKey);
    }
    
    if ((tModelKey != null) && (tModelKey.trim().length() > 0))
    {
      sql.append("AND I.TMODEL_KEY = ? ");
      sql.addValue(tModelKey);
    }
  }

  /**
   * Utility method used to construct SQL "IN" statements such as
   * the following SQL example:
   *
   *   SELECT * FROM TABLE WHERE MONTH IN ('jan','feb','mar')
   *
   * @param sql StringBuffer to append the final results to
   * @param keysIn Vector of Strings used to construct the "IN" clause
   */
  private static void appendIn(DynamicQuery sql,Vector keysIn)
  {
    if (keysIn == null)
      return;

    sql.append("AND S.SERVICE_KEY IN (");

    int keyCount = keysIn.size();
    for (int i=0; i<keyCount; i++)
    {
      String key = (String)keysIn.elementAt(i);
      sql.append("?");
      sql.addValue(key);

      if ((i+1) < keyCount)
        sql.append(",");
    }

    sql.append(") ");
  }

  /**
   *
   */
  private static void appendOrderBy(DynamicQuery sql,FindQualifiers qualifiers)
  {
    sql.append("ORDER BY ");

    if (qualifiers == null)
      sql.append("S.LAST_UPDATE DESC");
    else if (qualifiers.sortByDateAsc)
      sql.append("S.LAST_UPDATE ASC");
    else
      sql.append("S.LAST_UPDATE DESC");
  }
}
