package com.carmanconsulting.smx.example.domain.sproc;

import com.carmanconsulting.smx.example.domain.sproc.annotation.Param;
import com.carmanconsulting.smx.example.domain.sproc.annotation.ParamType;
import com.carmanconsulting.smx.example.domain.sproc.annotation.Sproc;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.*;

public class AnnotationDrivenStoredProcedure<T> extends StoredProcedure
{
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private static Map<String, Integer> SQL_TYPES_MAP = new HashMap<String, Integer>();
    private final List<ParamDefinition> paramDefinitions;

//----------------------------------------------------------------------------------------------------------------------
// Static Methods
//----------------------------------------------------------------------------------------------------------------------

    static
    {
        registerSqlType(String.class, Types.VARCHAR);
        registerSqlType(BigDecimal.class, Types.DECIMAL);
        registerSqlType(Boolean.class, Types.BOOLEAN);
        registerSqlType(Byte.class, Types.TINYINT);
        registerSqlType(Short.class, Types.SMALLINT);
        registerSqlType(Integer.class, Types.INTEGER);
        registerSqlType(Long.class, Types.BIGINT);
        registerSqlType(Float.class, Types.REAL);
        registerSqlType(Double.class, Types.DOUBLE);
        registerSqlType(Date.class, Types.DATE);
        registerSqlType(Time.class, Types.TIME);
        registerSqlType(Timestamp.class, Types.TIMESTAMP);
        registerSqlType(URL.class, Types.DATALINK);
    }

    private static SqlParameter createSqlParameter(Field field, Param annotation)
    {
        final int sqlType = determineSqlType(field, annotation);
        final String paramName = field.getName();
        switch (annotation.type())
        {
            case IN:
                if (Param.DEFAULT_SCALE != annotation.scale())
                {
                    return new SqlParameter(paramName, sqlType, annotation.scale());
                }
                if (!Param.DEFAULT_TYPE_NAME.equals(annotation.typeName()))
                {
                    return new SqlParameter(paramName, sqlType, annotation.typeName());
                }
                return new SqlParameter(paramName, sqlType);
            case OUT:
            case RETURN:
                if (Param.DEFAULT_SCALE != annotation.scale())
                {
                    return new SqlOutParameter(paramName, sqlType, annotation.scale());
                }
                if (!Param.DEFAULT_TYPE_NAME.equals(annotation.typeName()))
                {
                    return new SqlOutParameter(paramName, sqlType, annotation.typeName());
                }
                return new SqlOutParameter(paramName, sqlType);
            case IN_OUT:
                if (Param.DEFAULT_SCALE != annotation.scale())
                {
                    return new SqlInOutParameter(paramName, sqlType, annotation.scale());
                }
                if (!Param.DEFAULT_TYPE_NAME.equals(annotation.typeName()))
                {
                    return new SqlInOutParameter(paramName, sqlType, annotation.typeName());
                }
                return new SqlInOutParameter(paramName, sqlType);
            default:
                throw new IllegalArgumentException("Unsupported parameter type " + annotation.type().name() + ".");
        }
    }

    private static int determineSqlType(Field field, Param annotation)
    {
        if (Types.NULL != annotation.sqlType())
        {
            return annotation.sqlType();
        }
        else
        {
            Integer sqlType = SQL_TYPES_MAP.get(getCanonicalName(field.getType()));
            if (sqlType == null)
            {
                throw new IllegalArgumentException("No default SQL type mapping for field " + field.getName() + " of type " + getCanonicalName(field.getType()) + ".");
            }
            return sqlType;
        }
    }

    private static Field getAccessibleField(Object target, String fieldName)
    {
        try
        {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        }
        catch (NoSuchFieldException e)
        {
            throw new IllegalArgumentException("No such field " + fieldName + " exists (should never happen).", e);
        }
    }

    private static String getCanonicalName(Class<?> javaType)
    {
        return ClassUtils.getPackageName(javaType) + "." + ClassUtils.getShortCanonicalName(javaType);
    }

    private static void registerSqlType(Class<?> javaType, int sqlType)
    {
        SQL_TYPES_MAP.put(getCanonicalName(javaType), sqlType);
        Class<?> wrapper = ClassUtils.primitiveToWrapper(javaType);
        Class<?> primitive = ClassUtils.wrapperToPrimitive(javaType);
        if (wrapper != null && !wrapper.equals(javaType))
        {
            SQL_TYPES_MAP.put(getCanonicalName(wrapper), sqlType);
        }
        else if (primitive != null && !primitive.equals(javaType))
        {
            SQL_TYPES_MAP.put(getCanonicalName(primitive), sqlType);
        }
    }

//----------------------------------------------------------------------------------------------------------------------
// Constructors
//----------------------------------------------------------------------------------------------------------------------

    public AnnotationDrivenStoredProcedure(Class<T> beanType, DataSource dataSource)
    {
        super(dataSource, extractName(beanType));
        this.paramDefinitions = extractParameterDefinitions(beanType);
        for (ParamDefinition paramDefinition : paramDefinitions)
        {
            declareParameter(paramDefinition.getParameter());
            if (paramDefinition.isReturnValue())
            {
                setFunction(true);
            }
        }
        compile();
    }

    private static String extractName(Class<?> beanType)
    {
        Sproc sproc = beanType.getAnnotation(Sproc.class);
        return StringUtils.isEmpty(sproc.name()) ? ClassUtils.getShortClassName(beanType) : sproc.name();
    }

    private static List<ParamDefinition> extractParameterDefinitions(Class<?> beanType)
    {
        Field[] fields = beanType.getDeclaredFields();
        final List<ParamDefinition> definitions = new LinkedList<ParamDefinition>();
        for (Field field : fields)
        {
            Param paramAnnotation = field.getAnnotation(Param.class);
            if (paramAnnotation != null)
            {
                definitions.add(new ParamDefinition(createSqlParameter(field, paramAnnotation), paramAnnotation.index(), paramAnnotation.type()));
            }
        }
        Collections.sort(definitions);
        return definitions;
    }

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    public T execute(T bean)
    {
        Map<String, Object> results = execute(extractParameters(bean));
        populateResults(bean, results);
        return bean;
    }

    private Map<String, Object> extractParameters(T bean)
    {
        final Map<String, Object> params = new HashMap<String, Object>(paramDefinitions.size() * 2);

        for (ParamDefinition paramDefinition : paramDefinitions)
        {
            if (paramDefinition.getParameter().isInputValueProvided())
            {
                String name = paramDefinition.getParameter().getName();
                params.put(name, getFieldValue(bean, name));
            }
        }
        return params;
    }

    private static Object getFieldValue(Object target, String fieldName)
    {
        try
        {
            return getAccessibleField(target, fieldName).get(target);
        }
        catch (IllegalAccessException e)
        {
            throw new IllegalArgumentException("Unable to extract value of field " + fieldName + ".", e);
        }
    }

    private void populateResults(T bean, Map<String, Object> results)
    {
        for (ParamDefinition paramDefinition : paramDefinitions)
        {
            String name = paramDefinition.getParameter().getName();
            Object value = results.get(name);
            if (value != null)
            {
                setFieldValue(bean, name, value);
            }

        }
    }

    private static void setFieldValue(Object target, String fieldName, Object value)
    {
        try
        {
            getAccessibleField(target, fieldName).set(target, value);
        }
        catch (IllegalAccessException e)
        {
            throw new IllegalArgumentException("Unable to access field " + fieldName + ".", e);
        }
    }

//----------------------------------------------------------------------------------------------------------------------
// Inner Classes
//----------------------------------------------------------------------------------------------------------------------

    private static class ParamDefinition implements Comparable<ParamDefinition>
    {
        private final SqlParameter parameter;
        private final int index;
        private final ParamType paramType;

        private ParamDefinition(SqlParameter parameter, int index, ParamType paramType)
        {
            this.parameter = parameter;
            this.index = index;
            this.paramType = paramType;
        }

        public boolean isReturnValue()
        {
            return ParamType.RETURN.equals(paramType);
        }

        @Override
        public int compareTo(ParamDefinition o)
        {
            return isReturnValue() ? -1 : this.index - o.index;
        }

        public SqlParameter getParameter()
        {
            return parameter;
        }

        public String toString()
        {
            return parameter.getName() + " (" + paramType.name() + ")";
        }
    }
}
