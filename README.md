# ing-webshop
Webshop basics assignment #1

## Prerequisites / Requirements
Needed for building and running the application:
* [Java 11](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
* [Maven](https://maven.apache.org/)
* [Git](https://git-scm.com/)
* [Docker](https://docs.docker.com/)


## Runing the application locally
Below are instructions for downloading this app and running it locally.

### Setting up the environment / Starting the app
* **Clone repository.**
    To clone repository, open terminal in choosen directory and enter:
    ```
    $ git clone https://github.com/jelenabole/ing-webshop
    ```
* **Create postgres image.**
    In the downloaded directory, open docker folder and run a dockerfile to create and run
    PostgreSQL server container. To do all of that, run these commands (enter names inside <> brackets)
    ``` 
    $ cd ing-webshop/docker
    $ docker build -t <webshop-image> .
    $ docker run -d -p 5432:5432 --name <webshop-container> <webshop-image>
    ```
* **Configure preload data.**
    The application can preload some test data in the DB at the beginning. To do that, before
    running the application, in *'resources/application.properties'* file, change ddl-auto value to "create"
    (or "create-drop") and init-mode value from "never" to "always", like this:
   ```
   spring.jpa.hibernate.ddl-auto = create
   spring.datasource.initialization-mode = always
   ```
* **Run the application.**
    There are several ways to run a Spring Boot application on your local machine. One way is to execute the `main()`
    method in the package `com.ingemark.webshop.WebshopApplication` class from your IDE.
    Alternatively, Spring Boot Maven plugin can be used: `mvn spring-boot:run`
* **Check api documentation**
    After running the application, API documentation and examples of usage can be seen on
    this location (assumming the application port isn't changed):
    ```
    localhost:8080/swagger-ui/index.html
    ```

#### Notes
* End-2-end tests use wiremock to mock external service, here: 
[`com.ingemark.webshop.end2end.OrderControllerE2ETest`](https://github.com/jelenabole/ing-webshop/blob/main/src/test/java/com/ingemark/webshop/end2end/OrderControllerEnd2endTest.java)

## Running built app
To start the application, JAR file is available in /lib directory, from which it can be
started with the command `java -jar  ./webshop-0.0.1-SNAPSHOT.jar`

## Used in this project
* [Spring boot](https://spring.io/projects/spring-boot)
* [Swagger](https://swagger.io/)
* [Slf4j and Logback](http://www.slf4j.org/)
* [JUnit](https://junit.org/junit5/)
* [AssertJ](https://assertj.github.io/doc/)
* [Wiremock](http://wiremock.org/)