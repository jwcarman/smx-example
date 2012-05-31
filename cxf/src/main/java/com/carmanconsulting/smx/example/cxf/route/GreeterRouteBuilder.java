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

import com.carmanconsulting.schema.smx.greeting.GreetRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class GreeterRouteBuilder extends RouteBuilder
{
    @Override
    public void configure() throws Exception
    {
        from("cxf:bean:greeterService")
                .to("log:greeter.originalInput?showAll=true&multiline=true&level=INFO")
                .transform(simple("in.body[0]"))
                .marshal().jaxb(GreetRequest.class.getPackage().getName())
                .to("jms:queue:greetingInput?replyTo=greetingOutput&receiveTimeout=250").threads(5)
                .to("log:greeter.resultingOutput?showAll=true&multiline=true&level=INFO")
                .process(new Processor()
                {
                    @Override
                    public void process(Exchange exchange) throws Exception
                    {
                        System.out.println("Processing exchange with body of type " + exchange.getIn().getBody().getClass().getName() + "...");
                    }
                });

        from("jms:queue:greetingInput")
                .to("log:greeter.fromQueue?showAll=true&multiline=true&level=INFO")
                .unmarshal().jaxb(GreetRequest.class.getPackage().getName())
                .transform(simple("Hello, ${in.body.name}!"));
    }
}
