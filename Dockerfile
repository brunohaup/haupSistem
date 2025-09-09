# Usando JDK 17 leve
FROM eclipse-temurin:17-jdk-alpine

# Diretório de trabalho dentro do container
WORKDIR /app

# Copia o JAR gerado localmente
COPY target/haupsystem-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta que o Spring Boot vai usar
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]

