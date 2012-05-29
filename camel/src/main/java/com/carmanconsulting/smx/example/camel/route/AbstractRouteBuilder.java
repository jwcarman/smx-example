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

import com.carmanconsulting.smx.example.camel.exception.AuditException;
import com.carmanconsulting.smx.example.camel.payload.JaxbSerializer;
import com.carmanconsulting.smx.example.camel.payload.PayloadSerializer;
import com.carmanconsulting.smx.example.camel.payload.StringEchoSerializer;
import com.carmanconsulting.smx.example.camel.payload.XStreamSerializer;
import com.carmanconsulting.smx.example.domain.entity.Activity;
import com.carmanconsulting.smx.example.domain.entity.BusinessProcess;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public abstract class AbstractRouteBuilder extends RouteBuilder implements Service
{
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    public static final String HEADER_ACTIVITY_ID = "activity_id";
    public static final String HEADER_PARENT_ACTIVITY_ID = "parent_activity_id";
    public static final String HEADER_PROCESS_ID = "process_id";

    public static final int DEFAULT_MAX_REDELIVERIES = 2;
    public static final int DEFAULT_REDELIVERY_DELAY = 1000;
    public static final String DEFAULT_DEAD_LETTER_URI = "jms:queue:dlc";
    public static final String DEFAULT_AUDIT_URI = "jms:queue:audit";

    private String inputUri;
    private String outputUri;
    private String auditUri = DEFAULT_AUDIT_URI;
    private String deadLetterUri = DEFAULT_DEAD_LETTER_URI;
    private int maxRedeliveries = DEFAULT_MAX_REDELIVERIES;
    private long redeliveryDelay = DEFAULT_REDELIVERY_DELAY;
    private ProducerTemplate auditProducerTemplate;

//----------------------------------------------------------------------------------------------------------------------
// Abstract Methods
//----------------------------------------------------------------------------------------------------------------------

    protected abstract void configureRoutes();

//----------------------------------------------------------------------------------------------------------------------
// Service Implementation
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public void start() throws Exception
    {
        this.auditProducerTemplate = getContext().createProducerTemplate();
    }

    @Override
    public void stop() throws Exception
    {
        auditProducerTemplate.stop();
    }

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

    protected void auditActivity(Activity activity)
    {
        auditProducerTemplate.asyncSendBody(getAuditUri(), activity);
    }

    protected BeginBusinessProcessProcessor beginProcess()
    {
        return new BeginBusinessProcessProcessor();
    }

    protected void toDeadLetterOnly(Class<? extends Exception> exceptionTypes)
    {
        onException(exceptionTypes).to(getDeadLetterUri());
    }

    @Override
    public final void configure() throws Exception
    {
        getContext().addService(this);
        interceptFrom().process(new InjectActivityIds());
        configureErrorHandling();
        configureRoutes();
    }

    protected void configureErrorHandling()
    {
        toDeadLetterOnly(AuditException.class);
        onException()
                .maximumRedeliveries(getMaxRedeliveries())
                .redeliveryDelay(getRedeliveryDelay())
                .handled(true)
                .to("log:errors?level=ERROR&showAll=true&multiline=true")
                .choice()
                    .when(header(HEADER_PROCESS_ID).isNotNull())
                        .process(new InjectActivityIds())
                        .process(new ResumeBusinessProcessProcessor())
                    .otherwise()
                        .log("Unable to audit error message.  No business process id found.")
                .end()
                .process(new SwapInMessageProcessor())
                .to(getDeadLetterUri());
    }

    protected String createUniqueId()
    {
        return UUID.randomUUID().toString();
    }

    protected RouteDefinition fromDefault()
    {
        return from(getInputUri());
    }

    public List<PayloadSerializer> getPayloadSerializers()
    {
        return Arrays.asList(new StringEchoSerializer(), new JaxbSerializer(), new XStreamSerializer());
    }

    protected ResumeBusinessProcessProcessor resumeProcess()
    {
        return new ResumeBusinessProcessProcessor();
    }

//----------------------------------------------------------------------------------------------------------------------
// Inner Classes
//----------------------------------------------------------------------------------------------------------------------

    protected class BeginBusinessProcessProcessor extends RecordActivityProcessor
    {
        private String processType;

        @Override
        protected Activity buildActivity(Exchange exchange)
        {
            Activity activity = super.buildActivity(exchange);
            final BusinessProcess businessProcess = new BusinessProcess();
            businessProcess.setType(processType);
            businessProcess.setBegin(activity.getTimestamp());
            businessProcess.addActivity(activity);
            exchange.getIn().setHeader(HEADER_PROCESS_ID, businessProcess.getId());
            return activity;
        }

        public BeginBusinessProcessProcessor processType(String type)
        {
            this.processType = type;
            return this;
        }

        @Override
        public BeginBusinessProcessProcessor activityName(Expression expression)
        {
            super.activityName(expression);
            return this;
        }
    }

    private class InjectActivityIds implements Processor
    {
        @Override
        public void process(Exchange exchange) throws Exception
        {
            String parentActivityId = exchange.getIn().getHeader(HEADER_ACTIVITY_ID, String.class);
            if (parentActivityId != null)
            {
                exchange.getIn().setHeader(HEADER_PARENT_ACTIVITY_ID, parentActivityId);
            }
            exchange.getIn().setHeader(HEADER_ACTIVITY_ID, createUniqueId());
        }
    }

    protected class RecordActivityProcessor implements Processor
    {
        private Expression activityNameExpression = constant("");

        @Override
        public final void process(Exchange exchange) throws Exception
        {
            Activity activity = buildActivity(exchange);
            auditActivity(activity);
        }

        protected Activity buildActivity(Exchange exchange)
        {
            Activity activity = new Activity();
            final Date timestamp = exchange.getProperty(Exchange.CREATED_TIMESTAMP, Date.class);
            activity.setTimestamp(timestamp);
            activity.setName(activityNameExpression.evaluate(exchange, String.class));
            activity.setFromUri(exchange.getFromEndpoint().getEndpointUri());
            activity.setExchangePattern(exchange.getPattern().name());
            activity.setRouteId(exchange.getFromRouteId());
            activity.setId(exchange.getIn().getHeader(HEADER_ACTIVITY_ID, String.class));
            activity.setParentActivityId(exchange.getIn().getHeader(HEADER_PARENT_ACTIVITY_ID, String.class));
            activity.setPayload(serializePayload(exchange));
            Throwable exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
            if (exception != null)
            {
                activity.setError(exception);
                activity.setErrorUri(exchange.getProperty(Exchange.FAILURE_ENDPOINT, String.class));
            }
            return activity;
        }

        private String serializePayload(Exchange exchange)
        {
            final Object body = exchange.getIn().getBody();
            if (body == null)
            {
                return null;
            }
            for (PayloadSerializer serializer : getPayloadSerializers())
            {
                try
                {
                    String payload = serializer.getPayload(exchange);
                    if (payload != null)
                    {
                        return payload;
                    }
                }
                catch (RuntimeException e)
                {
                    // Do nothing...
                }
            }
            return String.valueOf(body);
        }

        public RecordActivityProcessor activityName(Expression expression)
        {
            this.activityNameExpression = expression;
            return this;
        }
    }

    protected class ResumeBusinessProcessProcessor extends RecordActivityProcessor
    {
        private Expression processIdExpression = header(HEADER_PROCESS_ID);

        public ResumeBusinessProcessProcessor processId(Expression expression)
        {
            this.processIdExpression = expression;
            return this;
        }

        @Override
        protected Activity buildActivity(Exchange exchange)
        {
            Activity activity = super.buildActivity(exchange);
            final String businessProcessId = processIdExpression.evaluate(exchange, String.class);
            if (businessProcessId == null)
            {
                throw new CamelExecutionException("Unable to continue business process.  No business process id found at " + processIdExpression + ".", exchange);
            }
            activity.setBusinessProcessId(businessProcessId);
            exchange.getIn().setHeader(HEADER_PROCESS_ID, businessProcessId);
            return activity;
        }

        @Override
        public ResumeBusinessProcessProcessor activityName(Expression expression)
        {
            super.activityName(expression);
            return this;
        }
    }

    protected class SwapInMessageProcessor implements Processor
    {
        @Override
        public void process(Exchange exchange) throws Exception
        {
            exchange.setIn(exchange.getUnitOfWork().getOriginalInMessage());
        }
    }
}
