package com.carmanconsulting.smx.example.domain.sproc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.Types;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Param
{
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    public static int DEFAULT_SCALE = -1;
    public static int DEFAULT_INDEX = Integer.MAX_VALUE;
    public static String DEFAULT_TYPE_NAME = "";

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    int index() default DEFAULT_INDEX;

    int scale() default DEFAULT_SCALE;

    int sqlType() default Types.NULL;

    ParamType type() default ParamType.IN;

    String typeName() default DEFAULT_TYPE_NAME;
}
