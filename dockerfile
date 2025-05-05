# Use uma imagem base Gradle com JDK 17
FROM gradle:8.5-jdk17 AS builder

# Define o diretório de trabalho
WORKDIR /app

# Copia os arquivos Gradle
COPY build.gradle.kts settings.gradle.kts gradlew /app/
COPY gradle /app/gradle

# Copia o código-fonte
COPY src /app/src

RUN chmod +x gradlew

# Compila o aplicativo
RUN ./gradlew build -x test

# Cria a imagem de execução
FROM openjdk:17-jdk-slim

# Define o diretório de trabalho
WORKDIR /app

# Copia o JAR compilado da etapa de construção
COPY --from=builder /app/build/libs/*.jar app.jar

# Expõe a porta usada pelo servidor Ktor
EXPOSE 8080

# Executa o aplicativo
CMD ["java", "-jar", "app.jar"]
