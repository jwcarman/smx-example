package com.carmanconsulting.smx.example.camel.route;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.spring.SpringRouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.springframework.jms.connection.JmsTransactionManager;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;

public class TestPurgatoryIdea extends CamelTestSupport
{
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    public static final String INPUT_URI = "jms:queue:input";
    public static final String OUTPUT_URI = "mock:output";
    public static final String PURGATORY_URI = "jms:purgatory";
    public static final String PURGATORY_RETURN_SLIP = "purgatory-return-slip";
    public static final String PURGATORY_TIMEOUT = "purgatory-timeout";
    public static final String PURGATORY_COUNT = "purgatory-count";

    @Produce(uri = INPUT_URI)
    private ProducerTemplate producerTemplate;

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    @Override
    protected CamelContext createCamelContext() throws Exception
    {
        SimpleRegistry registry = new SimpleRegistry();
        CamelContext context = new DefaultCamelContext(registry);
        context.setLazyLoadTypeConverters(isLazyLoadingTypeConverter());
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://" + getClass().getName() + "?broker.persistent=false&broker.useJmx=false");
        JmsTransactionManager jmsTransactionManager = new JmsTransactionManager(connectionFactory);
        registry.put("transactionManager", jmsTransactionManager);
        ActiveMQComponent jmsComponent = new ActiveMQComponent();
        jmsComponent.setCacheLevelName("CACHE_CONSUMER");
        jmsComponent.setTransactionManager(jmsTransactionManager);
        jmsComponent.setConnectionFactory(connectionFactory);
        jmsComponent.setTransacted(true);
        context.addComponent("jms", jmsComponent);
        return context;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception
    {
        return new PurgatoryRouteBuilder();
    }

    @Test
    public void testNastyMessageGoesToPurgatory() throws Exception
    {
        MockEndpoint purgatory = getMockEndpoint("mock:dlc");
        purgatory.setExpectedMessageCount(1);
        producerTemplate.sendBody("nasty");
        purgatory.await(20, TimeUnit.SECONDS);
        assertMockEndpointsSatisfied();
    }

//----------------------------------------------------------------------------------------------------------------------
// Inner Classes
//----------------------------------------------------------------------------------------------------------------------

    private static class PurgatoryRouteBuilder extends SpringRouteBuilder
    {
        private Set<String> blackList = Collections.synchronizedSet(new TreeSet<String>());

        @Override
        public void configure() throws Exception
        {
            onException()
                    .transacted()
                    .process(new BlacklistProcessor());
            interceptFrom()
                    .when(isBlackListed())
                    .transacted()
                    .choice()
                        .when(header(PURGATORY_COUNT).isGreaterThanOrEqualTo(3))
                            .log("Sending message to DLC as it has exceeded purgatory count...")
                            .to("mock:dlc")
                        .otherwise()
                            .log("Sending message to purgatory...")
                            .setHeader(PURGATORY_RETURN_SLIP, header(Exchange.INTERCEPTED_ENDPOINT))
                            .setHeader(PURGATORY_TIMEOUT, timeout(2, TimeUnit.SECONDS))
                            .to(PURGATORY_URI)
                    .end()
                    .stop();
            from(INPUT_URI)
                    .transacted()
                    .choice()
                        .when(body().isEqualTo("nasty"))
                            .throwException(new RuntimeException("Nope!"))
                        .otherwise()
                            .to(OUTPUT_URI);
            from(PURGATORY_URI)
                    .transacted()
                    .throttle(5)
                    .choice()
                        .when(isTimedOut())
                            .log("Returning message to ${in.header.purgatory-return-slip} from purgatory...")
                            .process(purgatoryCount())
                            .recipientList().header(PURGATORY_RETURN_SLIP).end()
                        .otherwise()
                            .to(PURGATORY_URI);
        }

        private Processor purgatoryCount()
        {
            return new Processor()
            {
                @Override
                public void process(Exchange exchange) throws Exception
                {
                    Integer count = exchange.getIn().getHeader(PURGATORY_COUNT, Integer.class);
                    exchange.getIn().setHeader(PURGATORY_COUNT, count == null ? Integer.valueOf(1) : Integer.valueOf(count + 1));
                }
            };
        }

        private Expression timeout(final long duration, final TimeUnit timeUnit)
        {
            return new Expression()
            {
                @Override
                @SuppressWarnings("unchecked")
                public <T> T evaluate(Exchange exchange, Class<T> type)
                {
                    Object value = System.currentTimeMillis() + timeUnit.toMillis(duration);
                    return (T) value;
                }
            };
        }

        private Predicate isTimedOut()
        {
            return new Predicate()
            {
                @Override
                public boolean matches(Exchange exchange)
                {
                    Long timeout = exchange.getIn().getHeader(PURGATORY_TIMEOUT, Long.class);
                    return timeout == null || timeout <= System.currentTimeMillis();
                }
            };
        }

        private Predicate isBlackListed()
        {
            return new Predicate()
            {
                @Override
                public boolean matches(Exchange exchange)
                {
                    return blackList.remove(exchange.getIn().getMessageId());
                }
            };
        }

        private class BlacklistProcessor implements Processor
        {
            @Override
            public void process(Exchange exchange) throws Exception
            {
                log.debug("Blacklisting message {}...", exchange.getIn().getMessageId());
                blackList.add(exchange.getIn().getMessageId());
            }
        }
    }
}
