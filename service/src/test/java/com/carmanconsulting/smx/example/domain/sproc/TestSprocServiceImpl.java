package com.carmanconsulting.smx.example.domain.sproc;

import com.carmanconsulting.smx.example.domain.sproc.annotation.Param;
import com.carmanconsulting.smx.example.domain.sproc.annotation.ParamType;
import com.carmanconsulting.smx.example.domain.sproc.annotation.Sproc;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.derby.jdbc.Driver40;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

public class TestSprocServiceImpl
{
//----------------------------------------------------------------------------------------------------------------------
// Static Methods
//----------------------------------------------------------------------------------------------------------------------

    public static String hello(String name)
    {
        return ("Hola, " + name + "!");
    }

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    @Test
    public void testSprocCall() throws Exception
    {
        final BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(Driver40.class.getName());
        /*dataSource.setUrl("jdbc:mysql://carman-ferb.dyndns.org/karaf");
        dataSource.setUsername("karaf");
        dataSource.setPassword("password");*/
        dataSource.setUrl("jdbc:derby:memory:"+ getClass().getName() + ";create=true");

        new JdbcTemplate(dataSource).update("create function hello\n" +
                "    (name varchar(50))\n" +
                "    returns varchar(50)\n" +
                "    language java parameter style java\n" +
                "    no sql\n" +
                "    external name '" + getClass().getName() + ".hello'");

        final SprocServiceImpl sprocService = new SprocServiceImpl();
        sprocService.setDataSource(dataSource);
        final ParametersBean bean = new ParametersBean();
        bean.setName("Jim");
        sprocService.executeSproc(bean);
        System.out.println(bean.getGreeting());
    }

//----------------------------------------------------------------------------------------------------------------------
// Inner Classes
//----------------------------------------------------------------------------------------------------------------------

    @Sproc(name = "hello")
    public static class ParametersBean
    {
        @Param(type = ParamType.RETURN)
        private String greeting;
        
        @Param
        private String name;

        public String getGreeting()
        {
            return greeting;
        }

        public void setGreeting(String greeting)
        {
            this.greeting = greeting;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }
    }
}
