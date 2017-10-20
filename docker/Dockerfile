FROM tomcat:8-jre8
MAINTAINER Jon Freer <freerjm@miamioh.edu>

#copies the .war file into the tomcat /webapps/ folder.
COPY target/*.war /usr/local/tomcat/webapps/

#starts up tomcat.
RUN $CATALINA_HOME/bin/startup.sh
