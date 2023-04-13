FROM amazoncorretto:17-alpine
RUN mkdir -p /opt/microservice-template/
WORKDIR /opt/microservice-template/
COPY ./build/libs/microservice-template.jar /opt/microservice-template/app.jar
ENTRYPOINT ["java","-jar","app.jar"]
