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
import java.util.HashSet;
import java.util.Set;

@Entity
public class BusinessProcess extends BaseEntity
{
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private String type;

    @Temporal(TemporalType.TIMESTAMP)
    private Date begin = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastActvity;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Activity> activities = new HashSet<Activity>();

//----------------------------------------------------------------------------------------------------------------------
// Getter/Setter Methods
//----------------------------------------------------------------------------------------------------------------------

    public Date getBegin()
    {
        return begin;
    }

    public void setBegin(Date begin)
    {
        this.begin = begin;
    }

    public Date getLastActvity()
    {
        return lastActvity;
    }

    public void setLastActvity(Date lastActvity)
    {
        this.lastActvity = lastActvity;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String name)
    {
        this.type = name;
    }

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    public void addActivity(Activity activity)
    {
        activities.add(activity);
        setLastActvity(activity.getTimestamp() == null ? new Date() : activity.getTimestamp());
        activity.setBusinessProcess(this);
    }

    public Activity findActivityById(String id)
    {
        for (Activity activity : activities)
        {
            if(id.equals(activity.getId()))
            {
                return activity;
            }
        }
        return null;
    }

    public void setId(String id)
    {
        super.setId(id);
    }
}
