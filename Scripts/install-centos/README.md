
### Prerequisites for local install

Installation of various open source software and also Java are required to run tranSmart on any platform.
 
* SDKMAN (latest) [https://sdkman.io/install](https://sdkman.io/install)
* JAVA (v8) `sdk install java 8u111`
* GRAILS (v.2.5.4) `sdk install grails 2.5.4`
* GRADLE (4.9) `sdk install gradle 4.9`
* TOMCAT (latest) [https://tomcat.apache.org/whichversion.html](https://tomcat.apache.org/whichversion.html)


### Prerequisites for docker install

In addition to all software that is required on a local installation, docker installation also requires `docker` service for the local OS.

Install the latest version of docker from docker.com for the appropriate local machine. You can find the instructions on [https://docs.docker.com/](https://docs.docker.com/).

### Build Plugins

 Run the script `Scripts/install-centos/buildPlugins.sh` from the main repository, to create a local cache of required plugins.

### Build tranSMART

 Run the script `Scripts/install-centos/buildTransmart.sh` from the *INSTALL_DIR* directory, to create the deployable .war file.

### Install `tranSMART`, `solr` and `Rserve`

 Copy the generated `transmart.war` file to the local Tomcat _webapps_ directory.

### Create docker image

 Run the script `Scripts/install-centos/installDocker.sh` from the *INSTALL_DIR* directory.