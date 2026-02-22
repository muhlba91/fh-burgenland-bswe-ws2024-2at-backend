FROM amazoncorretto:25-alpine-jdk@sha256:afb37b0939cf8e627e7a18569b661cd3470611e65639d128f7a709d65615482e

ARG CI_COMMIT_TIMESTAMP
ARG CI_COMMIT_SHA
ARG CI_COMMIT_TAG

LABEL org.opencontainers.image.authors="Daniel Muehlbachler-Pietrzykowski <daniel.muehlbachler@cloudventures.cloud>"
LABEL org.opencontainers.image.vendor="Daniel Muehlbachler-Pietrzykowski"
LABEL org.opencontainers.image.source="https://github.com/muhlba91/hochschule-burgenland-bswe-ws2024-2at-backend"
LABEL org.opencontainers.image.created="${CI_COMMIT_TIMESTAMP}"
LABEL org.opencontainers.image.title="hochschule-burgenland-bswe-ws2024-2at-backend"
LABEL org.opencontainers.image.description="Hochschule Burgenland WS2024 weather app backend of the 2nd attempt of the 'Software Management II' course."
LABEL org.opencontainers.image.revision="${CI_COMMIT_SHA}"
LABEL org.opencontainers.image.version="${CI_COMMIT_TAG}"

COPY build/libs/hochschule-burgenland-bswe-ws2024-2at-backend-*.jar /app.jar

EXPOSE 8080/tcp

CMD ["java", "-jar", "/app.jar"]
