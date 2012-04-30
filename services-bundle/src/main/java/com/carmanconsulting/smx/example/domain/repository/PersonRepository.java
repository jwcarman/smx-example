package com.carmanconsulting.smx.example.domain.repository;

import com.carmanconsulting.smx.example.domain.entity.Person;
import org.domdrides.repository.PageableRepository;

public interface PersonRepository extends PageableRepository<Person,String>
{
}
