# Этап 1: Сборка
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Копируем ВСЕ файлы проекта
COPY . .

# Проверяем что скопировалось
RUN echo "=== Содержимое папки ===" && \
    ls -la && \
    echo "=== Java файлы ===" && \
    find . -name "*.java" -type f 2>/dev/null | head -10

# Собираем проект (без Lombok ошибок не будет)
RUN mvn clean package -DskipTests

# Этап 2: Запуск
FROM eclipse-temurin:17-alpine

WORKDIR /app

# Копируем JAR файл
COPY --from=builder /app/target/*.jar app.jar

# Открываем порт
EXPOSE 8080

# Запускаем
CMD ["java", "-jar", "app.jar"]