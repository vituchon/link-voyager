# local build

mvn clean package
docker build -f Dockerfile -t link-voyager-maven-image .
docker run --name link-voyager-maven-container -d -p 8888:8080 --security-opt seccomp=unconfined  link-voyager-maven-image
  # one liner
  docker stop link-voyager-maven-container && docker rm link-voyager-maven-container && mvn clean package && docker build -f Dockerfile -t link-voyager-maven-image . && docker run --name link-voyager-maven-container -d -p 8888:8080 --security-opt seccomp=unconfined  link-voyager-maven-image

# deploy to docker hub

docker login --username vitus
docker tag link-voyager-maven-image vitus/link-voyager-app:0.6.5
docker push vitus/link-voyager-app:0.6.5

Y luego usar esta URL
docker.io/vitus/link-voyager-app:0.6.5