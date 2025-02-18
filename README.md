# Hochschule Burgenland - BSWE - WS2024 - 2nd Attempt - Weather App - Backend - Reference

[![](https://img.shields.io/github/license/muhlba91/fh-burgenland-bswe-ws2024-2at-backend?style=for-the-badge)](LICENSE.md)
[![](https://img.shields.io/github/actions/workflow/status/muhlba91/fh-burgenland-bswe-ws2024-2at-backend/verify.yml?style=for-the-badge)](https://github.com/muhlba91/fh-burgenland-bswe-ws2024-2at-backend/actions/workflows/verify.yml)
[![](https://api.scorecard.dev/projects/github.com/muhlba91/fh-burgenland-bswe-ws2024-2at-backend/badge?style=for-the-badge)](https://scorecard.dev/viewer/?uri=github.com/muhlba91/fh-burgenland-bswe-ws2024-2at-backend)
[![](https://img.shields.io/github/release-date/muhlba91/fh-burgenland-bswe-ws2024-2at-backend?style=for-the-badge)](https://github.com/muhlba91/fh-burgenland-bswe-ws2024-2at-backend/releases)

This is a reference implementation of the weather application's backend for the 2nd attempt of the "Software Management II" course at the Hochschule Burgenland in WS2024.
It solely acts as a reference, not as a complete implementation, and it is not expected by students to produce a similar implementation.

---

## API Specification

The OpenAPI specification can be found in [docs/openapi.yaml](./docs/openapi.yaml).

## Authentication / User Selection

The service does not provide authentication and users are selected by specifying their identifier (UUID) in the request URL.

---

## Configuration

See [src/main/resources/application.yaml](./src/main/resources/application.yaml) for all available and default configuration options.
To run the service successfully, you need to provide the following environment variables:

- `AVWX_API_KEY`: The API key for the [Aviation Weather Rest API](https://avwx.rest/) in the format `Token avwx-api-key`. The value will be used in the `Authorization` header when calling the API.

---

## Development

The service is implemented in Java using the Spring Boot framework and Gradle as the build tool.

### Code Quality

The code quality is ensured by the following tools:

- [Checkstyle](https://checkstyle.org/) for code style checking
- [PMD](https://pmd.github.io/) for static code analysis
- [JaCoCo](https://www.eclemma.org/jacoco/) for code coverage
- [PiTest](https://pitest.org/) for mutation testing
- [YAMLLint](https://yamllint.readthedocs.io/) for YAML linting
- [Spectral](https://stoplight.io/open-source/spectral/) for OpenAPI linting
- [Helm Lint](https://helm.sh/docs/helm/helm_lint/) for Helm chart linting
- [Conform](https://github.com/siderolabs/conform) for commit message linting
- [CycloneDX](https://cyclonedx.org/) for software bill of materials generation
- [Grype](https://github.com/anchore/grype) for software bill of materials scanning
- [Renovate](https://www.whitesourcesoftware.com/free-developer-tools/renovate/) for dependency updates

### Testing

To run the tests, run the following command:

```shell
# unit tests and jacoco reporting
./gradlew test jacocoTestReport jacocoTestCoverageVerification

# pitest
./gradlew pitest
```

### Linting

To run the linting checks, run the following command:

```shell
# checkstyle
./gradlew checkstyleMain checkstyleTest

# pmd
./gradlew pmdMain pmdTest

# yamllint
yamllint .

# spectral
spectral lint 'docs/**/*.yaml' --fail-severity info
spectral lint 'docs/**/*.yml' --fail-severity info

# helm lint
helm lint charts/*
```

### Software Bill of Materials

To generate the software bill of materials, run the following command:

```shell
./gradlew cycloneDxBom
grype sbom:build/reports/sbom.json
```

### Running

To run the service, run the following command:

```shell
./gradlew bootRun
```

### Building

To build the service, run the following command:

```shell
./gradlew bootJar
```

### Docker

To build the Docker image, run the following command:

```shell
./gradlew bootJar
docker build -t fh-burgenland-bswe-ws2024-2at-backend .
docker run -p 8080:8080 fh-burgenland-bswe-ws2024-2at-backend
```

### Commit Message

Commit messages must adhere to the [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) specification.

To lint the commit messages, run the following command:

```shell
conform enforce
```

You can also use [Commitizen](https://commitizen.github.io/cz-cli/) to create commit messages:

```shell
cz commit
```

## GitHub Actions

The GitHub Actions workflows ensure that all code quality checks pass and that the code is deployable.

The release workflow creates a new release with publishing a container image to the [GitHub Container Registry](https://ghcr.io/).
It also published the SBOMs of the release assets, the OpenAPI specification, and their build provenance.

---

## Kubernetes Deployment

The backend service is provided as an OCI image in [ghcr.io/muhlba91/fh-burgenland-bswe-ws2024-2at-backend](https://ghcr.io/muhlba91/fh-burgenland-bswe-ws2024-2at-backend).

The following example shows an example deployment using the provided [Helm chart](./charts/weather-app-backend/).

```shell
# set the AVWX API key
export AVWX_API_KEY="your-avwx-api-key"

# create the helm values file
cat <<EOF > weather-app-backend-values.yaml
---
apiKeys:
  avwx: "Token ${AVWX_API_KEY}$"
EOF

# install weather-app-backend with helm
helm install weather-app-backend ./charts/weather-app-backend -f weather-app-backend-values.yaml
```

### Private Container Registry

If you forked this repository and are using a private container registry, you need to provide the following Kubernetes secret:

```shell
kubectl create secret docker-registry ghcr-credentials \
  --docker-server=ghcr.io \
  --docker-username=your-username \
  --docker-password=your-token
```

Then, you can reference the secret in the Helm values file:

```yaml
imagePullSecrets
  - name: ghcr-credentials
```

> Make sure to replace `your-username` and `your-token` with your GitHub username and a personal access token with the `read:packages` scope.

---

## Notes

- Unit test generation supported by GitHub Copilot.
- Some workflow steps are allowed to fail due to the repository being private and not having access to certain security features.
