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

    @Lob
    private String payload;

    @Lob
    private String errorDetails;

    @ManyToOne
    private Activity parentActivity;

    private String exchangePattern;
    private String fromUri;
    private String routeId;

//----------------------------------------------------------------------------------------------------------------------
// Getter/Setter Methods
//----------------------------------------------------------------------------------------------------------------------

    public String getErrorDetails()
    {
        return errorDetails;
    }

    public void setErrorDetails(String errorDetails)
    {
        this.errorDetails = errorDetails;
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

    public Activity getParentActivity()
    {
        return parentActivity;
    }

    public void setParentActivity(Activity parentActivity)
    {
        this.parentActivity = parentActivity;
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

    public void setId(String id)
    {
        super.setId(id);
    }
}
