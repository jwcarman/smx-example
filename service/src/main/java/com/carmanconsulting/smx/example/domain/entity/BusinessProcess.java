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

    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Activity> activities = new HashSet<Activity>();

//----------------------------------------------------------------------------------------------------------------------
// Getter/Setter Methods
//----------------------------------------------------------------------------------------------------------------------

    public Date getCreated()
    {
        return created;
    }

    public void setCreated(Date begin)
    {
        this.created = begin;
    }

    public Date getUpdated()
    {
        return updated;
    }

    public void setUpdated(Date lastActvity)
    {
        this.updated = lastActvity;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    public void addActivity(Activity activity)
    {
        activities.add(activity);
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
