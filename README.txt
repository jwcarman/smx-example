1.  Execute the following in your ServiceMix/FuseESB console:

features:install jndi jpa transaction
install -s mvn:com.h2database/h2/1.3.166
install -s mvn:commons-dbcp/commons-dbcp/1.4
install -s mvn:org.apache.commons/commons-lang3/3.0
install -s mvn:org.springframework/spring-jdbc/3.0.5.RELEASE
install -s mvn:org.springframework/spring-orm/3.0.5.RELEASE
install -s mvn:org.jboss.javassist/com.springsource.javassist/3.12.1.GA
install -s mvn:org.objectweb.asm/com.springsource.org.objectweb.asm/3.1.0
install -s mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.dom4j/1.6.1_3
install -s mvn:net.sourceforge.cglib/com.springsource.net.sf.cglib/2.2.0
install -s mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.antlr/2.7.7_5

2.  Drop the h2database.xml file (after you tweak it to your liking) into $KARAF_HOME/deploy.

3.  Execute the following in your ServiceMix/FuseESB console:

install -s mvn:com.carmanconsulting.smx/hibernate-bundle/1.0.0-SNAPSHOT
install -s mvn:com.carmanconsulting.smx/services-bundle/1.0.0-SNAPSHOT
install -s mvn:com.carmanconsulting.smx/camel-routes/1.0.0-SNAPSHOT