FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Instala Maven
RUN apk add --no-cache maven git bash

# Copia todo o código-fonte
COPY . .

# Build do projeto (gera o JAR)
RUN mvn clean package -DskipTests

# Expõe porta
EXPOSE 8080

# Rodar a aplicação
ENTRYPOINT ["java", "-jar", "target/haupsystem-0.0.1-SNAPSHOT.jar"]
