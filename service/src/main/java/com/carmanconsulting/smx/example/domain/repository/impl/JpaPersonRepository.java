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
}
