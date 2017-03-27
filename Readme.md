#backend

##BUILD
#mvn clean compile
mvn clean package

##RUN
java -jar target/maven-verticle-3.4.1-fat.jar
docker run -p 6379:6379 redis:alpine



#frontend

##BUILD
docker build -t vertx-voting-app/vote-front:dev .

##RUN
run --name angular-client -p 4200:4200 vertx-voting-app/vote-front:dev
