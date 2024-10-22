# Step 1: Java 17 기반 Debian 이미지 사용
FROM openjdk:17-jdk-slim as build

# Step 2: 기본 유틸리티 설치 (bash, xargs 등)
RUN apt-get update && apt-get install -y \
    bash \
    coreutils \
    findutils

# Step 3: 작업 디렉토리 설정
WORKDIR /app

# Step 4: Gradle Wrapper 파일과 필요한 설정 파일 복사
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

# Step 5: Gradle Wrapper 실행 권한 부여
RUN chmod +x gradlew

# Step 6: 운영체제 정보 출력
RUN echo "Operating System Information:" && cat /etc/os-release

# Step 7: 의존성 다운로드 (Gradle dependencies 캐시 활용)
RUN ./gradlew build --no-daemon || exit 0

# Step 8: 소스 코드 복사
COPY src ./src

# Step 9: 프로젝트 빌드
RUN ./gradlew build --no-daemon -x test

# Step 10: 애플리케이션 포트 설정
EXPOSE 8080

# Step 11: 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]