# Spring Boot DEMO

API demo para practicar Spring Boot, CI/CD y más. Es el equivalente en Java de `python-fastapi-demo`.

## Requisitos

- Java >= 21
- Docker (opcional)

No hace falta instalar Maven: el proyecto trae el wrapper (`mvnw` / `mvnw.cmd`).

## Instrucciones

1. Correr los tests
```
./mvnw test          # Linux / macOS / Git Bash
.\mvnw.cmd test      # PowerShell
```

2. Iniciar la API
```
./mvnw spring-boot:run
```
La API queda en http://localhost:8080

3. Construir y correr con Docker
```
docker build -t springboot-demo .
docker run -p 8080:8080 springboot-demo
```

## Endpoints

| Método | Ruta              | Descripción                         |
|--------|-------------------|-------------------------------------|
| GET    | `/`               | Devuelve `ok`                       |
| GET    | `/status`         | Estado y versión                    |
| GET    | `/stores`         | Lista todas las tiendas             |
| GET    | `/stores/{id}`    | Una tienda (404 si no existe)       |
| POST   | `/stores`         | Crea una tienda (201)               |
| DELETE | `/stores/{id}`    | Borra una tienda (204)              |
| GET    | `/products`       | Lista los productos                 |
| GET    | `/actuator/health`| Health check (para Kubernetes)      |

Ejemplo:
```
curl -X POST localhost:8080/stores -H "Content-Type: application/json" \
     -d '{"name":"Mi Tienda","address":"Calle 123"}'
```

Los datos se cargan de `src/main/resources/data/*.json` y viven en memoria
(se reinician al reiniciar la app).

## CI/CD (GitHub Actions)

- `.github/workflows/pr.yaml`: en cada Pull Request corre los tests.
- `.github/workflows/cicd.yaml`: en cada push a `master`:
  1. **CI**: tests → build de la imagen Docker → push a Docker Hub.
  2. **CD**: se autentica en AWS y actualiza el deployment en EKS con la nueva imagen.

Secrets necesarios en el repositorio de GitHub:
`DOCKER_USERNAME`, `DOCKER_PASSWORD`, `AWS_IAM_ROLE`, `CLUSTER_NAME`.

## Kubernetes

```
kubectl create namespace app
kubectl apply -f k8s/app.yaml
```
(Cambia `<tu-usuario-docker>` en `k8s/app.yaml` por tu usuario de Docker Hub.)
"# spring-demo-ci-cd" 
