# syntax=docker/dockerfile:1.7
FROM eclipse-temurin:25-jre-noble@sha256:f9bd8815e73632c22985ebb133ec49b9fc4ad5ffe0657594ac02748ad0431ab7

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
