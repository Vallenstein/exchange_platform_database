# exchange_platform_database
This repository is an addition to https://github.com/Vallenstein/exchange_platform_frontend 
Follow the steps on the other repository.
When asked to install the Spring API:

1. Install OpenJDK 10 or higher
2. install mariaDB and maven

3. create Database ilabexchange
4. create user with full access and edit application properties accordingly in project/src/main/resources/application.properties

5. switch to project and run mvn package
6. start the server: java -jar target/backendAPI-0.0.1-SNAPSHOT.jar
