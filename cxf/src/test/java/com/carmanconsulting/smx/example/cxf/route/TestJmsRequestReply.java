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

package com.carmanconsulting.smx.example.cxf.route;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class TestJmsRequestReply extends CamelTestSupport
{
    @Override
    protected CamelContext createCamelContext() throws Exception
    {
        final CamelContext camelContext = super.createCamelContext();
        camelContext.addComponent("jms", ActiveMQComponent.activeMQComponent("vm://" + getClass().getSimpleName() + "?broker.persistent=false"));
        return camelContext;
    }

    @Produce(uri = "jms:queue:a")
    private ProducerTemplate producer;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception
    {
        return new RouteBuilder()
        {
            @Override
            public void configure() throws Exception
            {
                from("jms:queue:a")
                        .to("log:received-a?showAll=true&multiline=true&level=INFO")
                        .transform(simple("${body}1"))
                        .inOut("jms:queue:b?replyTo=d");

                from("jms:queue:b?disableReplyTo=true")
                        .to("log:received-b?showAll=true&multiline=true&level=INFO")
                        .transform(simple("${body}2"))
                        .inOnly("jms:queue:c?preserveMessageQos=true");

                from("jms:queue:c")
                        .to("log:received-c?showAll=true&multiline=true&level=INFO")
                        .transform(simple("${body}3"));
            }
        };
    }

    @Test
    public void testRequestReply()
    {
        String reply = (String) producer.sendBody("jms:queue:a", ExchangePattern.InOut, "Hello");
        System.out.println("The reply is " + reply);
    }

}
