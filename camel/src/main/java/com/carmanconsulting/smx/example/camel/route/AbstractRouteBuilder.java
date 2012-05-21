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

public abstract class AbstractRouteBuilder extends RouteBuilder
{
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    public static final int DEFAULT_MAX_REDELIVERIES = 3;
    public static final int DEFAULT_REDELIVERY_DELAY = 10000;
    public static final String DEFAULT_DLC_URI = "jms:queue:dlc";

//----------------------------------------------------------------------------------------------------------------------
// Abstract Methods
//----------------------------------------------------------------------------------------------------------------------

    protected abstract void configureRoutes();

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

    protected String getDeadLetterUri()
    {
        return DEFAULT_DLC_URI;
    }

    protected int getMaxRedeliveries()
    {
        return DEFAULT_MAX_REDELIVERIES;
    }

    protected long getRedeliveryDelay()
    {
        return DEFAULT_REDELIVERY_DELAY;
    }
}
