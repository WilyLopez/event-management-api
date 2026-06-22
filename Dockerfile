# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copiar pom.xml para descargar dependencias y aprovechar la caché de Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente y construir el jar empaquetado sin ejecutar los tests
COPY src ./src
RUN mvn package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiar el jar generado desde la etapa de construcción
COPY --from=build /app/target/*.jar app.jar

# Crear un usuario no root para mejorar la seguridad del contenedor
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Exponer el puerto por defecto de Spring Boot
EXPOSE 8080

# Parámetros óptimos de JVM para entornos de producción y Docker
ENTRYPOINT ["java", \
            "-XX:MaxRAMPercentage=75.0", \
            "-XX:MinRAMPercentage=50.0", \
            "-XX:+UseG1GC", \
            "-jar", "app.jar"]
