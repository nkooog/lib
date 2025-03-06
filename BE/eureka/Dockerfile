# 1. JDK 17 기반 빌드 이미지 사용
FROM maven:3.8.6-eclipse-temurin-17 AS builder

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 프로젝트 코드 복사
COPY . /app

# 4. Maven을 이용한 빌드 (테스트 제외)
RUN mvn -f /app/pom.xml clean package -DskipTests

# 5. JRE 17 기반의 경량 이미지 사용 (실행 단계)
FROM eclipse-temurin:17-jre

# 6. 작업 디렉토리 설정
WORKDIR /app

# 7. 빌드된 JAR 파일 복사
COPY --from=builder /app/target/*.jar app.jar

# 8. 환경 변수 설정 (기본값 제공)
ENV SERVER_PORT=45000
ENV EUREKA_INSTANCE_HOSTNAME=eureka

# 9. 컨테이너 실행 시 JAR 실행
CMD ["sh", "-c", "java -jar app.jar --server.port=${SERVER_PORT} --eureka.instance.hostname=${EUREKA_INSTANCE_HOSTNAME}"]

# 10. Eureka 서버 포트 개방
EXPOSE 45000
