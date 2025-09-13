# Usa uma imagem base com o OpenJDK para compilação
FROM maven:3.8.5-openjdk-17-slim AS build

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia o arquivo pom.xml para gerenciar as dependências
COPY pom.xml .

# Baixa as dependências do Maven
RUN mvn dependency:go-offline

# Copia todo o código-fonte da sua aplicação
COPY src ./src

# Compila o projeto e cria o arquivo .jar
RUN mvn clean install -DskipTests

# --- Segunda etapa: Cria o ambiente de execução final ---

# Usa uma imagem mais leve com o OpenJDK para rodar a aplicação
FROM openjdk:17-jdk-slim

# Define o diretório de trabalho
WORKDIR /app

# Copia o arquivo .jar da etapa de compilação
COPY --from=build /app/target/*.jar ./app.jar

# Define o comando que será executado quando o contêiner iniciar
# Use a porta 8080, o Render vai mapeá-la para a porta externa
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]