1.  Add the feature repository.  In the ${smx.home}/etc/org.apache.karaf.features.cfg file, modify the
featuresRepositories setting by appending:

    mvn:com.carmanconsulting.smx/com.carmanconsulting.smx.example/1.0.0-SNAPSHOT/xml/features

2.  Add boot features.  In the ${smx.home}/etc/org.apache.karaf.features.cfg file, modify the
featuresBoot setting by appending:

    camel-jms,jndi,jpa,commons-dbcp,org.apache.servicemix.bundles.serp,openjpa,com.carmanconsulting.smx.example.datasource,com.carmanconsulting.smx.example.service,com.carmanconsulting.smx.example.camel