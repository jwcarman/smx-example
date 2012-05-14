package com.carmanconsulting.smx.example.camel;

import org.apache.camel.builder.RouteBuilder;

public class PersonPollingRouteBuilder extends RouteBuilder
{
    @Override
    public void configure() throws Exception
    {
        from("timer://personPolling?delay=3000&period=10000")
                .transacted()
                .to("bean:personRepository?method=getAll")
                .split().body()
                .to("jms:queue:people");

        from("jms:queue:people").to("log:people");
    }
}
