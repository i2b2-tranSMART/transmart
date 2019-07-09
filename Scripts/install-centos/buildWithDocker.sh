#!/bin/sh -x

if [ "${INSTALL_DIR}" = "" ];
then
	export INSTALL_DIR=$(realpath $(dirname ${0})/../../)
	echo "INSTALL_DIR is now ${INSTALL_DIR}"
fi

createDockerImage() {
	# Create a local image that has all prerequisites
	docker build --no-cache --rm \
		--tag transmart-builder \
		--file Dockerfile.buildtransmart .
}

runBuild() {
	# Run the image that will build transmart.war from
	# current source repo
	docker rm transmart-building-container
	docker run -i -t \
		--volume "${INSTALL_DIR}:/tmp/transmart" \
		--name transmart-building-container \
		transmart-builder /tmp/transmart/Scripts/install-centos/buildTransmart.sh
}

if [ $# -eq 0 ];
then
	echo "No specific step specified. Will run all steps"
	createDockerImage
	runBuild
else
	$*
fi
