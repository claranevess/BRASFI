# Usa uma imagem base oficial do OpenJDK 21 com Alpine Linux para um tamanho menor
FROM openjdk:21-jdk-slim-bullseye

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia o Maven Wrapper e o pom.xml primeiro para aproveitar o cache do Docker.
# Se esses arquivos não mudarem, as camadas de build do Maven não precisarão ser reconstruídas.
COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .

# Copia o código-fonte restante
COPY src ./src

# Builda o projeto Maven.
# -DskipTests: pula os testes para um build mais rápido no ambiente de deploy.
# --batch-mode: evita prompts interativos durante o build.
RUN ./mvnw clean install -DskipTests --batch-mode

# Expõe a porta que sua aplicação Spring Boot usa (padrão é 8080)
EXPOSE 8080

# Comando para rodar a aplicação Spring Boot
# A versão padrão para novos projetos Spring Boot é '0.0.1-SNAPSHOT'.
CMD ["java", "-jar", "target/brasfi-platforma-0.0.1-SNAPSHOT.jar"]