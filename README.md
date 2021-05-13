# ing-webshop
Webshop basics assignment #1

## Prerequisites / Requirements
Needed for building and running the application:
* [Java 8+](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
* [Maven](https://maven.apache.org/)
* [Git](https://git-scm.com/)
* [Docker](https://docs.docker.com/)


## Runing application locally
Below are instructions for downloading this app and running it locally.

### Setting up the environment / Starting the app
* Create a postgres image from the Dockerfile and run PostgreSQL server container:
* Go into the /docker folder, and run these commands:
    ``` 
    $ docker build -t webshop-image .
    $ docker images  // see all images
    $ docker run -d --name <webshop-container> -p 5432:5432 webshop-image
    $ docker ps -a // check id of the container
    $ docker start <first-3-chars-of-id>
  ```
* Check if the container is running:
    ``` 
    $ docker ps
  ```
  
* Clone repository to your pc
    ```
    git clone
    ```
* Create database container from image, and run it
    ``` 
    Docker compose - create container from image, start container
    docker-compose run <image>
    ```
*  Build project with Maven (for dev) (DL all dependencies)
   ```
   
   ```
* Run the application
    There are several ways to run a Spring Boot application on your local machine. One way is to execute the main method in the de.codecentric.springbootsample.Application class from your IDE.
    
    Alternatively you can use the Spring Boot Maven plugin like so:
    
    mvn spring-boot:run

### Running tests
-- run tests --

### Using the application

* App is running on a port host:8080
* --postman methods-- for testing --here--
* This project uses Swagger for project documentation. After starting this project you can see all available endpoints at _host:port/swagger-ui/index.html_ where you can visualize and interact with the API's resources.
* You can check the logs at --location-- after the project starts.

## Use built application
To start the application, there is a JAR available in /lib directory.


## Used in this project
* Spring boot
* Swagger for documenting API
* --Logging-- for loging code
* [JUnit](https://junit.org/junit5/), [AssertJ](https://assertj.github.io/doc/), [Wiremock](http://wiremock.org/) for unit testing