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

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

public abstract class AbstractRouteBuilder extends RouteBuilder
{
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    public static final int DEFAULT_MAX_REDELIVERIES = 3;
    public static final int DEFAULT_REDELIVERY_DELAY = 10000;
    public static final String DEFAULT_DEAD_LETTER_URI = "jms:queue:dlc";
    public static final String DEFAULT_AUDIT_URI = "jms:queue:audit";

    private String inputUri;
    private String outputUri;
    private String auditUri = DEFAULT_AUDIT_URI;
    private String deadLetterUri = DEFAULT_DEAD_LETTER_URI;
    private int maxRedeliveries = DEFAULT_MAX_REDELIVERIES;
    private long redeliveryDelay = DEFAULT_REDELIVERY_DELAY;

//----------------------------------------------------------------------------------------------------------------------
// Abstract Methods
//----------------------------------------------------------------------------------------------------------------------

    protected abstract void configureRoutes();

//----------------------------------------------------------------------------------------------------------------------
// Getter/Setter Methods
//----------------------------------------------------------------------------------------------------------------------

    public String getAuditUri()
    {
        return auditUri;
    }

    public void setAuditUri(String auditUri)
    {
        this.auditUri = auditUri;
    }

    public String getDeadLetterUri()
    {
        return deadLetterUri;
    }

    public void setDeadLetterUri(String deadLetterUri)
    {
        this.deadLetterUri = deadLetterUri;
    }

    public String getInputUri()
    {
        return inputUri;
    }

    public void setInputUri(String inputUri)
    {
        this.inputUri = inputUri;
    }

    public int getMaxRedeliveries()
    {
        return maxRedeliveries;
    }

    public void setMaxRedeliveries(int maxRedeliveries)
    {
        this.maxRedeliveries = maxRedeliveries;
    }

    public String getOutputUri()
    {
        return outputUri;
    }

    public void setOutputUri(String outputUri)
    {
        this.outputUri = outputUri;
    }

    public long getRedeliveryDelay()
    {
        return redeliveryDelay;
    }

    public void setRedeliveryDelay(long redeliveryDelay)
    {
        this.redeliveryDelay = redeliveryDelay;
    }

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public final void configure()
    {
        configureErrorHandling();
        configureRoutes();
    }

    protected void configureErrorHandling()
    {
        onException()
                .maximumRedeliveries(getMaxRedeliveries())
                .redeliveryDelay(getRedeliveryDelay())
                .to(getDeadLetterUri());
    }

    protected RouteDefinition fromDefault()
    {
        return from(getInputUri());
    }
}
