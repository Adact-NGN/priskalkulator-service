# Introduction 
Price Calculator Service Layer is a support service layer for the NGN Price Calculator.
This service application will serve the web application with all data it needs to create a  
pricing offer for NGN's customers.

The goal for this application is to simplify the process in creating these pricing offers by using a more flexible and modern technology.

# Getting Started

## Prerequisite

### WSL2 for Windows users
For information on how to installing WSL2 for Windows with desired distro: [WSL Install](https://learn.microsoft.com/en-us/windows/wsl/install)

As administrator in PoweShell rin the following command:
```
wsl --install
```

To install a specific distro: ```wsl --install -d <Distro Name>```

List available distros: ```wsl --list --online```

If more than one distro is installed, set the default distro: ```wsl --setdefault <Distro Name>```
List local distros: ```wsl -l -v```

### Upgrade from WSL1 to WSL2
New Linux installations, installed using the ```wsl --install``` command, will be set to WSL 2 by default.

To see whether your Linux distribution is set to WSL 1 or WSL 2, use the command: wsl -l -v.

To change versions, use the command: ```wsl --set-version <distro name> 2```

Ex: ```wsl --set-version Ubuntu-20.04 2```

### Azure CLI
1. Install the Azure CLI you need: [How to install Azure CLI](https://learn.microsoft.com/en-us/cli/azure/install-azure-cli)
2. Login with the client with, you will be prompted with a web page to complete the login: 
```
az login
```
### Kubernetes command-line tool (kubectl)
1. Install the command-line tool you need: [Install Tools](https://kubernetes.io/docs/tasks/tools/)
2. Connect kubectl to the AKS:
```
az aks get-credentials --resource-group <resource group name> --name <aks cluster name>
```
3. Test the connection with a kubectl command:
```
kubectl cluster-info
```
This will output the infmation for the connected cluster.

## 1. Installation process
To be able to start developing on the application, the following must be installed; Java JDK and Maven.
### Java version 11 or above
Choose between OpenJDK or Oracles JavaJDK. Download and install it. Install version 11 or above.
### Maven for building
Download the latest version of Maven from https://maven.apache.org/download.cgi
Choose either the binary-tar.gz or -zip archive.

Follow the instructions https://maven.apache.org/install.html
### Local docker installation
For local development docker is a handy tool to simulate real world running of the application.

Install docker: [Get docker](https://docs.docker.com/get-docker/)

Verify the docker installation:
```
docker -v
```
### Docker container with Mariadb 10.2 or above

Install mariadb with the following command or use the docker compose in the next section:

```
$ docker run --detach --name some-mariadb --env MARIADB_USER=example-user --env MARIADB_PASSWORD=my_cool_secret --env MARIADB_ROOT_PASSWORD=my-secret-pw  mariadb:latest
```

The <i>docker-compose.yml</i> contains two services. The first is the application, and the second is the database.<br/>
It is advised to build the database before the application to prepear it before the application is beeing used.

To only build the database run the following command:

```
docker-compose up -d maria_db
```
In this command:
- -d the container will run in a detached state.

Enter bash to make changes to mariadb:
```
docker exec -it <container name> bash
```

Log in to mariadb with:
```
mysql -u <username> -p<password>
```

In this command:

```-u``` specifies the username. <br/>
```-p``` specifies the password for the username. You have to input the password directly after the flag, no space.

<b>NB:</b> To execute the following commands you have to be <b>root</b> user. These where set during the creation of the container. <br/>
When connected to the mariadb, execute the following commands:
```
create database pk_poal_db_dev;

grant all privileges on pk_poal_db_dev.* TO 'admin'@'localhost' identified by 'admin1234';

grant all privileges on pk_poal_db_dev.* TO 'admin'@'%' identified by 'admin1234';

flush privileges;
```
<b>NB!</b> If a nother password and username where used for the root user, exchange these in the commands above.
## 2. Software dependencies
    - All dependencies is packaged in the Maven pom.xml file. External dependencies is a MariaDB server.
## 3. Latest releases
Latest release of this project is in the <i>develop</i> branch.
## 4. API references
### SAP API
The application is mainly communicating with SAP. It gets all the information it needs about, standard prices, customer, contactinformation etc. from here.

For more information about the usage of Sap APIs: [Integrasjon SAP til Hubspot](https://ping24.atlassian.net/wiki/spaces/DOI/pages/2276229128/Integrasjon+SAP+til+Hubspot)


# Build, Test and Debug
## Building
Building the service application:
```
mvn clean install -P <profile-name>
```

Available profiles:
 - prod - Building application for production.
 - dev - Local development of the application. <b>NB!</b> Active by default if no other profile is given.
 - docker-dev - Profile for building dockerized application for local development.
 - itest - 
 - test - Used when running tests. Uses a H2 in memmory database for testing.

## Testing
Run tests with:
```
mvn clean test -P <profile-name>
```

## Running the application:
```
mvn spring-boot:run
```
No need for profile here, since profiles is only for setting values during build.

## Debug

To se what is happening in the pod in AKS you can either read the deployments logs:
```
kubectl logs <pod-name>
```

Or access the container, if it's running:
```
kubectl exec -it <pod-name> -- /bin/sh
```

You get the pod names with:
```
kubectl get pod
```

# Contribute
TODO: Explain how other users and developers can contribute to make your code better. 

If you want to learn more about creating good readme files then refer the following [guidelines](https://docs.microsoft.com/en-us/azure/devops/repos/git/create-a-readme?view=azure-devops). You can also seek inspiration from the below readme files:
- [ASP.NET Core](https://github.com/aspnet/Home)
- [Visual Studio Code](https://github.com/Microsoft/vscode)
- [Chakra Core](https://github.com/Microsoft/ChakraCore)