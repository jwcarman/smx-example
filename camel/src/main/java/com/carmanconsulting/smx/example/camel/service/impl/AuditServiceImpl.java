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

import com.carmanconsulting.smx.example.camel.service.AuditService;
import com.carmanconsulting.smx.example.domain.entity.Activity;
import com.carmanconsulting.smx.example.domain.entity.BusinessProcess;
import com.carmanconsulting.smx.example.domain.repository.BusinessProcessRepository;
import org.apache.camel.Exchange;

import java.util.Date;

public class AuditServiceImpl implements AuditService
{
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

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
        if(businessProcess == null)
        {
            final String name = exchange.getIn().getHeader(BAM_PROCESS_TYPE_HEADER, String.class);

            businessProcess = new BusinessProcess();
            businessProcess.setId(bpId);
            businessProcess.setBegin(now);
            businessProcess.setLastActvity(now);
            businessProcess.setName(name);
            businessProcessRepository.add(businessProcess);
        }
        Activity activity = new Activity();
        final String activityId = exchange.getIn().getHeader(BAM_ACTIVITY_ID_HEADER, String.class);
        activity.setId(activityId);
        activity.setTimestamp(now);
        activity.setPayload(exchange.getIn().getBody(String.class));
        activity.setFromUri(exchange.getFromEndpoint().getEndpointUri());
        activity.setRouteId(exchange.getFromRouteId());
        activity.setExchangePattern(exchange.getPattern().name());
        final String parentActivityId = exchange.getIn().getHeader(BAM_PARENT_ACTIVITY_ID_HEADER, String.class);
        if(parentActivityId != null)
        {
            activity.setParentActivity(businessProcess.findActivityById(parentActivityId));
        }
        businessProcess.addActivity(activity);
        businessProcessRepository.update(businessProcess);
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
