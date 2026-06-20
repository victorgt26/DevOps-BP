# syntax=docker/dockerfile:1.7
FROM eclipse-temurin:17-jre-noble@sha256:1d0f3f847109abf20b50ef72c53366c1b7bf5d52ffd71c562c17f06a39b32863

RUN apt-get update \
    && apt-get install --only-upgrade --no-install-recommends -y openssl libssl3t64 \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --gid 10001 app \
    && useradd --uid 10001 --gid app --no-create-home --shell /usr/sbin/nologin app \
    && mkdir -p /app \
    && chown app:app /app

WORKDIR /app
COPY --chown=app:app target/demo-*.jar app.jar

USER 10001:10001
EXPOSE 8000

ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError" \
    PORT=8000

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
