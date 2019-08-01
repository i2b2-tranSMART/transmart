#!/bin/sh

# TODO: Check if INSTALL_DIR is set, and points to a valid directory

docker run -d --rm --name i2b2-transmart-tomcat tomcat

docker cp Config.groovy i2b2-transmart-tomcat:/root/.grails/transmartConfig/
docker cp DataSource.groovy i2b2-transmart-tomcat:/root/.grails/transmartConfig/
docker cp ${INSTALL_DIR}/transmartApp/target/transmart.war i2b2-transmart-tomcat:/usr/local/tomcat/webapps/

docker run -d --rm --name i2b2-transmart-solr solr
docker run -d --rm --name i2b2-transmart-rserve dbmi/rserve:18.1-Quickstart




