FROM amazoncorretto:17-alpine
RUN mkdir -p /opt/laa-dces-report-service/
WORKDIR /opt/laa-dces-report-service/
COPY ./build/libs/dces-report-service.jar /opt/laa-dces-report-service/app.jar
RUN addgroup -S appgroup && adduser -u 1001 -S appuser -G appgroup
USER 1001
EXPOSE 8088 8099
ENTRYPOINT ["java","-jar","app.jar"]
