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

package org.apache.juddi.keygen;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.Property;
import org.apache.juddi.error.ErrorMessage;
import org.apache.juddi.error.FatalErrorException;
import org.apache.juddi.uuidgen.UUIDGenFactory;
import org.apache.juddi.uuidgen.UUIDGen;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * The default jUDDI key generator.  Generates a key like this:
 * 
 * uddiScheme : RootDomain : UUID
 * 
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class DefaultKeyGenerator implements KeyGenerator {

	public String generate() throws DispositionReportFaultMessage {
		String rootDomain = "";
		try 
		{ rootDomain = AppConfig.getConfiguration().getString(Property.JUDDI_ROOT_DOMAIN); }
		catch(ConfigurationException ce) 
		{ throw new FatalErrorException(new ErrorMessage("errors.configuration.Retrieval", Property.JUDDI_ROOT_DOMAIN));}
		
		UUIDGen uuidgen = UUIDGenFactory.getUUIDGen();
		return UDDI_SCHEME + PARTITION_SEPARATOR + rootDomain + PARTITION_SEPARATOR + uuidgen.uuidgen();
	}
}