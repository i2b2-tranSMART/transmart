
### Prerequisites for local build

Installation of various open source software and also Java are required to run tranSmart on any platform.
 
* SDKMAN (latest) [https://sdkman.io/install](https://sdkman.io/install)
* JAVA (v8) `sdk install java 8u111`
* GRAILS (v.2.5.4) `sdk install grails 2.5.4`
* GRADLE (4.9) `sdk install gradle 4.9`
* MAVEN (latest)
* TOMCAT (latest) [https://tomcat.apache.org/whichversion.html](https://tomcat.apache.org/whichversion.html)

Also, the environment variables INSTALL_DIR will need to be set (or the scripts will assume the git repo directory to be the INSTALL_DIR variable)

### Prerequisites for docker install

In addition to all software that is required on a local installation, docker installation also requires `docker` service for the local OS.

Install the latest version of docker from docker.com for the appropriate local machine. You can find the instructions on [https://docs.docker.com/](https://docs.docker.com/).

### Build tranSMART

 Run the script `${INSTALL_DIR}/Scripts/install-centos/buildTransmart.sh` to create the deployable .war file.

### Build With Docker

 Run the script `${INSTALL_DIR}/Scripts/install-centos/buildWithDocker.sh` .