# ===========================
# Stage 1: Dependencies
# ===========================
FROM eclipse-temurin:21-jdk-alpine AS deps

WORKDIR /app

# Copy only dependency-related files first (maximizes layer caching)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && \
    ./mvnw dependency:go-offline -B --no-transfer-progress

# ===========================
# Stage 2: Build
# ===========================
FROM deps AS builder

WORKDIR /app

COPY src/ src/
RUN ./mvnw package -DskipTests -B --no-transfer-progress && \
    java -Djarmode=layertools -jar target/*.jar extract --destination /app/extracted

# ===========================
# Stage 3: Runtime
# ===========================
FROM eclipse-temurin:21-jre-alpine AS runtime

LABEL maintainer="yashconsulting" \
      org.opencontainers.image.title="EAMS Backend" \
      org.opencontainers.image.description="Enterprise Asset Management System" \
      org.opencontainers.image.source="https://github.com/yashconsulting/enterprise-asset-management-system"

# Install curl for healthcheck (wget already in alpine, but curl is more reliable)
RUN apk add --no-cache curl && \
    addgroup -S eams && adduser -S eams -G eams

WORKDIR /app

# Copy Spring Boot layered JAR (maximizes Docker layer reuse on rebuilds)
COPY --from=builder /app/extracted/dependencies/ ./
COPY --from=builder /app/extracted/spring-boot-loader/ ./
COPY --from=builder /app/extracted/snapshot-dependencies/ ./
COPY --from=builder /app/extracted/application/ ./

# Create required directories
RUN mkdir -p /app/uploads /var/log/eams && \
    chown -R eams:eams /app /var/log/eams

USER eams

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -sf http://localhost:8080/management/health || exit 1

ENTRYPOINT ["java", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:+ExitOnOutOfMemoryError", \
    "-XX:+UseG1GC", \
    "-XX:+UseStringDeduplication", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod}", \
    "org.springframework.boot.loader.launch.JarLauncher"]
