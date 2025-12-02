# Этап 1: Сборка
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Рабочая директория
WORKDIR /app

# 1. Сначала копируем ТОЛЬКО pom.xml
COPY pom.xml .

# 2. Скачиваем зависимости (кешируем этот слой)
RUN mvn dependency:go-offline -B

# 3. Копируем исходный код
COPY src ./src

# 4. Собираем проект
RUN mvn clean package -DskipTests

# Проверяем что собралось
RUN ls -la target/*.jar

# Этап 2: Запуск
FROM eclipse-temurin:17-alpine

WORKDIR /app

# Копируем JAR файл
COPY --from=builder /app/target/MyShop-0.0.1-SNAPSHOT.jar app.jar

# Открываем порт
EXPOSE 8080

# Запускаем
CMD ["java", "-jar", "app.jar"]