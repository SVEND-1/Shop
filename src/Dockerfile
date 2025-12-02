# Этап 1: Сборка
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Копируем ВЕСЬ проект
COPY . /project

# Переходим в папку проекта
WORKDIR /project

# Проверяем что скопировалось
RUN echo "=== Структура проекта ===" && \
    find . -name "*.java" -type f | head -5 && \
    echo "=== pom.xml ===" && \
    find . -name "pom.xml" -type f

# Собираем проект
RUN mvn clean package -DskipTests

# Проверяем JAR
RUN echo "=== Собранные JAR ===" && \
    find . -name "*.jar" -type f

# Этап 2: Запуск
FROM eclipse-temurin:17-alpine

WORKDIR /app

# Ищем и копируем JAR (любое имя)
COPY --from=builder /project/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]