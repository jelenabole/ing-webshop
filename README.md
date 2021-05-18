# ing-webshop
Webshop basics assignment #1

### Notes
* End-2-end tests use wiremock to test external service, here: (wiremock example)
### Assumptions
* code is not autogenerated, rather inputted by a user


## Prerequisites / Requirements
Needed for building and running the application:
* [Java 8+](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
* [Maven](https://maven.apache.org/)
* [Git](https://git-scm.com/)
* [Docker](https://docs.docker.com/)


## Runing application locally
Below are instructions for downloading this app and running it locally.

### Setting up the environment / Starting the app
* **Clone repository.** To clone repository to your pc, open terminal in (desired) directory and enter:
    ```
    git clone https://github.com/jelenabole/ing-webshop
    ```
* **Create postgres image.** In the downloaded directory, open docker directory and run a dockerfile to create and run
    PostgreSQL server container. To do all of that, run these commands (names inside <> brackets can be changed)
    ``` 
    cd ing-webshop/docker
    $ docker build -t <webshop-image> .
    $ docker images                 // check if image is created
    $ docker run -d --name <webshop-container> -p 5432:5432 <webshop-image>
    $ docker ps                     // check if container is running container
  ```
* **Open application.** Open application in choosen IDE.
* **Configure preload data.** Application can preload some test data in the DB at the start. To do that, before
    running the application, in *'resources/application.properties'* file, change ddl-auto value to "create"
    (or "create-drop") and init-mode value from "never" to "always":
   ```
   spring.jpa.hibernate.ddl-auto = create
   spring.datasource.initialization-mode = always
   ```
* **Run the application.**
    There are several ways to run a Spring Boot application on your local machine. One way is to execute the `main()`
    method in the package `com.ingemark.webshop.WebshopApplication` class from your IDE.
    Alternatively, Spring Boot Maven plugin can be used: `mvn spring-boot:run`
* **Check api documentation** After running the application, API documentation and examples of usage can be seen on
    this location (assumming the application port isn't changed):
    ```
    localhost:8080/swagger-ui/index.html
    ```

## Use built application
To start the application, there is a JAR available in /lib directory.

## Used in this project
* [Spring boot](https://spring.io/projects/spring-boot)
* [Swagger](https://swagger.io/)
* [Slf4j and Logback](http://www.slf4j.org/)
* [JUnit](https://junit.org/junit5/), [AssertJ](https://assertj.github.io/doc/), [Wiremock](http://wiremock.org/)