# Docker
## Create the artifacts
mvn clean install
## Run the docker file
docker build -t composer-facade .
## Run the image as container
docker run --rm -p8080:8080 -e "SPRING_PROFILES_ACTIVE=docker" --hostname composer-facade composer-facade


#### debug
docker run -it --entrypoint /bin/bash composer-facade
