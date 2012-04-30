package com.carmanconsulting.smx.example.jta;

import org.hibernate.transaction.JNDITransactionManagerLookup;

public class OsgiTransactionManagerLookup extends JNDITransactionManagerLookup
{
//----------------------------------------------------------------------------------------------------------------------
// TransactionManagerLookup Implementation
//----------------------------------------------------------------------------------------------------------------------


    @Override
    public String getUserTransactionName()
    {
        return "osgi:services/javax.transaction.UserTransaction";
    }

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    @Override
    protected String getName()
    {
        return "osgi:services/javax.transaction.TransactionManager";
    }
}
