/*
 * Copyright 2001-2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.juddi.v3.client;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.apache.juddi.v3.annotations.AnnotationProcessor;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClerkManager;
import org.junit.Assert;
import org.junit.Test;
import org.uddi.api_v3.BusinessService;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class UDDIClerkManagerTest {
	
     @Test
     public void testReadingTheConfig() {
	     try {
	    	 UDDIClerkManager.start();
	     } catch (Exception e) {
	    	 //we should not have any issues reading the config
	         e.printStackTrace();
	         Assert.fail();
	     } 
     }
     
     @Test
     public void testAnnotation() {
    	 try {
	    	 Map<String,UDDIClerk> clerks = UDDIClerkManager.getClientConfig().getClerks();
	 		 AnnotationProcessor ap = new AnnotationProcessor();
	 		 UDDIClerk clerk = clerks.get("default");
	 		 BusinessService service = ap.readServiceAnnotations(
	 				 HelloWorldMockup.class.getName(), clerk.getNode().getProperties());
	 		 BusinessService serviceOut = clerk.register(service);
	 		 assertEquals(service.getServiceKey(),serviceOut.getServiceKey());
    	 } catch (Exception e) {
	    	 //we should not have any issues reading the config
	         e.printStackTrace();
	         Assert.fail();
	     } 
    	 
     }
     
   
	
}