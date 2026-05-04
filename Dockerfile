FROM node:22-alpine AS frontend
WORKDIR /app
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ .
RUN npm run build

FROM eclipse-temurin:26-jdk AS backend
WORKDIR /app
COPY backend/pom.xml backend/mvnw backend/mvnw.cmd ./
COPY backend/.mvn ./.mvn
COPY backend/src ./src
RUN chmod +x mvnw && ./mvnw package -DskipTests -q

FROM eclipse-temurin:26-jre
WORKDIR /app
COPY --from=backend /app/target/game-mall-1.0.0.jar app.jar
COPY --from=frontend /app/dist /app/frontend
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
