<jsp:include page="header.html" />

<%
   String requestName = "save_business";
   String requestType = "publish";
   String requestKey = requestName+":request";
   String responseKey = requestName+":response";
%>

<form method="post" action="controller.jsp">
<textarea class=msgs id=soap_request name=soap_request rows=15 cols=75 wrap=off><%
String requestMessage = (String)session.getAttribute(requestKey);
if (requestMessage != null) {
  out.print(requestMessage);
} else { %>
<?xml version="1.0" encoding="utf-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
  <soapenv:Body>
    <save_business generic="2.0" xmlns="urn:uddi-org:api_v2">
      <authInfo>*****</authInfo>
      <businessEntity businessKey="">
        <discoveryURLs>
          <discoveryURL useType="*****">*****</discoveryURL>
        </discoveryURLs>
        <name xml:lang="en">*****</name>
        <description xml:lang="en">*****</description>
        <contacts>
          <contact useType="*****">
            <personName>*****</personName>
            <phone>*****</phone>
          </contact>
          <contact useType="*****">
            <personName>*****</personName>
            <phone>*****</phone>
          </contact>
        </contacts>
      </businessEntity>
    </save_business>
  </soapenv:Body>
</soapenv:Envelope>
<% } %>
</textarea>
<p>
<input type="hidden" name="request_name" value=<%=requestName%>>
<input type="hidden" name="request_type" value=<%=requestType%>>
<input type=submit name="submit_button" value="Submit">
<input type=submit name="reset_button" value="Reset">
</p>
<textarea class=msgs id=soap_response name=soap_response rows=20 cols=75 wrap=off><%
String responseMessage = (String)session.getAttribute(responseKey);
if (responseMessage != null) {
  out.print(responseMessage);
} %>
</textarea>
</form>

<jsp:include page="footer.html" />