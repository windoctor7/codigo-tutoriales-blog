#Ascari Q. Romo - 9 Octubre 2022

FROM amazoncorretto:17.0.4-alpine as packager

# Primero creamos con jlink una JRE ligera con los módulos indispensables
RUN $JAVA_HOME/bin/jlink \
    --module-path $JAVA_HOME/jmods \
    --verbose \
    --add-modules java.base,java.desktop,java.instrument,java.management.rmi,java.naming,java.prefs,java.scripting,java.security.jgss,java.sql,jdk.httpserver,jdk.jfr,jdk.unsupported \
    --compress 2 \
    --no-header-files \
    --output /opt/jre-17-minimal

# Utilizamos una distro de linux ligera y agregamos nuestro microservicio
FROM alpine:latest
COPY --from=packager /opt/jre-17-minimal /opt/jre-17-minimal
COPY app/build/libs/app-0.1.0.jar /opt/

ENV JAVA_HOME=/opt/jre-17-minimal
ENV PATH="$PATH:$JAVA_HOME/bin"
EXPOSE 8080
CMD java -jar /opt/app-0.1.0.jar
