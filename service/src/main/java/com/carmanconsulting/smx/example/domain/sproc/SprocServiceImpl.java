package com.carmanconsulting.smx.example.domain.sproc;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SprocServiceImpl implements SprocService
{
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private DataSource dataSource;
    private final Map<String, AnnotationDrivenStoredProcedure<?>> compiledSprocs = new ConcurrentHashMap<String, AnnotationDrivenStoredProcedure<?>>(20);

//----------------------------------------------------------------------------------------------------------------------
// SprocService Implementation
//----------------------------------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public <T> T executeSproc(T parameters)
    {
        String key = parameters.getClass().getName();
        AnnotationDrivenStoredProcedure<T> sproc = (AnnotationDrivenStoredProcedure<T>) compiledSprocs.get(key);
        if (sproc == null)
        {
            sproc = new AnnotationDrivenStoredProcedure<T>((Class<T>) parameters.getClass(), dataSource);
            compiledSprocs.put(key, sproc);
        }
        return sproc.execute(parameters);
    }

//----------------------------------------------------------------------------------------------------------------------
// Getter/Setter Methods
//----------------------------------------------------------------------------------------------------------------------

    public void setDataSource(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }
}
