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

import com.carmanconsulting.smx.example.camel.exception.BamException;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.WireTapDefinition;

import java.util.UUID;

public abstract class AbstractRouteBuilder extends RouteBuilder
{
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    public static final String BAM_PROCESS_TYPE_HEADER = "bam_process_type";
    public static final String BAM_PROCESS_ID_HEADER = "bam_process_id";
    public static final String BAM_ACTIVITY_ID_HEADER = "bam_activity_id";
    public static final String BAM_PARENT_ACTIVITY_ID_HEADER = "bam_parent_activity_id";

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
    private final Processor snapshotProcessor;

//----------------------------------------------------------------------------------------------------------------------
// Constructors
//----------------------------------------------------------------------------------------------------------------------

    protected AbstractRouteBuilder()
    {
        snapshotProcessor = new SnapshotProcessor();
    }

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

    protected RouteDefinition beginFrom(String uri, String businessProcessType)
    {
        return from(uri).process(new BeginBusinessProcessProcessor(businessProcessType)).wireTap(getAuditUri()).end();
    }

    @Override
    public final void configure()
    {
        interceptFrom().process(new ManageActivityIdsProcessor());
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

    protected String createUniqueId()
    {
        return UUID.randomUUID().toString();
    }

    protected RouteDefinition fromDefault()
    {
        return from(getInputUri());
    }

    protected RouteDefinition resumeFrom(String uri)
    {
        return from(uri).process(new ResumeBusinessProcessProcessor()).wireTap(getAuditUri()).end();
    }

    protected RouteDefinition resumeFrom(String uri, Expression expression)
    {
        return from(uri).process(new ResumeBusinessProcessProcessor(expression)).wireTap(getAuditUri()).end();
    }

    protected Processor snapshot()
    {
        return snapshotProcessor;
    }

//----------------------------------------------------------------------------------------------------------------------
// Inner Classes
//----------------------------------------------------------------------------------------------------------------------

    private class BeginBusinessProcessProcessor implements Processor
    {
        private final String businessProcessType;

        private BeginBusinessProcessProcessor(String businessProcessType)
        {
            this.businessProcessType = businessProcessType;
        }

        @Override
        public void process(Exchange exchange) throws Exception
        {
            exchange.getIn().setHeader(BAM_PROCESS_ID_HEADER, createUniqueId());
            exchange.getIn().getHeader(BAM_PROCESS_TYPE_HEADER, businessProcessType);
        }
    }

    private class ManageActivityIdsProcessor implements Processor
    {
        @Override
        public void process(Exchange exchange) throws Exception
        {
            String parentActivityId = exchange.getIn().getHeader(BAM_ACTIVITY_ID_HEADER, String.class);
            if (parentActivityId != null)
            {
                exchange.getIn().setHeader(BAM_PARENT_ACTIVITY_ID_HEADER, parentActivityId);
            }
            exchange.getIn().setHeader(BAM_ACTIVITY_ID_HEADER, createUniqueId());
        }
    }

    private class ResumeBusinessProcessProcessor implements Processor
    {
        private Expression businessProcessIdExpression;

        private ResumeBusinessProcessProcessor()
        {
            this(header(BAM_PROCESS_ID_HEADER));
        }

        private ResumeBusinessProcessProcessor(Expression businessProcessIdExpression)
        {
            this.businessProcessIdExpression = businessProcessIdExpression;
        }

        @Override
        public void process(Exchange exchange) throws Exception
        {
            String businessProcessId = businessProcessIdExpression.evaluate(exchange, String.class);
            if (businessProcessId == null)
            {
                throw new BamException("Unable to resume business process.  Business process id not found using expression " + businessProcessIdExpression + ".");
            }
            exchange.getIn().setHeader(BAM_PROCESS_ID_HEADER, businessProcessId);
        }
    }

    private class SnapshotProcessor implements Processor, Service
    {
        private ProducerTemplate producerTemplate;

        @Override
        public void process(Exchange exchange) throws Exception
        {
            producerTemplate.asyncSend(getAuditUri(), exchange.copy());
        }

        @Override
        public void start() throws Exception
        {
            this.producerTemplate = getContext().createProducerTemplate();
        }

        @Override
        public void stop() throws Exception
        {
            this.producerTemplate.stop();
        }
    }
}
