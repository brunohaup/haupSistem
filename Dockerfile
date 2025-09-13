# Imagem JDK 17
FROM eclipse-temurin:17-jdk-alpine

# Diretório de trabalho
WORKDIR /app

# Instala Maven e bash
RUN apk add --no-cache maven bash

# Copia todo o código-fonte
COPY . .

# Build do projeto (gera o JAR)
RUN mvn clean package -DskipTests

# Expõe a porta do Spring Boot
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "target/haupsystem-0.0.1-SNAPSHOT.jar"]
