#!/usr/bin/env zsh

if [ "${INSTALL_DIR}" = "" ];
then
	export INSTALL_DIR=$(realpath $(dirname ${0})/../../)
fi

# Create a local image that has all prerequisites
docker build --no-cache --rm \
	--tag transmart-builder \
	--file Dockerfile.buildtransmart .

exit


# Run the image that will build transmart.war from
# current source repo
docker run -i -t \
	--volume ${INSTALL_DIR}:/root/transmart \
	--name transmart-building-container
	transmart-builder
