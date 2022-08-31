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
Choose between OpenJDK or Oracles JavaJDK. Download and install it.
### Maven for building
Download the latest version of Maven from https://maven.apache.org/download.cgi
Choose either the binary-tar.gz or -zip archive.

Follow the instructions https://maven.apache.org/install.html
### Local docker installation

### Docker container with Mariadb 10.2 or above
```
create database pk_poal_db_dev;

grant all privileges on pk_poal_db_dev.* TO 'admin'@'localhost' identified by 'admin1234';

grant all privileges on pk_poal_db_dev.* TO 'admin'@'%' identified by 'admin1234';

flush privileges;
```
### WSL2 for Windows users
## 2. Software dependencies
    - All dependencies is packaged in the Maven pom.xml file. External dependencies is a MariaDB server.
## 3. Latest releases
## 4. API references

# Build and Test
TODO: Describe and show how to build your code and run the tests. 
## Local development

# Contribute
TODO: Explain how other users and developers can contribute to make your code better. 

If you want to learn more about creating good readme files then refer the following [guidelines](https://docs.microsoft.com/en-us/azure/devops/repos/git/create-a-readme?view=azure-devops). You can also seek inspiration from the below readme files:
- [ASP.NET Core](https://github.com/aspnet/Home)
- [Visual Studio Code](https://github.com/Microsoft/vscode)
- [Chakra Core](https://github.com/Microsoft/ChakraCore)