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

import org.domdrides.entity.Entity;
import org.domdrides.repository.PageableRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractJpaRepository<EntityType extends Entity<IdType>, IdType extends Serializable> implements PageableRepository<EntityType, IdType>
{
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private EntityManager entityManager;
    private final Class<EntityType> entityClass;

//----------------------------------------------------------------------------------------------------------------------
// Constructors
//----------------------------------------------------------------------------------------------------------------------

    protected AbstractJpaRepository(Class<EntityType> entityClass)
    {
        this.entityClass = entityClass;
    }

//----------------------------------------------------------------------------------------------------------------------
// PageableRepository Implementation
//----------------------------------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public List<EntityType> list(final int first, final int max, final String sortProperty, final boolean ascending)
    {
        final String jpaql = "select x from " + entityClass.getName() + " x order by x." + sortProperty +
                ( ascending ? " asc" : " desc" );
        final Query query = entityManager.createQuery(jpaql);
        query.setFirstResult(first).setMaxResults(max);
        return query.getResultList();
    }

//----------------------------------------------------------------------------------------------------------------------
// Repository Implementation
//----------------------------------------------------------------------------------------------------------------------

    public EntityType add(EntityType entity)
    {
        entityManager.persist(entity);
        return entity;
    }

    public boolean contains(EntityType entity)
    {
        return getById(entity.getId()) != null;
    }

    @SuppressWarnings("unchecked")
    public Set<EntityType> getAll()
    {
        final String jpaql = "select x from " + entityClass.getName() + " x";
        return queryForSet(jpaql);
    }

    public EntityType getById(IdType id)
    {
        return entityManager.find(entityClass, id);
    }

    public void remove(EntityType entity)
    {
        entityManager.remove(entity);
    }

    public int size()
    {
        List results = entityManager.createQuery("select count(x) from " + entityClass.getName() + " x").getResultList();
        return ((Number)results.get(0)).intValue();
    }

    public EntityType update(EntityType entity)
    {
        return entityManager.merge(entity);
    }

//----------------------------------------------------------------------------------------------------------------------
// Getter/Setter Methods
//----------------------------------------------------------------------------------------------------------------------

    protected Class<EntityType> getEntityClass()
    {
        return entityClass;
    }

    protected EntityManager getEntityManager()
    {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private Set<EntityType> queryForSet(String jpaql)
    {
        return new HashSet<EntityType>(entityManager.createQuery(jpaql).getResultList());
    }
}