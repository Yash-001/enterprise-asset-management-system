# ===========================
# Stage 1: Build
# ===========================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:resolve -B

COPY src/ src/
RUN ./mvnw package -DskipTests -B

# ===========================
# Stage 2: Runtime
# ===========================
FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S eams && adduser -S eams -G eams

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

RUN mkdir -p /app/uploads /var/log/eams && \
    chown -R eams:eams /app /var/log/eams

USER eams

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/management/health || exit 1

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:+ExitOnOutOfMemoryError", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
