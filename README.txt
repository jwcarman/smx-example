1.  Execute the following in your ServiceMix/FuseESB console:

features:addulr mvn:com.carmanconsulting.smx/com.carmanconsulting.smx.example/1.0.0-SNAPSHOT/xml/features
features:install camel-jms jndi jpa commons-dbcp org.apache.servicemix.bundles.serp openjpa com.carmanconsulting.smx.example.datasource com.carmanconsulting.smx.example.service com.carmanconsulting.smx.example.camel