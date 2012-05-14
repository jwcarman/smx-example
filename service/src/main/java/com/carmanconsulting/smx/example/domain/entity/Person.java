package com.carmanconsulting.smx.example.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class Person implements org.domdrides.entity.Entity<String>
{
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    @Id
    private String id = UUID.randomUUID().toString();

    private String firstName;
    private String lastName;

//----------------------------------------------------------------------------------------------------------------------
// Entity Implementation
//----------------------------------------------------------------------------------------------------------------------

    public String getId()
    {
        return id;
    }

//----------------------------------------------------------------------------------------------------------------------
// Getter/Setter Methods
//----------------------------------------------------------------------------------------------------------------------

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    protected void setId(String id)
    {
        this.id = id;
    }

//----------------------------------------------------------------------------------------------------------------------
// Canonical Methods
//----------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof Person))
        {
            return false;
        }

        Person person = (Person) o;

        if (id != null ? !id.equals(person.id) : person.id != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return id != null ? id.hashCode() : 0;
    }
}
