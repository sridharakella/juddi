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
package org.apache.juddi.handler;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.apache.juddi.datatype.Description;
import org.apache.juddi.datatype.OverviewDoc;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.binding.AccessPoint;
import org.apache.juddi.datatype.binding.BindingTemplate;
import org.apache.juddi.datatype.binding.HostingRedirector;
import org.apache.juddi.datatype.binding.InstanceDetails;
import org.apache.juddi.datatype.binding.TModelInstanceDetails;
import org.apache.juddi.datatype.binding.TModelInstanceInfo;
import org.apache.juddi.datatype.response.BindingDetail;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author anou_mana@users.sourceforge.net
 */
public class BindingDetailHandlerTests extends TestCase
{
	private static final String TEST_ID = "juddi.handler.BindingDetail.test";
	private BindingDetailHandler handler = null;

  public BindingDetailHandlerTests(String arg0)
  {
    super(arg0);
  }

  public static void main(String[] args)
  {
    junit.textui.TestRunner.run(BindingDetailHandlerTests.class);
  }

  public void setUp()
  {
		HandlerMaker maker = HandlerMaker.getInstance();
		handler = (BindingDetailHandler)maker.lookup(BindingDetailHandler.TAG_NAME);
  }

	private RegistryObject getRegistryObject()
	{

		OverviewDoc overDoc = new OverviewDoc();
		overDoc.setOverviewURL("http://www.juddi.org/service.html");
		overDoc.addDescription(new Description("over-doc Descr"));
		overDoc.addDescription(new Description("over-doc Descr Two","en"));

		InstanceDetails instDetails = new InstanceDetails();
		instDetails.addDescription(new Description("inst-det descr"));
		instDetails.addDescription(new Description("inst-det descr in italy","it"));
		instDetails.setInstanceParms("inst-det parameters");
		instDetails.setOverviewDoc(overDoc);

		TModelInstanceInfo tModInstInfo = new TModelInstanceInfo();
		tModInstInfo.setTModelKey("uuid:ae0f9fd4-287f-40c9-be91-df47a7c72fd9");
		tModInstInfo.addDescription(new Description("tMod-Inst-Info"));
		tModInstInfo.addDescription(new Description("tMod-Inst-Info too","es"));
		tModInstInfo.setInstanceDetails(instDetails);

		TModelInstanceDetails tModInstDet = new TModelInstanceDetails();
		tModInstDet.addTModelInstanceInfo(tModInstInfo);

		BindingTemplate binding =  new BindingTemplate();
		binding.setBindingKey("c9613c3c-fe55-4f34-a3da-b3167afbca4a");
		binding.setServiceKey("997a55bc-563d-4c04-8a94-781604870d31");
		binding.addDescription(new Description("whatever"));
		binding.addDescription(new Description("whatever too","fr"));
		binding.setHostingRedirector(new HostingRedirector("92658289-0bd7-443c-8948-0bb4460b44c0"));
		binding.setAccessPoint(new AccessPoint(AccessPoint.HTTP,"http://www.juddi.org/service.wsdl"));
		binding.setTModelInstanceDetails(tModInstDet);

		BindingDetail object = new BindingDetail();
		object.setGeneric("2.0");
		object.setOperator("jUDDI.org");
		object.setTruncated(true);
		object.addBindingTemplate(binding);
		object.addBindingTemplate(binding);


		return object;

	}
	
	private String getXMLString(Element element)
	{
		StringWriter writer = new StringWriter();
		XMLUtils.writeXML(element,writer);

		String xmlString = writer.toString();
		
		try
		{
			writer.close();
		}
		catch(IOException exp)
		{
		}
		
		return xmlString;
	}
	
	private Element getMarshalledElement(RegistryObject regObject)
	{
		Element parent = XMLUtils.newRootElement();
		Element child = null;

		if(regObject == null)
			regObject = this.getRegistryObject();
		
		handler.marshal(regObject,parent);
		child = (Element)parent.getFirstChild();
		parent.removeChild(child);

		return child;
	}
	
	public void testMarshal()
	{
		Element child = getMarshalledElement(null);
		
		String marshalledString = this.getXMLString(child);
		
		assertNotNull("Marshalled BindingDetail ", marshalledString);

	}
	
	public void testUnMarshal()
	{
				
		Element child = getMarshalledElement(null);
		RegistryObject regObject = handler.unmarshal(child);
		
		assertNotNull("UnMarshalled BindingDetail ", regObject);

	}
	
  public void testMarshUnMarshal()
  {
		Element child = getMarshalledElement(null);
		
		String marshalledString = this.getXMLString(child);
		
		assertNotNull("Marshalled BindingDetail ", marshalledString);
		
		RegistryObject regObject = handler.unmarshal(child);
		
		child = getMarshalledElement(regObject);

		String unMarshalledString = this.getXMLString(child);
		
		assertNotNull("Unmarshalled BindingDetail ", unMarshalledString);
		
		boolean equals = marshalledString.equals(unMarshalledString);

		assertEquals("Expected result: ", marshalledString, unMarshalledString );
  }

}
