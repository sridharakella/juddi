/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "jUDDI" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.juddi.datastore.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.datatype.request.FindQualifiers;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.jdbc.ConnectionManager;
import org.apache.juddi.util.jdbc.Transaction;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class FindTModelByNameQuery
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(FindTModelByNameQuery.class);

  static String selectSQL;
  static
  {
    // build selectSQL
    StringBuffer sql = new StringBuffer(200);
    sql.append("SELECT M.TMODEL_KEY,M.LAST_UPDATE,M.NAME ");
    sql.append("FROM TMODEL M ");
    selectSQL = sql.toString();
  }

  /**
   * Select ...
   *
   * @param name primary key value
   * @param keysIn primary key value
   * @param qualifiers primary key value
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector select(String name,Vector keysIn,FindQualifiers qualifiers,Connection connection)
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
    StringBuffer sql = new StringBuffer(selectSQL);
    appendWhere(sql,name,qualifiers);
    appendIn(sql,name,keysIn);
    appendOrderBy(sql,qualifiers);

    try
    {
      log.debug("select from TMODEL table:\n\n\t" + sql.toString() + "\n");

      statement = connection.prepareStatement(sql.toString());
      resultSet = statement.executeQuery();

      while (resultSet.next())
        keysOut.addElement(resultSet.getString(1));//("TMODEL_KEY"));

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
          "the Find TModel ResultSet: "+e.getMessage(),e);
      }

      try {
        statement.close();
      }
      catch (Exception e)
      {
        log.warn("An Exception was encountered while attempting to close " +
          "the Find TModel Statement: "+e.getMessage(),e);
      }
    }
  }

  /**
   *
   */
  private static void appendWhere(StringBuffer sql,String name,FindQualifiers qualifiers)
  {
    if ((name == null) || (name.length() == 0))
      return;

    if ((qualifiers != null) && (qualifiers.exactNameMatch))
      sql.append("WHERE M.NAME = '").append(name).append("' ");
    else
      sql.append("WHERE M.NAME LIKE '").append(name).append("%' ");
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
  private static void appendIn(StringBuffer sql,String name,Vector keysIn)
  {
    if (keysIn == null)
      return;

    if ((name == null) || (name.length() == 0))
      sql.append("WHERE M.TMODEL_KEY IN (");
    else
      sql.append("AND M.TMODEL_KEY IN (");

    int keyCount = keysIn.size();
    for (int i=0; i<keyCount; i++)
    {
      String key = (String)keysIn.elementAt(i);
      sql.append("'").append(key).append("'");

      if ((i+1) < keyCount)
        sql.append(",");
    }

    sql.append(") ");
  }

  /**
   *
   */
  private static void appendOrderBy(StringBuffer sql,FindQualifiers qualifiers)
  {
    sql.append("ORDER BY ");

    if ((qualifiers == null) ||
       ((!qualifiers.sortByNameAsc) && (!qualifiers.sortByNameDesc) &&
        (!qualifiers.sortByDateAsc) && (!qualifiers.sortByDateDesc)))
    {
      sql.append("M.NAME ASC,M.LAST_UPDATE DESC");
    }
    else if (qualifiers.sortByNameAsc || qualifiers.sortByNameDesc)
    {
      if (qualifiers.sortByNameAsc && qualifiers.sortByDateDesc)
        sql.append("M.NAME ASC,M.LAST_UPDATE DESC");
      else if (qualifiers.sortByNameAsc && qualifiers.sortByDateAsc)
        sql.append("M.NAME ASC,M.LAST_UPDATE ASC");
      else if (qualifiers.sortByNameDesc && qualifiers.sortByDateDesc)
        sql.append("M.NAME DESC,M.LAST_UPDATE DESC");
      else
        sql.append("M.NAME DESC,M.LAST_UPDATE ASC");
    }
    else if (qualifiers.sortByDateAsc || qualifiers.sortByDateDesc)
    {
      if (qualifiers.sortByDateDesc)
        sql.append("M.LAST_UPDATE ASC,M.NAME ASC");
      else
        sql.append("M.LAST_UPDATE DESC,M.NAME ASC");
    }
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String[] args)
    throws Exception
  {
    // make sure we're using a DBCP DataSource and
    // not trying to use JNDI to aquire one.
    Config.setStringProperty("juddi.useConnectionPool","true");

    Connection conn = null;
    try {
      conn = ConnectionManager.aquireConnection();
      test(conn);
    }
    finally {
      if (conn != null)
        conn.close();
    }
  }

  public static void test(Connection connection)
    throws Exception
  {
    String name = new String("St");

    Vector keysIn = null;
    keysIn = new Vector();
    keysIn.add("0e70128c-f7c6-4854-b292-d2f13b638acf");
    keysIn.add("b405450a-64f5-4f95-8131-450429d0ae8c");
    keysIn.add("3009f336-98c1-4193-a22f-fea73e79c909");
    keysIn.add("45994713-d3c3-40d6-87b5-6ce51f36001c");
    keysIn.add("901b15c5-799c-4387-8337-a1a35fceb791");
    keysIn.add("80fdae14-0e5d-4ea6-8eb8-50fde422056d");
    keysIn.add("e1996c33-c436-4004-9e3e-14de191bcc6b");
    keysIn.add("3ef4772f-e04b-46ed-8065-c5a4e167b5ba");

    Transaction txn = new Transaction();

    if (connection != null)
    {
      try
      {
        // begin a new transaction
        txn.begin(connection);

        select(name,keysIn,null,connection);
        select(name,null,null,connection);

        // commit the transaction
        txn.commit();
      }
      catch(Exception ex)
      {
        try { txn.rollback(); }
        catch(java.sql.SQLException sqlex) { sqlex.printStackTrace(); }
        throw ex;
      }
    }
  }
}
