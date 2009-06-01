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

import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.request.FindPublisher;
import org.apache.juddi.datatype.request.FindQualifier;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author anou_mana@apache.org
 */
public class  FindPublisherHandlerTests extends HandlerTestCase
{
	private static final String TEST_ID = "juddi.handler.DeletePublisher.test";
	private  FindPublisherHandler handler = null;

  public  FindPublisherHandlerTests(String arg0)
  {
    super(arg0);
  }

  public static void main(String[] args)
  {
    junit.textui.TestRunner.run( FindPublisherHandlerTests.class);
  }

  public void setUp()
  {
		HandlerMaker maker = HandlerMaker.getInstance();
		handler = ( FindPublisherHandler)maker.lookup( FindPublisherHandler.TAG_NAME);
  }

	private RegistryObject getRegistryObject()
	{
		FindPublisher object = new FindPublisher();
		object.setName(new Name("s","en"));
		object.addFindQualifier(new FindQualifier(FindQualifier.SORT_BY_NAME_ASC));
		object.setMaxRows(50);

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

		assertNotNull("Marshalled  FindPublisher ", marshalledString);

	}

	public void testUnMarshal()
	{

		Element child = getMarshalledElement(null);
		RegistryObject regObject = handler.unmarshal(child);

		assertNotNull("UnMarshalled  FindPublisher ", regObject);

	}

  public void testMarshUnMarshal()
  {
		Element child = getMarshalledElement(null);

		String marshalledString = this.getXMLString(child);

		assertNotNull("Marshalled  FindPublisher ", marshalledString);

		RegistryObject regObject = handler.unmarshal(child);

		child = getMarshalledElement(regObject);

		String unMarshalledString = this.getXMLString(child);

		assertNotNull("Unmarshalled  FindPublisher ", unMarshalledString);

		boolean equals = marshalledString.equals(unMarshalledString);

		assertEquals("Expected result: ", marshalledString, unMarshalledString );
  }

}