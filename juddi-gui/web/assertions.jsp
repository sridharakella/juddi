<%-- 
    Document   : assertions
    Created on : April 13, 2013, 9:14:01 AM
    Author     : Alex O'Ree
--%>

<%@page import="org.apache.juddi.webconsole.hub.UddiHub"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="header-top.jsp" %>
<div class="container">

    <!-- Main hero unit for a primary marketing message or call to action -->
    <div class="well" >
        <h1><%=ResourceLoader.GetResource(session, "navbar.publisherassertions")%></h1>
    </div>

    <!-- Example row of columns -->
    <div class="row">
        <div class="span12" >

            <a href="javascript:ShowAssertionDialog();" ><i class="icon-plus-sign"></i><%=ResourceLoader.GetResource(session, "items.publisherassertion.add")%> </a><br><br>
            <div id="businesslist">
                <img src="img/bigrollergreen.gif" title="<%=ResourceLoader.GetResource(session, "items.loading")%>"/>
            </div>


            <script type="text/javascript">
               
                function ShowAssertionDialog()
                {
                    $("#addPublisherAssertion").modal('show');
                }
                function RenderAssertions()
                {
                    var lang = $("#lang").text();
                    $("#businesslist").html("<img src=\"img/bigrollergreen.gif\" title=\"Loading\"/>");
                    var request=   $.ajax({
                        url: 'ajax/assertions.jsp',
                        type:"GET",
                        cache: false
                    });
                  
                    request.done(function(msg) {
                        window.console && console.log('postback done ');                
                        $("#businesslist").html(msg);
                        //refresh();
                    });

                    request.fail(function(jqXHR, textStatus) {
                        window.console && console.log('postback failed ');                                
                        $("#businesslist").html("An error occured! " + textStatus + jqXHR);
                        //refresh();
                    });
                }
                $('.edit').editable(function(value, settings) { 
                    console.log(this);
                    console.log(value);
                    console.log(settings);
                    RenderAssertions();
                    return(value);
                }, { 
                    type    : 'text',
                    submit  : 'OK'
                });
                
                RenderAssertions();

                function addAssertion()
                {
                    var ok=true;
                    var url='ajax/assertions.jsp';
                    var postbackdata = new Array();
                    $("div.edit").each(function()
                    {
                        //TODO filter out (click to edit) values
                        var id=$(this).attr("id");
                        var value=$(this).text();
                        if (value == i18n_clicktoedit)
                            ok = false;
                        postbackdata.push({
                            name: id, 
                            value: value
                        });
                    }); 
                    postbackdata.push({
                        name:"nonce", 
                        value: $("#nonce").val()
                    });
                    $("div.noedit").each(function()
                    {
                        var id=$(this).attr("id");
                        var value=$(this).text();
                        postbackdata.push({
                            name: id, 
                            value: value
                        });
                    }); 
                    if (!ok)
                    {
                        $("#saveresult").html("A value must be specified for each value.");
                        return;
                    }
    
                    var request=   $.ajax({
                        url: url,
                        type:"POST",
                        //  data" + i18n_type + ": "html", 
                        cache: false, 
                        //  processData: false,f
                        data: postbackdata
                    });
                
                
                    request.done(function(msg) {
                        window.console && console.log('postback done '  + url);                
                        $("#saveresult").html(msg);
                        RenderAssertions();
                    });

                    request.fail(function(jqXHR, textStatus) {
                        window.console && console.log('postback failed ' + url);                                
                        $("#saveresult").html(jqXHR.responseText + textStatus);
                    });
                }
              
              
                function removeAssertion(fromkey,tokey, tmodelkey, keyname, keyvalue)
                {
                    var ok=true;
                    var url='ajax/assertions.jsp?action=delete';
                    var postbackdata = new Array();
                    postbackdata.push({
                        name: 'fromkey', 
                        value: fromkey
                    });
                    postbackdata.push({
                        name: 'tokey', 
                        value: tokey
                    });
                    postbackdata.push({
                        name: 'tmodelkey', 
                        value: tmodelkey
                    });
                    postbackdata.push({
                        name: 'keyname', 
                        value: keyname
                    });
                    postbackdata.push({
                        name: 'keyvalue', 
                        value: keyvalue
                    });
                    postbackdata.push({
                        name: 'action', 
                        value: 'delete'
                    });
                        
                        
                    postbackdata.push({
                        name:"nonce", 
                        value: $("#nonce").val()
                    });
                    
                    var request=   $.ajax({
                        url: url,
                        type:"POST",
                        //  data" + i18n_type + ": "html", 
                        cache: false, 
                        //  processData: false,f
                        data: postbackdata
                    });
                
                
                    request.done(function(msg) {
                        window.console && console.log('postback done '  + url);                
                        $("#saveresult").html(msg);
                        RenderAssertions();
                    });

                    request.fail(function(jqXHR, textStatus) {
                        window.console && console.log('postback failed ' + url);                                
                        $("#saveresult").html(jqXHR.responseText + textStatus);
                    });
                }

                <%
                    String fromkey = request.getParameter("fromkey");
                %>
            </script>
        </div>
    </div>

    <div class="modal hide fade" id="addPublisherAssertion">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3>Add a publisher assertion</h3>
        </div>
        <div class="modal-body" id="addPublisherAssertionContent">
            <div style="float:left;width:25%">From Key : </div><div id="fromkey" class="edit"><%
                if (fromkey != null) {
                    out.write(StringEscapeUtils.escapeHtml(fromkey));
                }
                %></div>
            <div style="float:left;width:25%">To Key : </div><div id="tokey" class="edit"></div>
            <div style="float:left;width:25%">tModel Key : </div><div id="tmodelkey" class="edit">uddi:uddi.org:relationships</div>
            <div style="float:left;width:25%">Key Name : </div><div id="keyname" class="edit">Subsidiary</div>
            <div style="float:left;width:25%">Key Value : </div><div id="keyvalue" class="edit">parent-child</div>
            <div id="saveresult"></div>
        </div>
        <script type="text/javascript">
            Reedit();
        </script>
        <div class="modal-footer">
            <a href="javascript:addAssertion();" class="btn btn-primary">Save</a>
            <a href="javascript:$('#addPublisherAssertion').modal('hide');" class="btn"><%=ResourceLoader.GetResource(session, "modal.close")%></a>
        </div>
    </div>
    <script type="text/javascript">
        <%if (fromkey != null) {
        %>
            $("#addPublisherAssertion").modal('show');
        <%                    }
        %>
    </script>

    <%@include file="header-bottom.jsp" %>