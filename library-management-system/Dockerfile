# =============================================================================
#  Library Management System - Dockerfile (multi-stage build)
#  ----------------------------------------------------------------------------
#  Stage 1  builds the WAR with Maven and JDK 17.
#  Stage 2  drops the WAR into Apache Tomcat 10.1 as the ROOT application,
#           so the site is served directly at http://localhost:8080/
#
#  Build and run:
#      docker build -t library-management-system .
#      docker run -p 8080:8080 \
#        -e DB_URL="jdbc:mysql://host.docker.internal:3306/library_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" \
#        -e DB_USER=lms_user -e DB_PASSWORD=lms_pass \
#        library-management-system
#
#  Usually you will use docker-compose.yml instead (app + MySQL together).
# =============================================================================

# ----------------------------- stage 1: build -------------------------------
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /build

# Copy the POM first: Docker caches this layer, so dependencies are only
# downloaded again when pom.xml really changes.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Now the sources, then compile + test + package.
COPY src ./src
RUN mvn -B clean package

# ----------------------------- stage 2: runtime -----------------------------
FROM tomcat:10.1-jdk17-temurin

# Remove Tomcat's default sample applications.
RUN rm -rf /usr/local/tomcat/webapps/*

# Deploy the WAR as ROOT.war -> application available at "/"
COPY --from=build /build/target/library-management-system.war \
     /usr/local/tomcat/webapps/ROOT.war

# Database settings are injected as environment variables (see AppConfig).
# Defaults keep a local MySQL on the host working, but compose overrides them.
ENV DB_URL="jdbc:mysql://db:3306/library_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8" \
    DB_USER=lms_user \
    DB_PASSWORD=lms_pass

EXPOSE 8080

# Cataline is started in the foreground (container stays alive).
# If the hosting platform provides a $PORT variable (Render, Railway, ...) the
# HTTP connector port is rewritten before start-up, otherwise 8080 is used.
CMD ["sh", "-c", "if [ -n \"$PORT\" ]; then sed -i \"s/port=\\\"8080\\\"/port=\\\"$PORT\\\"/\" \"$CATALINA_BASE/conf/server.xml\"; fi; exec catalina.sh run"]
