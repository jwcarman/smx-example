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

package com.carmanconsulting.smx.example.domain.entity;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Activity extends BaseEntity
{
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    private String name;
    @Lob
    private String payload;
    private String exchangePattern;
    private String fromUri;
    private String routeId;


    private String errorMessage;
    @Lob
    private String errorDetails;
    private String errorUri;
    @Transient
    private String parentActivityId;

    @Transient
    private String businessProcessId;


    @ManyToOne
    private Activity parentActivity;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private BusinessProcess businessProcess;

//----------------------------------------------------------------------------------------------------------------------
// Getter/Setter Methods
//----------------------------------------------------------------------------------------------------------------------

    public String toString()
    {
        final ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
        builder
                .append("businessProcess", businessProcess)
                .append("businessProcessId", businessProcessId)
                .append("exchangePattern", exchangePattern)
                .append("fromUri", fromUri)
                .append("parentActivity", parentActivity)
                .append("parentActivityId", parentActivityId)
                .append("payload", payload);
        if (errorMessage != null)
        {
            builder.append("errorUri", errorUri)
                    .append("errorMessage", errorMessage)
                    .append("errorDetails", errorDetails);
        }
        return builder.toString();
    }

    public BusinessProcess getBusinessProcess()
    {
        return businessProcess;
    }

    void setBusinessProcess(BusinessProcess businessProcess)
    {
        this.businessProcess = businessProcess;
    }

    public String getBusinessProcessId()
    {
        return businessProcessId;
    }

    public void setBusinessProcessId(String businessProcessId)
    {
        this.businessProcessId = businessProcessId;
    }

    public String getErrorDetails()
    {
        return errorDetails;
    }

    public void setErrorDetails(String errorDetails)
    {
        this.errorDetails = errorDetails;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getErrorUri()
    {
        return errorUri;
    }

    public void setErrorUri(String errorUri)
    {
        this.errorUri = errorUri;
    }

    public String getExchangePattern()
    {
        return exchangePattern;
    }

    public void setExchangePattern(String exchangePattern)
    {
        this.exchangePattern = exchangePattern;
    }

    public String getFromUri()
    {
        return fromUri;
    }

    public void setFromUri(String fromUri)
    {
        this.fromUri = fromUri;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Activity getParentActivity()
    {
        return parentActivity;
    }

    public void setParentActivity(Activity parentActivity)
    {
        this.parentActivity = parentActivity;
    }

    public String getParentActivityId()
    {
        return parentActivityId;
    }

    public void setParentActivityId(String parentActivityId)
    {
        this.parentActivityId = parentActivityId;
    }

    public String getPayload()
    {
        return payload;
    }

    public void setPayload(String payload)
    {
        this.payload = payload;
    }

    public String getRouteId()
    {
        return routeId;
    }

    public void setRouteId(String routeId)
    {
        this.routeId = routeId;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Date timestamp)
    {
        this.timestamp = timestamp;
    }

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    public void setError(Throwable t)
    {
        errorMessage = t.getMessage();
        errorDetails = ExceptionUtils.getFullStackTrace(t);
    }

    public void setId(String id)
    {
        super.setId(id);
    }
}
