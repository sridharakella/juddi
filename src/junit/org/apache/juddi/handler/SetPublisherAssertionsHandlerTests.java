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
package org.apache.juddi.handler;

import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.assertion.PublisherAssertion;
import org.apache.juddi.datatype.request.AuthInfo;
import org.apache.juddi.datatype.request.SetPublisherAssertions;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author anou_mana@apache.org
 */
public class  SetPublisherAssertionsHandlerTests extends HandlerTestCase
{
	private static final String TEST_ID = "juddi.handler.DeleteSetPublisherAssertions.test";
	private  SetPublisherAssertionsHandler handler = null;

  public  SetPublisherAssertionsHandlerTests(String arg0)
  {
    super(arg0);
  }

  public static void main(String[] args)
  {
    junit.textui.TestRunner.run( SetPublisherAssertionsHandlerTests.class);
  }

  public void setUp()
  {
		HandlerMaker maker = HandlerMaker.getInstance();
		handler = ( SetPublisherAssertionsHandler)maker.lookup( SetPublisherAssertionsHandler.TAG_NAME);
  }

	private RegistryObject getRegistryObject()
	{
		AuthInfo authInfo = new AuthInfo();
		authInfo.setValue("6f157513-844e-4a95-a856-d257e6ba9726");

		PublisherAssertion assertion = new PublisherAssertion();
		assertion.setFromKey("3379ec11-a509-4668-9fee-19b134d0d09b");
		assertion.setToKey("3379ec11-a509-4668-9fee-19b134d0d09b");
		assertion.setKeyName("paKeyName");
		assertion.setKeyValue("paKeyValue");
		assertion.setTModelKey("uuid:3379ec11-a509-4668-9fee-19b134d0d09b");

		SetPublisherAssertions object = new SetPublisherAssertions();
		object.setAuthInfo(authInfo);
		object.addPublisherAssertion(assertion);
		object.addPublisherAssertion(assertion);

		return object;

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

		assertNotNull("Marshalled  SetPublisherAssertions ", marshalledString);

	}

	public void testUnMarshal()
	{

		Element child = getMarshalledElement(null);
		RegistryObject regObject = handler.unmarshal(child);

		assertNotNull("UnMarshalled  SetPublisherAssertions ", regObject);

	}

  public void testMarshUnMarshal()
  {
		Element child = getMarshalledElement(null);

		String marshalledString = this.getXMLString(child);

		assertNotNull("Marshalled  SetPublisherAssertions ", marshalledString);

		RegistryObject regObject = handler.unmarshal(child);

		child = getMarshalledElement(regObject);

		String unMarshalledString = this.getXMLString(child);

		assertNotNull("Unmarshalled  SetPublisherAssertions ", unMarshalledString);

		boolean equals = marshalledString.equals(unMarshalledString);

		assertEquals("Expected SetPublisherAssertions: ", marshalledString, unMarshalledString );
  }

}
