@echo off
rem java -cp ../juddi-tomcat/target/tomcat/apache-tomcat-6.0.26/webapps/juddiv3/WEB-INF/lib/juddi-core-3.2.0-SNAPSHOT.jar;../juddi-tomcat/target/tomcat/apache-tomcat-6.0.26/webapps/juddiv3/WEB-INF/lib/uddi-ws-3.2.0-SNAPSHOT.jar;../juddi-tomcat/target/tomcat/apache-tomcat-6.0.26/webapps/juddiv3/WEB-INF/lib/commons-logging-api-1.1.jar;../juddi-tomcat/target/tomcat/apache-tomcat-6.0.26/webapps/juddiv3/WEB-INF/lib/commons-configuration-1.6.jar;../juddi-tomcat/target/tomcat/apache-tomcat-6.0.26/webapps/juddiv3/WEB-INF/lib/commons-lang-2.4.jar;../juddi-tomcat/target/tomcat/apache-tomcat-6.0.26/webapps/juddiv3/WEB-INF/lib/commons-collections-3.2.1.jar;../juddi-tomcat/target/tomcat/apache-tomcat-6.0.26/webapps/juddiv3/WEB-INF/lib/commons-codec-1.3.jar;../juddi-tomcat/target/tomcat/apache-tomcat-6.0.26/webapps/juddiv3/WEB-INF/classes/* org.apache.juddi.v3.auth.CrytorUtil org.apache.juddi.cryptor.DefaultCryptor

rem java -cp ../juddi-tomcat/target/tomcat/apache-tomcat-6.0.26/webapps/juddi-gui/WEB-INF/lib;../juddi-tomcat/target/tomcat/apache-tomcat-6.0.26/webapps/juddi-gui/WEB-INF/lib/*;../juddi-tomcat/target/tomcat/apache-tomcat-6.0.26/lib/* org.apache.juddi.v3.auth.CrytorUtil org.apache.juddi.cryptor.DefaultCryptor

rem cd ../juddi-tomcat/target/tomcat/apache-tomcat-6.0.26/webapps/juddiv3/WEB-INF/lib
java -cp ../juddi-tomcat/target/tomcat/apache-tomcat-6.0.26/webapps/juddiv3/WEB-INF/lib;../juddi-tomcat/target/tomcat/apache-tomcat-6.0.26/webapps/juddiv3/WEB-INF/lib/* org.apache.juddi.v3.auth.CrytorUtil %*