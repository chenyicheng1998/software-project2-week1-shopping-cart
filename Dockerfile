# Stage 1: Build the application using Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime image for JavaFX GUI mode
FROM maven:3.9-eclipse-temurin-17
WORKDIR /app

# Install Linux GUI runtime libraries required by JavaFX (X11/GTK/OpenGL)
RUN apt-get update && apt-get install -y --no-install-recommends \
    libx11-6 \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libgtk-3-0 \
    libgl1 \
    libasound2t64 \
    fonts-noto-cjk \
    fonts-noto-color-emoji \
    && rm -rf /var/lib/apt/lists/*

# Copy project files needed for GUI run
COPY --from=build /app/pom.xml ./pom.xml
COPY --from=build /app/src ./src
COPY --from=build /app/target ./target

# UTF-8 + database defaults
ENV LANG=en_US.UTF-8
ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8"
ENV DB_URL="jdbc:mariadb://host.docker.internal:3306/shopping_cart_localization?useSsl=false&restrictedAuth=mysql_native_password"
ENV DB_USER=root

ENTRYPOINT ["mvn", "-q", "javafx:run"]
