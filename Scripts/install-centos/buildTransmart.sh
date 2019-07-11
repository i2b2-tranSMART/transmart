#!/bin/sh

SCRIPT_NAME=$0

SDK_JAVA_VERSION=8.0.212-zulu
SDK_GRAILS_VERSION=2.5.4
SDK_GRADLE_VERSION=4.9


OUTPUT_LOGFILE=local_transmart_release-$(date "+%Y-%m-%d_%H-%M-%S").log

logger() {
	LEVEL=$(printf "%5s" $1)
	shift
  MSG=$*
	TIMESTAMP=$(date "+%Y-%m-%d %H:%M:%S")
  echo "${TIMESTAMP} [${LEVEL}] ${MSG}"
}

loginfo() {
	logger "INFO" $*
}

logerror() {
	logger "ERROR" $*
}

logdebug() {
	logger "DEBUG" $*
}

if [ "${INSTALL_DIR}" == "" ];
then
	CURRENT_DIR=$(dirname $SCRIPT_NAME)
	export INSTALL_DIR=$(realpath ${CURRENT_DIR}/../..)
	loginfo "INSTALL_DIR variable is not defined. Using ${INSTALL_DIR} instead."
fi

checkExitStatus() {
	STATUS=$1
	MSG=$2
	TIMESTAMP=$(date "+%Y-%m-%d %H:%M:%S")
	if [ ${STATUS} -ne 0 ];
	then
		logerror "${MSG}"
		exit
	else
		loginfo "${MSG}"
	fi
}

check() {
	if [ ! -d $HOME/.sdkman ];
	then
		loginfo 'Local SDKMAN directory is not found. Installing SDKMAN now.'
		curl -s https://get.sdkman.io | bash
		chmod a+x "$HOME/.sdkman/bin/sdkman-init.sh"
	else
		loginfo 'Local SDKMAN directory has been found.'
	fi
	# Source SDKMAN
	source "$HOME/.sdkman/bin/sdkman-init.sh"
	# Update SDKMAN
	sdk selfupdate force

	JAVA_VERSION=$(sdk current java | tail -1)
	if [ "${JAVA_VERSION}" = "Using java version ${SDK_JAVA_VERSION}" ];
	then
		loginfo "Java version verified"
	else
		logerror "Java version invalid. ${JAVA_VERSION}"
		loginfo "Installing expected version of java"
		sdkman_auto_answer=true sdk install java ${SDK_JAVA_VERSION}
		sdk use ${SDK_JAVA_VERSION}
		checkExitStatus $? "installing java version ${SDK_JAVA_VERSION}"
	fi

	GRADLE_VERSION=$(sdk current gradle | tail -1)
	if [ "${GRADLE_VERSION}" = "Using gradle version ${SDK_GRADLE_VERSION}" ];
	then
		loginfo "Gradle version ${SDK_GRADLE_VERSION} verified"
	else
		logerror "Gradle version invalid. ${GRADLE_VERSION}"
		loginfo "Installing the gradle version ${SDK_GRADLE_VERSION}"
		sdkman_auto_answer=true sdk install gradle ${SDK_GRADLE_VERSION}
		sdk use gradle ${SDK_GRADLE_VERSION}
		checkExitStatus $? "installing gradle version ${SDK_GRADLE_VERSION}"
	fi

	GRAILS_VERSION=$(sdk current grails | tail -1)
	if [ "${GRAILS_VERSION}" = "Using grails version ${SDK_GRAILS_VERSION}" ];
	then
		loginfo "Grails version ${SDK_GRAILS_VERSION} verified"
	else
		loginfo "Installing the grails version ${SDK_GRAILS_VERSION}"
		sdkman_auto_answer=true sdk install grails ${SDK_GRAILS_VERSION}
		sdk use ${SDK_GRAILS_VERSION}
		checkExitStatus $? "installing grails version ${SDK_GRAILS_VERSION}"
	fi

}

buildTransmartCoreApi() {
	loginfo 'Build transmart-core-api plugin with Gradle'

	cd ${INSTALL_DIR}/transmart-core-api
	checkExitStatus $? "Using ${INSTALL_DIR}/transmart-core-api for gradle build."

	# Due to gradle version being behind, this is required for now, otherwise
	# warning message will instruct you to do this.
	grep "enableFeaturePreview('STABLE_PUBLISHING')" settings.gradle
	# Skip adding it if already added
	if [ $? -ne 0 ];
	then
		echo "enableFeaturePreview('STABLE_PUBLISHING')" >> settings.gradle
	fi
	gradle build --warning-mode all
	checkExitStatus $? "Gradle build"
	gradle publishToMavenLocal
	checkExitStatus $? "Gradle publishToMavenLocal"
}

clean() {
	# Clear out local maven cache
	rm -fR $HOME/.m2
}

buildAGrailsPlugin() {
	PLUGIN_SUBDIR=$1
	
  cd ${INSTALL_DIR}/${PLUGIN_SUBDIR}
  CURRENT_DIR=`pwd`
  loginfo "Building Plugin in ${CURRENT_DIR} directory."

	grails RefreshDependencies
	checkExitStatus $? "Checking plugin dependencies for ${PLUGIN_SUBDIR}"

  grails compile
	checkExitStatus $? "Compiling plugin ${PLUGIN_SUBDIR}"

  grails maven-install
	checkExitStatus $? "Installing in local maven cache ${PLUGIN_SUBDIR}"

}

buildAllGrailsPlugins() {
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
	for PLUGIN_DIR in $PLUGIN_DIRS
	do
		buildAGrailsPlugin $PLUGIN_DIR
	done
}

buildplugins() {
	check
	loginfo "Clean and rebuild all plugin in local maven cache"
	buildTransmartCoreApi
	buildAllGrailsPlugins
	loginfo "Finished building all plugins"
}

buildtransmart() {
	cd $INSTALL_DIR/transmartApp
	grails war
	checkExitStatus $? "building transmart.war in $(dirname ./target)"
}

buildall() {
	check
	buildTransmartCoreApi
	buildAllGrailsPlugins
	buildtransmart
}

if [ $# -eq 0 ];
then
	echo "\n\nNo parameter is given.\nAt least one parameter is mandatory.\nPlease provide one of the following parameters:"
	echo "\tclean"
	echo "\tcheck"
	echo "\tbuildAGrailsPlugin <SinglePluginName>"
	echo "\tbuildTransmartCoreApi"
	echo "\tbuildAllGrailsPlugins"
	echo "\tbuildplugins"
	echo "\tbuiltransmart"
	echo "\tbuildall"
	echo "\n"
fi

$*

# Notes:
# Unable to build `transmart-core-db-tests` because it uses special Hyve
# release of gmock library, which is not included, and I could not find it
# anywhere else.
# Those were also removed from the main `transmartApp` build dependencies

# Removed 'transmart-mydas', because `Could not find artifact
# uk.ac.ebi.mydas:mydas:jar:1.7.0.transmart-19.0-SNAPSHOT`
#
# TODO:
# remove `transmart-core-db-tests` from `transmart-rest-api` BuildConfig

# TODO: how we handle ojdbc7.jar file (license)
