#!/usr/bin/env bash

export PATH=/usr/local/bin:$PATH
# This file has to exist in the local filesystem. This is a licensed software
# that requires an agreement to be signed
export ORACLE_JDBC_DRIVER_FILE="/Users/gabor/Projects/ojdbc7.jar"
export INSTALL_DIR=~/git/transmart/

export SDKMAN_DIR="~/.sdkman"
[[ -s "~/.sdkman/bin/sdkman-init.sh" ]] && source "~/.sdkman/bin/sdkman-init.sh"
source ~/.zshrc

sdk update
sdk use grails 2.5.4
sdk use java 8u111
sdk use gradle 4.9

# Clean out the local repo
rm -fR ~/.m2

# Add OJDBC Driver, now required, since i2b2 on Oracle is used.
echo "### Installing ojdbc driver from ${ORACLE_JDBC_DRIVER_FILE} ###"
mvn install:install-file \
  -DgroupId=com.oracle -DartifactId=ojdbc7 \
  -Dversion=12.1.0.1 \
  -Dpackaging=jar \
  -Dfile=${ORACLE_JDBC_DRIVER_FILE} \
  -DgeneratePom=true
RC=$?
echo "Completed with ${RC} status"

# This is needed for IapApi compilation, but if I get the pom fixed, then
# we don't need this.
#mvn dependency:get -Dartifact=org.apache.logging.log4j:log4j-api:2.6.2

# Transmart Core is a Gradle project
echo '### Build transmart-core-api plugin with Gradle'
cd ${INSTALL_DIR}/transmart-core-api
# Due to gradle version being behind, this is required for now, otherwise
# warning message will instruct you to do this.
grep "enableFeaturePreview('STABLE_PUBLISHING')" settings.gradle
# Skip adding it if already added
if [ $RC -ne 0 ];
then
	echo "enableFeaturePreview('STABLE_PUBLISHING')" >> settings.gradle
fi
gradle build --warning-mode all; RC=$?; echo "### Gradle build completed with ${RC} status"
gradle publishToMavenLocal; RC=$?; echo "### Gradle publishToMavenLocal completed with ${RC} status"

# Order the plugins, based on dependency
PLUGIN_DIRS="transmart-java
transmart-shared
biomart-domain
search-domain
transmart-core-db
transmart-custom
transmart-legacy-db
folder-management-plugin
dalliance-plugin
spring-security-auth0
Rmodules
galaxy-export-plugin
transmart-fractalis
transmart-gwas-plink
transmart-gwas-plugin
transmart-metacore-plugin
transmart-rest-api
transmart-xnat-importer-plugin
transmart-xnat-viewer"

# Note: Unable to build `transmart-core-db-tests` because it uses special Hyve
# release of gmock library, which is not included, and I could not find it
# anywhere else.

# Removed 'transmart-mydas', because `Could not find artifact uk.ac.ebi.mydas:mydas:jar:1.7.0.transmart-19.0-SNAPSHOT`
# TODO:
# remove `transmart-core-db-tests` from `transmart-rest-api` BuildConfig
# Update xnat-viewer to use the right Rserve version

# Which one needs Rserve and how we handle ojdbc7.jar file (license), base it on CentOS

for PLUGIN_DIR in $PLUGIN_DIRS
do

  cd ${INSTALL_DIR}/${PLUGIN_DIR}
  CURRENT_DIR=`pwd`
  echo '***********'
  echo "*** Building Plugin in ${CURRENT_DIR} directory. ***"
  echo '***********'
  echo
  # grails package-plugin
  grails RefreshDependencies; RC=$?; echo "### grails RefreshDependencies step completed with ${RC} status"
  grails compile; RC=$?; echo "### grails compile step completed with ${RC} status"
  grails maven-install; RC=$?; echo "### grails maven-install completed with ${RC} status"

done

cd ${INSTALL_DIR}/transmartApp
grails war
