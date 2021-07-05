FROM jboss/wildfly:9.0.0.Final

USER root

RUN yum -y update && \
    yum -y install sudo iproute vim curl && \
	yum clean all

ENV PATH /usr/local/tomee/bin:$PATH

RUN mkdir -p /opt/appdynamics

RUN chown -R jboss:jboss /opt/appdynamics

ADD standalone.conf /opt/jboss/wildfly/bin/standalone.conf

ADD standalone.xml /opt/jboss/wildfly/standalone/configuration/standalone.xml

RUN mkdir -p /src

COPY bashrc /root/.bashrc

#adds our app code to the deployment scanner
ADD java-services.war /opt/jboss/wildfly/standalone/deployments/

RUN mkdir -p /loanLogs

EXPOSE 8080