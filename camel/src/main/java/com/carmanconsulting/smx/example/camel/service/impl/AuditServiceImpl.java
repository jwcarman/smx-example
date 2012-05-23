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

package com.carmanconsulting.smx.example.camel.service.impl;

import com.carmanconsulting.smx.example.camel.exception.BamException;
import com.carmanconsulting.smx.example.camel.service.AuditService;
import com.carmanconsulting.smx.example.domain.entity.Activity;
import com.carmanconsulting.smx.example.domain.entity.BusinessProcess;
import com.carmanconsulting.smx.example.domain.repository.BusinessProcessRepository;
import com.thoughtworks.xstream.XStream;
import org.apache.camel.Exchange;
import org.apache.camel.model.dataformat.XStreamDataFormat;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.Date;

public class AuditServiceImpl implements AuditService
{
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(AuditServiceImpl.class);
    private BusinessProcessRepository businessProcessRepository;

//----------------------------------------------------------------------------------------------------------------------
// AuditService Implementation
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public void auditExchange(Exchange exchange)
    {
        final Date now = new Date();
        String bpId = exchange.getIn().getHeader(BAM_PROCESS_ID_HEADER, String.class);
        BusinessProcess businessProcess = businessProcessRepository.getById(bpId);
        if (businessProcess == null)
        {
            final String name = exchange.getIn().getHeader(BAM_PROCESS_TYPE_HEADER, String.class);

            businessProcess = new BusinessProcess();
            businessProcess.setId(bpId);
            businessProcess.setCreated(now);
            businessProcess.setUpdated(now);
            businessProcess.setName(name);
            businessProcessRepository.add(businessProcess);
        }
        Activity activity = new Activity();
        final String activityId = exchange.getIn().getHeader(BAM_ACTIVITY_ID_HEADER, String.class);
        activity.setId(activityId);
        activity.setTimestamp(now);
        activity.setPayload(toPayload(exchange));
        activity.setFromUri(exchange.getFromEndpoint().getEndpointUri());
        activity.setRouteId(exchange.getFromRouteId());
        activity.setExchangePattern(exchange.getPattern().name());
        final String parentActivityId = exchange.getIn().getHeader(BAM_PARENT_ACTIVITY_ID_HEADER, String.class);
        if (parentActivityId != null)
        {
            activity.setParentActivity(businessProcess.findActivityById(parentActivityId));
        }
        Throwable t = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
        if (t != null)
        {
            final String fullStackTrace = ExceptionUtils.getFullStackTrace(t);
            log.warn("Received exception\n{}", fullStackTrace);
            activity.setErrorDetails(fullStackTrace);
            activity.setErrorMessage(t.toString());
            activity.setErrorUri(exchange.getProperty(Exchange.FAILURE_ENDPOINT, String.class));
        }
        businessProcess.addActivity(activity);
        businessProcessRepository.update(businessProcess);
    }

    private String toPayload(Exchange exchange)
    {
        Object o = exchange.getIn().getBody();
        if (o == null)
        {
            return "";
        }
        if (o instanceof String)
        {
            return String.valueOf(o);
        }
        if (o.getClass().isAnnotationPresent(XmlRootElement.class))
        {
            final StringWriter sw = new StringWriter();
            JAXB.marshal(o, sw);
            return sw.toString();
        }

        XStream xstream = new XStream();
        return xstream.toXML(o);
    }
//----------------------------------------------------------------------------------------------------------------------
// Getter/Setter Methods
//----------------------------------------------------------------------------------------------------------------------

    public BusinessProcessRepository getBusinessProcessRepository()
    {
        return businessProcessRepository;
    }

    public void setBusinessProcessRepository(BusinessProcessRepository businessProcessRepository)
    {
        this.businessProcessRepository = businessProcessRepository;
    }
}
