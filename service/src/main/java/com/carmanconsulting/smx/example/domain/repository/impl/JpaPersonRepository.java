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

package com.carmanconsulting.smx.example.domain.repository.impl;

import com.carmanconsulting.smx.example.domain.entity.Person;
import com.carmanconsulting.smx.example.domain.repository.PersonRepository;

public class JpaPersonRepository extends AbstractJpaRepository<Person,String> implements PersonRepository
{
//----------------------------------------------------------------------------------------------------------------------
// Constructors
//----------------------------------------------------------------------------------------------------------------------

    public JpaPersonRepository()
    {
        super(Person.class);
    }

//----------------------------------------------------------------------------------------------------------------------
// Repository Implementation
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public Person add(Person entity)
    {
        if(size() > 5)
        {
            throw new RuntimeException("Person repository is getting too large!");
        }
        return super.add(entity);
    }
}
