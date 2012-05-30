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
