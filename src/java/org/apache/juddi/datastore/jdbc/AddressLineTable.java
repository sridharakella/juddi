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
import org.apache.juddi.datatype.Address;
import org.apache.juddi.datatype.AddressLine;
import org.apache.juddi.datatype.business.BusinessEntity;
import org.apache.juddi.datatype.business.Contact;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.jdbc.ConnectionManager;
import org.apache.juddi.util.jdbc.Transaction;
import org.apache.juddi.uuidgen.UUIDGen;
import org.apache.juddi.uuidgen.UUIDGenFactory;

/**
 * @author Steve Viens (sviens@apache.org)
 */
class AddressLineTable
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(AddressLineTable.class);

  static String insertSQL = null;
  static String selectSQL = null;
  static String deleteSQL = null;

  static
  {
    // buffer used to build SQL statements
    StringBuffer sql = null;

    // build insertSQL
    sql = new StringBuffer(150);
    sql.append("INSERT INTO ADDRESS_LINE (");
    sql.append("BUSINESS_KEY,");
    sql.append("CONTACT_ID,");
    sql.append("ADDRESS_ID,");
    sql.append("ADDRESS_LINE_ID,");
    sql.append("LINE,");
    sql.append("KEY_NAME,");
    sql.append("KEY_VALUE) ");
    sql.append("VALUES (?,?,?,?,?,?,?)");
    insertSQL = sql.toString();

    // build selectSQL
    sql = new StringBuffer(200);
    sql.append("SELECT ");
    sql.append("LINE,");
    sql.append("KEY_NAME,");
    sql.append("KEY_VALUE, ");
    sql.append("ADDRESS_LINE_ID ");
    sql.append("FROM ADDRESS_LINE ");
    sql.append("WHERE BUSINESS_KEY=? ");
    sql.append("AND CONTACT_ID=? ");
    sql.append("AND ADDRESS_ID=? ");
    sql.append("ORDER BY ADDRESS_LINE_ID");
    selectSQL = sql.toString();

    // build deleteSQL
    sql = new StringBuffer(100);
    sql.append("DELETE FROM ADDRESS_LINE ");
    sql.append("WHERE BUSINESS_KEY=?");
    deleteSQL = sql.toString();
  }

  /**
   * Insert new row into the ADDRESS_LINE table.
   *
   * @param businessKey BusinessKey to the BusinessEntity object that owns the Contact to be inserted
   * @param contactID The unique ID generated when saving the parent Contact instance.
   * @param addressID The unique ID generated when saving the parent Address instance.
   * @param lineList Enumeration of AddressLines objects holding values to be inserted.
   * @param connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void insert(String businessKey,int contactID,int addressID,Vector lineList,Connection connection)
    throws java.sql.SQLException
  {
    if ((lineList == null) || (lineList.size() == 0))
      return; // everything is valid but no elements to insert

    PreparedStatement statement = null;

    try
    {
      statement = connection.prepareStatement(insertSQL);
      statement.setString(1,businessKey.toString());
      statement.setInt(2,contactID);
      statement.setInt(3,addressID);

      int listSize = lineList.size();
      for (int lineID=0; lineID<listSize; lineID++)
      {
        AddressLine line = (AddressLine)lineList.elementAt(lineID);
        statement.setInt(4,lineID);
        statement.setString(5,line.getLineValue());
        statement.setString(6,line.getKeyName());
        statement.setString(7,line.getKeyValue());

        log.debug("insert into ADDRESS_LINE table:\n\n\t" + insertSQL +
          "\n\t BUSINESS_KEY=" + businessKey.toString() +
          "\n\t CONTACT_ID=" + contactID +
          "\n\t ADDRESS_ID=" + addressID +
          "\n\t ADDRESS_LINE_ID=" + lineID +
          "\n\t LINE=" + line.getLineValue() +
          "\n\t KEY_NAME=" + line.getKeyName() +
          "\n\t KEY_VALUE=" + line.getKeyValue() + "\n");

        statement.executeUpdate();
      }
    }
    finally
    {
      try {
        statement.close();
      }
      catch (Exception e) { /* ignored */ }
    }
  }

  /**
   * Select all rows from the CONTACT table for a given BusinessKey.
   *
   * @param  businessKey BusinessKey
   * @param  contactID Unique ID representing the parent Contact instance
   * @param  connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static Vector select(String businessKey,int contactID,int addressID,Connection connection)
    throws java.sql.SQLException
  {
    Vector lineList = new Vector();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try
    {
      // create a statement to query with
      statement = connection.prepareStatement(selectSQL);
      statement.setString(1,businessKey.toString());
      statement.setInt(2,contactID);
      statement.setInt(3,addressID);

      log.debug("select from ADDRESS_LINE table:\n\n\t" + selectSQL +
        "\n\t BUSINESS_KEY=" + businessKey.toString() +
        "\n\t CONTACT_KEY=" + contactID +
        "\n\t ADDRESS_ID=" + addressID + "\n");

      // execute the statement
      resultSet = statement.executeQuery();
      while (resultSet.next())
      {
        AddressLine line = new AddressLine();
        line.setLineValue(resultSet.getString(1));//("LINE")
        line.setKeyName(resultSet.getString(2));//("KEY_NAME"));
        line.setKeyValue(resultSet.getString(3));//("KEY_VALUE"));
        lineList.add(line);
      }

      return lineList;
    }
    finally
    {
      try {
        resultSet.close();
        statement.close();
      }
      catch (Exception e) { /* ignored */ }
    }
  }

  /**
   * Delete multiple rows from the ADDRESS_LINE table that are assigned to the
   * BusinessKey specified.
   *
   * @param  businessKey String
   * @param  connection JDBC connection
   * @throws java.sql.SQLException
   */
  public static void delete(String businessKey,Connection connection)
    throws java.sql.SQLException
  {
    PreparedStatement statement = null;

    try
    {
      // prepare the delete
      statement = connection.prepareStatement(deleteSQL);
      statement.setString(1,businessKey.toString());

      log.debug("delete from ADDRESS_LINE table:\n\n\t" + deleteSQL +
        "\n\t BUSINESS_KEY=" + businessKey.toString() + "\n");

      // execute
      statement.executeUpdate();
    }
    finally
    {
      try {
        statement.close();
      }
      catch (Exception e) { /* ignored */ }
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
    Transaction txn = new Transaction();
    UUIDGen uuidgen = UUIDGenFactory.getUUIDGen();

    if (connection != null)
    {
      try
      {
        String authorizedUserID = "sviens";

        String businessKey = uuidgen.uuidgen();
        BusinessEntity business = new BusinessEntity();
        business.setBusinessKey(businessKey);
        business.setAuthorizedName("sviens");
        business.setOperator("WebServiceRegistry.com");

        Contact contact = new Contact();
        contact.setPersonNameValue("Bill Bob");
        contact.setUseType("server");

        Vector contactList = new Vector();
        contactList.add(contact);
        int contactID = 0;

        Address address = new Address();
        address.setUseType("Mailing");
        address.setSortCode("a");

        Vector addrList = new Vector();
        addrList.add(address);
        int addressID = 0;

        AddressLine addrLine1 = new AddressLine();
        addrLine1.setLineValue("SteveViens.com, Inc.");

        AddressLine addrLine2 = new AddressLine();
        addrLine2.setLineValue("PO BOX 6856");

        AddressLine addrLine3 = new AddressLine();
        addrLine3.setLineValue("78 Marne Avenue");

        AddressLine addrLine4 = new AddressLine();
        addrLine4.setLineValue("Portsmouth");

        AddressLine addrLine5 = new AddressLine();
        addrLine5.setLineValue("New Hampshire");

        Vector lineList = new Vector();
        lineList.add(addrLine1);
        lineList.add(addrLine2);
        lineList.add(addrLine3);
        lineList.add(addrLine4);
        lineList.add(addrLine5);

        // begin a new transaction
        txn.begin(connection);

        // insert a new BusinessEntity
        BusinessEntityTable.insert(business,authorizedUserID,connection);

        // insert a new Contact
        ContactTable.insert(businessKey,contactList,connection);

        // insert a new Address
        AddressTable.insert(businessKey,contactID,addrList,connection);

        // insert a Collection of AddressLine objects
        AddressLineTable.insert(businessKey,contactID,addressID,lineList,connection);

        // select the Collection of AddressLine objects
        lineList = AddressLineTable.select(businessKey,contactID,addressID,connection);

        // delete the Collection of AddressLine objects
        //AddressLineTable.delete(businessKey,connection);

        // re-select the Collection of AddressLine objects
        lineList = AddressLineTable.select(businessKey,contactID,addressID,connection);

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
