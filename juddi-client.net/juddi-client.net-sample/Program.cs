﻿using net.java.dev.wadl;
/*
 * Copyright 2001-2008 The Apache Software Foundation.
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
 *
 */
using org.apache.juddi.v3.client;
using org.apache.juddi.v3.client.config;
using org.apache.juddi.v3.client.crypto;
using org.apache.juddi.v3.client.mapping;
using org.apache.juddi.v3.client.transport;
using org.uddi.apiv3;
using System;
using System.Collections.Generic;
using System.Security.Cryptography;
using System.Text;


namespace juddi_client.net_sample
{
    class Program
    {
        static void Main(string[] args)
        {
            AesManaged aes = new AesManaged();
            Console.WriteLine("AesManaged ");
            KeySizes[] ks = aes.LegalKeySizes;
            foreach (KeySizes k in ks)
            {
                Console.WriteLine("\tLegal min key size = " + k.MinSize);
                Console.WriteLine("\tLegal max key size = " + k.MaxSize);
            }
            ks = aes.LegalBlockSizes;
            foreach (KeySizes k in ks)
            {
                Console.WriteLine("\tLegal min block size = " + k.MinSize);
                Console.WriteLine("\tLegal max block size = " + k.MaxSize);
            }


            RijndaelManaged rij = new RijndaelManaged();
            Console.WriteLine("RijndaelManaged ");
            ks = rij.LegalKeySizes;
            foreach (KeySizes k in ks)
            {
                Console.WriteLine("\tLegal min key size = " + k.MinSize);
                Console.WriteLine("\tLegal max key size = " + k.MaxSize);
            }
            ks = rij.LegalBlockSizes;
            foreach (KeySizes k in ks)
            {
                Console.WriteLine("\tLegal min block size = " + k.MinSize);
                Console.WriteLine("\tLegal max block size = " + k.MaxSize);
            }
            TripleDESCryptoServiceProvider tsp = new TripleDESCryptoServiceProvider();
            Console.WriteLine("TripleDESCryptoServiceProvider ");
            ks = tsp.LegalKeySizes;
            foreach (KeySizes k in ks)
            {
                Console.WriteLine("\tLegal min key size = " + k.MinSize);
                Console.WriteLine("\tLegal max key size = " + k.MaxSize);
            }
            ks = tsp.LegalBlockSizes;
            foreach (KeySizes k in ks)
            {
                Console.WriteLine("\tLegal min block size = " + k.MinSize);
                Console.WriteLine("\tLegal max block size = " + k.MaxSize);
            }


            using (RijndaelManaged rijAlg = new RijndaelManaged())
            {
                rijAlg.KeySize = 256;
                rijAlg.BlockSize = 256;
                rijAlg.GenerateKey();
                rijAlg.GenerateIV();
                Console.Out.WriteLine( rijAlg.KeySize + " " + rijAlg.BlockSize + " " + Convert.ToBase64String(rijAlg.IV, Base64FormattingOptions.None) + " " +
                    Convert.ToBase64String(rijAlg.Key, Base64FormattingOptions.None));
            }


            TripleDESCryptoServiceProvider des = new TripleDESCryptoServiceProvider();
            des.KeySize = 192;
            des.BlockSize = 64;
            des.GenerateKey();
            des.GenerateIV();
            Console.Out.WriteLine(des.KeySize + " " + des.BlockSize + " " + Convert.ToBase64String(des.IV, Base64FormattingOptions.None) + " " +
                Convert.ToBase64String(des.Key, Base64FormattingOptions.None));


            UDDIClient clerkManager = null;
            Transport transport = null;
            UDDIClerk clerk = null;
            try
            {
                clerkManager = new UDDIClient("uddi.xml");
                UDDIClientContainer.addClient(clerkManager);

                transport = clerkManager.getTransport("default");

                org.uddi.apiv3.UDDI_Security_SoapBinding security = transport.getUDDISecurityService();
                org.uddi.apiv3.UDDI_Inquiry_SoapBinding inquiry = transport.getUDDIInquiryService();
                org.uddi.apiv3.UDDI_Publication_SoapBinding publish = transport.getUDDIPublishService();

                clerk = clerkManager.getClerk("default");


                find_business fb = new find_business();
                fb.authInfo = clerk.getAuthToken(security.Url);
                fb.findQualifiers = new string[] { UDDIConstants.APPROXIMATE_MATCH };
                fb.name = new name[1];
                fb.name[0] = new name(UDDIConstants.WILDCARD, "en");
                businessList bl = inquiry.find_business(fb);
                for (int i = 0; i < bl.businessInfos.Length; i++)
                {
                    Console.WriteLine(bl.businessInfos[i].name[0].Value);
                }


                //Wadl Import example

                application app = WADL2UDDI.ParseWadl("sample.wadl");
                List<Uri> urls = WADL2UDDI.GetBaseAddresses(app);
                Uri url = urls[0];
                String domain = url.Host;

                tModel keygen = UDDIClerk.createKeyGenator("uddi:" + domain + ":keygenerator", domain, "en");
                //save the keygen
                save_tModel stm = new save_tModel();
                stm.authInfo = clerk.getAuthToken(clerk.getUDDINode().getSecurityUrl());
                stm.tModel = new tModel[] { keygen };

                publish.save_tModel(stm);
                Properties properties = new Properties();

                properties.put("keyDomain", domain);
                properties.put("businessName", domain);
                properties.put("serverName", url.Host);
                properties.put("serverPort", url.Port.ToString());
                //wsdlURL = wsdlDefinition.getDocumentBaseURI();
                WADL2UDDI wadl2UDDI = new WADL2UDDI(clerk, properties);

                businessService businessServices = wadl2UDDI.createBusinessService(new QName("MyWasdl.namespace", "Servicename"), app);


                HashSet<tModel> portTypeTModels = wadl2UDDI.createWADLPortTypeTModels(url.ToString(), app);

                //When parsing a WSDL, there's really two things going on
                //1) convert a bunch of stuff (the portTypes) to tModels
                //2) convert the service definition to a BusinessService

                //Since the service depends on the tModel, we have to save the tModels first
                save_tModel tms = new save_tModel();
                tms.authInfo = clerk.getAuthToken(clerk.getUDDINode().getSecurityUrl());
                HashSet<tModel>.Enumerator it = portTypeTModels.GetEnumerator();
                List<tModel> ts = new List<tModel>();
                while (it.MoveNext())
                {
                    ts.Add(it.Current);
                }
                tModel[] tmodels = ts.ToArray();

                tms.tModel = tmodels;
                if (tms.tModel.Length > 0)
                    //important, you'll need to save your new tModels, or else saving the business/service may fail
                    publish.save_tModel(tms);



                //finaly, we're ready to save all of the services defined in the WSDL
                //again, we're creating a new business, if you have one already, look it up using the Inquiry getBusinessDetails




                save_business sb = new save_business();
                //  sb.setAuthInfo(rootAuthToken.getAuthInfo());
                businessEntity be = new businessEntity();
                be.businessKey = (businessServices.businessKey);
                //TODO, use some relevant here
                be.name = new name[] { new name(domain, "en") };


                be.businessServices = new businessService[] { businessServices };
                sb.authInfo = clerk.getAuthToken(clerk.getUDDINode().getSecurityUrl());
                sb.businessEntity = new businessEntity[] { be };
                publish.save_business(sb);
            }
            catch (Exception ex)
            {
                while (ex != null)
                {
                    System.Console.WriteLine("Error! " + ex.Message);
                    ex = ex.InnerException;
                }
            }
            finally
            {
                if (transport != null && transport is IDisposable)
                {
                    ((IDisposable)transport).Dispose();
                }
                if (clerk != null)
                    clerk.Dispose();
            }



            Console.WriteLine("Press any key to exit");
            Console.Read();
        }
    }
}
