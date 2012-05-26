/*
 * Copyright (c) 2012 Carman Consulting, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.carmanconsulting.smx.example.camel.payload;

import org.apache.camel.Exchange;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.StringWriter;

public class JaxbSerializer implements PayloadSerializer
{
//----------------------------------------------------------------------------------------------------------------------
// PayloadSerializer Implementation
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public String getPayload(Exchange exchange)
    {
        Object body = exchange.getIn().getBody();
        if(body.getClass().isAnnotationPresent(XmlRootElement.class))
        {
            final StringWriter sw = new StringWriter();
            JAXB.marshal(body, sw);
            return sw.toString();
        }
        return null;
    }
}
