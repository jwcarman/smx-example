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

package com.carmanconsulting.smx.example.camel.route;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.testng.CamelTestSupport;


public class AbstractRouteBuilderTest extends CamelTestSupport
{
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    public static final String INPUT_URI = "direct:input";
    public static final String OUTPUT_URI = "mock:output";
    public static final String AUDIT_URI = "mock:audit";
    public static final String DEAD_LETTER_URI = "mock:dlc";
    public static final int MAX_REDELIVERIES = 1;
    public static final int REDELIVERY_DELAY = 100;

    @Produce(uri = INPUT_URI)
    private ProducerTemplate inputProducerTemplate;

//----------------------------------------------------------------------------------------------------------------------
// Getter/Setter Methods
//----------------------------------------------------------------------------------------------------------------------

    public ProducerTemplate getInputProducerTemplate()
    {
        return inputProducerTemplate;
    }

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    protected <R extends AbstractRouteBuilder> R configure(R routeBuilder)
    {
        routeBuilder.setInputUri(INPUT_URI);
        routeBuilder.setOutputUri(OUTPUT_URI);
        routeBuilder.setAuditUri(AUDIT_URI);
        routeBuilder.setDeadLetterUri(DEAD_LETTER_URI);
        routeBuilder.setMaxRedeliveries(MAX_REDELIVERIES);
        routeBuilder.setRedeliveryDelay(REDELIVERY_DELAY);
        return routeBuilder;
    }
}
