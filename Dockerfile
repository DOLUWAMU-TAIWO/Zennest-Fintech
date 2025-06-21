FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTests

###Runtime

FROM eclipse-temurin:21-alpine

ENV SPRING_PROFILES_ACTIVE=prod

WORKDIR /app

COPY --from=builder /build/target/Payment-0.0.1-SNAPSHOT.jar payment.jar

EXPOSE 6500

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:6600/actuator/health || exit 1

CMD ["java","-XX:MaxRAMPercentage=75.0","-jar","payment.jar"]