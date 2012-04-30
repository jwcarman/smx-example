package com.carmanconsulting.smx.example.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Service;

@Service
public class PersonPollingRouteBuilder extends RouteBuilder
{
    @Override
    public void configure() throws Exception
    {
        from("timer://personTimer?delay=3000&period=10000")
                .transacted()
                .to("bean:personRepository?method=getAll")
                .split().body()
                .to("log:people");
    }
}
