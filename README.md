# Introduction 
TODO: Give a short introduction of your project. Let this section explain the objectives or the motivation behind this project. 

Price Calculator Service Layer is a support service layer for the NGN Price Calculator.
This service application will serve the web application with all data it needs to create a  
pricing offer for NGN's customers.

The goal for this application is to simplify the process in creating these pricing offers by using a more flexible and modern technology.

# Getting Started
TODO: Guide users through getting your code up and running on their own system. In this section you can talk about:
## 1. Installation process



### Java version 11 or above
Choose between OpenJDK or Oracles JavaJDK. Download and install it. Install version 11 or above.
### Maven for building
Download the latest version of Maven from https://maven.apache.org/download.cgi
Choose either the binary-tar.gz or -zip archive.

Follow the instructions https://maven.apache.org/install.html
### Local docker installation

### Docker container with Mariadb 10.2 or above
Log in to mariadb with:
```
mysql -u <username> -p<password>
```

In this command:

```-u``` specifies the username. <br/>
```-p``` specifies the password for the username. You have to input the password directly after the flag, no space.

<b>NB:</b> To execute the following commands you have to be <b>root</b> user. <br/>
When connected to the mariadb, execute the following commands:
```

The <i>docker-compose.yml</i> contains two services. The first is the application, and the second is the database.<br/>
It is advised to build the database before the application to prepear it before the application is beeing used.

To only build the database run the following command:

```
docker-compose up -d maria_db
```
In this command:
- -d the container will run in a detached state.


```

create database pk_poal_db_dev;

grant all privileges on pk_poal_db_dev.* TO 'admin'@'localhost' identified by 'admin1234';

grant all privileges on pk_poal_db_dev.* TO 'admin'@'%' identified by 'admin1234';

flush privileges;
```
### WSL2 for Windows users
For information on how to installing WSL2 for Windows with desired distro: https://learn.microsoft.com/en-us/windows/wsl/install

1. wsl --install
2. wls

## 2. Software dependencies
    - All dependencies is packaged in the Maven pom.xml file. External dependencies is a MariaDB server.
## 3. Latest releases
Latest release of this project is in the <i>develop</i> branch.
## 4. API references
### SAP API
The application is mainly communicating with SAP. It gets all the information it needs about, standard prices, customer, contactinformation etc. from here.

For more information about the usage of Sap APIs: [Integrasjon SAP til Hubspot](https://ping24.atlassian.net/wiki/spaces/DOI/pages/2276229128/Integrasjon+SAP+til+Hubspot)


# Build and Test
TODO: Describe and show how to build your code and run the tests. 

Building the service application:
```
mvn clean install -P<profile-name>
```

Available profiles:
 - prod - Building application for production.
 - dev - Local development of the application. <b>NB!</b>Active by default if no other profile is given.
 - docker-dev - Profile for building dockerized application for local development.
 - itest - 
 - test - Used when running tests. Uses a H2 in memmory database for testing.

Running the application:
```
mvn spring-boot:run
```
No need for profile here, since profiles is only for setting values during build.
## Local development

# Contribute
TODO: Explain how other users and developers can contribute to make your code better. 

If you want to learn more about creating good readme files then refer the following [guidelines](https://docs.microsoft.com/en-us/azure/devops/repos/git/create-a-readme?view=azure-devops). You can also seek inspiration from the below readme files:
- [ASP.NET Core](https://github.com/aspnet/Home)
- [Visual Studio Code](https://github.com/Microsoft/vscode)
- [Chakra Core](https://github.com/Microsoft/ChakraCore)