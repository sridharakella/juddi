<%@ page language="java" import="java.util.*,java.io.*,java.net.*" %>

<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%
  String requestName = request.getParameter("request_name");
  String requestType = request.getParameter("request_type");
  String submitButton = request.getParameter("submit_button");
  String resetButton = request.getParameter("reset_button");
  String requestKey = requestName+":request";
  String responseKey = requestName+":response"; 
  String requestMsg = request.getParameter("soap_request");
  String responseMsg = "";
  String targetPage = requestName+".jsp";

  try 
  {
    // Create a Socket
    String hostname = "localhost";
    int port = 8080;
    InetAddress addr = InetAddress.getByName(hostname);
    Socket sock = new Socket(addr,port);
		
    // Send the HTTP Headers
    String path = "/juddi/"+requestType;
    BufferedWriter  wr = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream(),"UTF-8"));
    wr.write("POST " + path + " HTTP/1.0\r\n");
    wr.write("Content-Length: " + requestMsg.length() + "\r\n");
    wr.write("Content-Type: text/xml; charset=\"utf-8\"\r\n");
    wr.write("\r\n");
		
    // Send the HTTP Request
    wr.write(requestMsg);
    wr.flush();
		
    // Read the HTTP Response
    BufferedReader rd = new BufferedReader(new InputStreamReader(sock.getInputStream()));
    String line;
    StringBuffer message = new StringBuffer();
    while((line = rd.readLine()) != null)
      message.append(line+"\n");
      
    responseMsg = message.toString();
   } 
  catch (Exception e) 
  {
    e.printStackTrace();
  }
  
  session.setAttribute(requestKey,requestMsg);
  session.setAttribute(responseKey,responseMsg);
  
  // Redirect back to the source page
  
%>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en-US" xml:lang="en-US">
<head>
<title>jUDDI Web Services Registry</title>
<link rel="stylesheet" href="../juddi.css"/>
</head>
<body>
<div class="nav" align="right"><font size="-2"><a href="http://ws.apache.org/juddi/" target="_top">jUDDI@Apache</a></font></div>
<h1>jUDDI Console</h1>
<TABLE border="0">

<TR valign=top>
<TD>
<H2>SOAP Request Name</H2>
<%= requestName %>
<P></P>
</TD>
</TR>

<TR valign=top>
<TD>
<H2>SOAP Request Type</H2>
<%= requestType %>
<P></P>
</TD>
</TR>

<TR valign=top>
<TD>
<H2>SOAP Request Action</H2>
Submit Button: <%= submitButton %><br>
Reset Button: <%= resetButton %>
<P></P>
</TD>
</TR>


<TR valign=top>
<TD>
<H2>SOAP Request Message</H2>
<FORM>
<TEXTAREA rows=15 cols=75 wrap=off><%= session.getAttribute(requestKey) %></TEXTAREA>
</FORM>
</TD>
</TR>


<TR valign=top>
<TD>
<H2>SOAP Response Message</H2>
<FORM>
<TEXTAREA rows=15 cols=75 wrap=off><%= session.getAttribute(responseKey) %></TEXTAREA>
</FORM>
</TD>
</TR>


<TR valign=top>
<TD>
<H2>Current Message Set</H2>
<%
	Enumeration enum = session.getAttributeNames();
	while (enum.hasMoreElements())
	{
	  out.print(enum.nextElement());
	  out.print("<br>");
	}
%>
</TD>
</TR>


</TABLE>
</BODY>
</HTML>
