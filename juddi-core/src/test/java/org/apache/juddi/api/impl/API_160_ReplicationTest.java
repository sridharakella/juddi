/*
 * Copyright 2014 The Apache Software Foundation.
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
package org.apache.juddi.api.impl;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.List;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.Registry;
import org.apache.juddi.api_v3.Clerk;
import org.apache.juddi.api_v3.ClerkList;
import org.apache.juddi.api_v3.DeleteClerk;
import org.apache.juddi.api_v3.DeleteNode;
import org.apache.juddi.api_v3.Node;
import org.apache.juddi.api_v3.NodeList;
import org.apache.juddi.api_v3.SaveClerk;
import org.apache.juddi.api_v3.SaveNode;
import org.apache.juddi.replication.ReplicationNotifier;
import org.apache.juddi.v3.error.FatalErrorException;
import org.apache.juddi.v3.tck.TckPublisher;
import org.apache.juddi.v3.tck.TckSecurity;
import org.apache.juddi.v3.tck.TckTModel;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.DispositionReport;
import org.uddi.repl_v3.ChangeRecord;
import org.uddi.repl_v3.ChangeRecordIDType;
import org.uddi.repl_v3.CommunicationGraph;
import org.uddi.repl_v3.DoPing;
import org.uddi.repl_v3.HighWaterMarkVectorType;
import org.uddi.repl_v3.ReplicationConfiguration;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDIReplicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 *
 * @author Alex O'Ree
 */
public class API_160_ReplicationTest {

        private static Log logger = LogFactory.getLog(API_141_JIRATest.class);
        static UDDIReplicationPortType repl = new UDDIReplicationImpl();
        static JUDDIApiImpl juddi = new JUDDIApiImpl();
        static ReplicationNotifier notifier = null;
        protected static String authInfoJoe = null;
        protected static String authInfoMary = null;
        protected static String authInfoSam = null;
        protected static String authInfoRoot = null;
        protected static String authInfoUDDI = null;

        @AfterClass
        public static void stopManager() throws ConfigurationException {
                Registry.stop();
        }

        @BeforeClass
        public static void startManager() throws ConfigurationException {
                Registry.start();

                try {

                        UDDISecurityPortType security = new UDDISecurityImpl();
                        authInfoRoot = TckSecurity.getAuthToken(security, TckPublisher.getRootPublisherId(), TckPublisher.getRootPassword());

                } catch (RemoteException e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("Could not obtain authInfo token.");
                }

        }

        @Test
        public void getPing() throws Exception {
                String node = repl.doPing(new DoPing());
                Assert.assertNotNull(node);
        }

        @Test
        public void getGetHighWaterMarks() throws Exception {
                List<ChangeRecordIDType> highWaterMarks = repl.getHighWaterMarks();

                Assert.assertNotNull(highWaterMarks);
                Assert.assertFalse(highWaterMarks.isEmpty());
                for (int i = 0; i < highWaterMarks.size(); i++) {
                        Assert.assertNotNull(highWaterMarks.get(i).getNodeID());
                        //If the highest originatingUSN for a specific node within the registry is not known, then the responding node MUST return a highWaterMark for that node with an originatingUSN of 0 (zero).
                        Assert.assertNotNull(highWaterMarks.get(i).getOriginatingUSN());
                        Assert.assertTrue(highWaterMarks.get(i).getOriginatingUSN() >= 0);

                        /* if (highWaterMarks.get(i).getOriginatingUSN() > 0) {
                         List<ChangeRecord> changeRecords = repl.getChangeRecords("test", null, BigInteger.valueOf(highWaterMarks.get(i).getOriginatingUSN()), null);
                         Assert.assertNotNull(changeRecords);
                         Assert.assertFalse(changeRecords.isEmpty());
                                
                         }*/
                }
        }

        @Test(expected = FatalErrorException.class)
        public void getChangeRecordsInvalid() throws DispositionReportFaultMessage, RemoteException {
                List<ChangeRecordIDType> highWaterMarks = repl.getHighWaterMarks();

                HighWaterMarkVectorType highWaterMarkVectorType = new HighWaterMarkVectorType();
                highWaterMarkVectorType.getHighWaterMark().add(highWaterMarks.get(0));
                List<ChangeRecord> changeRecords = repl.getChangeRecords("test", null, BigInteger.valueOf(highWaterMarks.get(0).getOriginatingUSN()), highWaterMarkVectorType);
                Assert.fail("unexpected success");
        }

        /**
         * add a clerk and node, delete the node, then try to access the clerk.
         * it should have been deleted
         *
         * @throws Exception
         */
        @Test
        public void testAddClerkNodeThenDelete() throws Exception {
                SaveClerk sc = new SaveClerk();
                sc.setAuthInfo(authInfoRoot);
                Clerk c = new Clerk();
                c.setName("clerk1");

                c.setPassword("pass");
                c.setPublisher("username");
                c.setNode(new Node());
                c.getNode().setName("test_node");
                c.getNode().setClientName("test_client");
                c.getNode().setProxyTransport(org.apache.juddi.v3.client.transport.JAXWSTransport.class.getCanonicalName());
                c.getNode().setCustodyTransferUrl("http://localhost");
                c.getNode().setDescription("http://localhost");
                c.getNode().setInquiryUrl("http://localhost");
                c.getNode().setPublishUrl("http://localhost");
                c.getNode().setReplicationUrl("http://localhost");
                c.getNode().setSecurityUrl("http://localhost");
                c.getNode().setSubscriptionListenerUrl("http://localhost");
                c.getNode().setSubscriptionUrl("http://localhost");

                sc.getClerk().add(c);
                juddi.saveClerk(sc);

                juddi.deleteClerk(new DeleteClerk(authInfoRoot, "clerk1"));

                ClerkList allNodes = juddi.getAllClerks(authInfoRoot);
                boolean found = false;
                for (int i = 0; i < allNodes.getClerk().size(); i++) {
                        if (allNodes.getClerk().get(i).getName().equals("clerk1")) {
                                found = true;
                        }

                }
                Assert.assertFalse(found);

                NodeList allNodes1 = juddi.getAllNodes(authInfoRoot);
                for (int i = 0; i < allNodes1.getNode().size(); i++) {
                        if (allNodes1.getNode().get(i).getName().equals("test_node")) {
                                return;
                        }
                }
                //TODO revise cascade deletes on nodes and clerks
                Assert.fail("node unexpected deleted");
        }

        @Test
        public void testAddClerkExistingNode() throws Exception {

                SaveClerk sc = new SaveClerk();
                sc.setAuthInfo(authInfoRoot);
                Clerk c = new Clerk();
                c.setName("clerk1");

                c.setPassword("pass");
                c.setPublisher("username");
                c.setNode(new Node());
                c.getNode().setName("test_node");
                c.getNode().setClientName("test_client");
                c.getNode().setProxyTransport(org.apache.juddi.v3.client.transport.JAXWSTransport.class.getCanonicalName());
                c.getNode().setCustodyTransferUrl("http://localhost");
                c.getNode().setDescription("http://localhost");
                c.getNode().setInquiryUrl("http://localhost");
                c.getNode().setPublishUrl("http://localhost");
                c.getNode().setReplicationUrl("http://localhost");
                c.getNode().setSecurityUrl("http://localhost");
                c.getNode().setSubscriptionListenerUrl("http://localhost");
                c.getNode().setSubscriptionUrl("http://localhost");
                sc.getClerk().add(c);

                SaveNode saveNode = new SaveNode();
                saveNode.setAuthInfo(authInfoRoot);
                saveNode.getNode().add(c.getNode());

                juddi.saveNode(saveNode);

                juddi.saveClerk(sc);
                //delete it
                juddi.deleteClerk(new DeleteClerk(authInfoRoot, "clerk1"));

                juddi.deleteNode(new DeleteNode(authInfoRoot, "test_node"));
                //confirm it's gone
                NodeList allNodes = juddi.getAllNodes(authInfoRoot);
                boolean found = false;
                for (int i = 0; i < allNodes.getNode().size(); i++) {
                        if (allNodes.getNode().get(i).getName().equals("test_node")) {
                                found = true;
                        }

                }
                Assert.assertFalse(found);
        }

        @Test
        public void setReplicationConfig() throws Exception {

                Node node = new Node();
                node.setName("test_node");
                node.setClientName("test_client");
                node.setProxyTransport(org.apache.juddi.v3.client.transport.JAXWSTransport.class.getCanonicalName());
                node.setCustodyTransferUrl("http://localhost");
                node.setDescription("http://localhost");
                node.setInquiryUrl("http://localhost");
                node.setPublishUrl("http://localhost");
                node.setReplicationUrl("http://localhost");
                node.setSecurityUrl("http://localhost");
                node.setSubscriptionListenerUrl("http://localhost");
                node.setSubscriptionUrl("http://localhost");

                SaveNode saveNode = new SaveNode();
                saveNode.setAuthInfo(authInfoRoot);
                saveNode.getNode().add(node);

                juddi.saveNode(saveNode);

                ReplicationConfiguration r = new ReplicationConfiguration();
                r.setCommunicationGraph(new CommunicationGraph());
              //  r.getCommunicationGraph().getEdge().add(new CommunicationGraph.Edge());
                r.getCommunicationGraph().getNode().add("test_node");

                DispositionReport setReplicationNodes = juddi.setReplicationNodes(authInfoRoot, r);

                ReplicationConfiguration replicationNodes = juddi.getReplicationNodes(authInfoRoot);
                Assert.assertNotNull(replicationNodes.getCommunicationGraph());
                Assert.assertNotNull(replicationNodes.getCommunicationGraph().getNode());
                Assert.assertEquals("test_node", replicationNodes.getCommunicationGraph().getNode().get(0));
                Assert.assertNotNull(replicationNodes.getMaximumTimeToGetChanges());
                Assert.assertNotNull(replicationNodes.getMaximumTimeToSyncRegistry());
                Assert.assertNotNull(replicationNodes.getTimeOfConfigurationUpdate());
                Assert.assertNotNull(replicationNodes.getSerialNumber());

        }
}
