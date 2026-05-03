# ============================================================
# Stage 1: Build
# ============================================================
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copiar Maven wrapper y pom.xml primero (cache de dependencias)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# Copiar código fuente y compilar
COPY src/ src/
RUN ./mvnw clean package -DskipTests -B

# ============================================================
# Stage 2: Runtime
# ============================================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiar JAR desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Puerto dinámico (Railway inyecta PORT automáticamente)
EXPOSE 8080

# Shell form para que las env vars se expandan correctamente
ENTRYPOINT java -jar app.jar
