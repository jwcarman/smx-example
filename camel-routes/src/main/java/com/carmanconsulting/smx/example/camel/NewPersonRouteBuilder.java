package com.carmanconsulting.smx.example.camel;

import com.carmanconsulting.smx.example.domain.entity.Person;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Service;

@Service
public class NewPersonRouteBuilder extends RouteBuilder
{
//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public void configure() throws Exception
    {
        from("timer://createPerson?delay=3000&period=2000")
                .transacted()
                .process(new Processor()
                {

                    @Override
                    public void process(Exchange exchange) throws Exception
                    {
                        final Person p = new Person();
                        p.setFirstName("Slappy");
                        p.setLastName("White");
                        exchange.getIn().setBody(p);
                    }
                })
                .to("bean:personRepository?method=add")
                .end();
    }
}
