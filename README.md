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

## Flujo de ramas

| Rama | Para qué | Se despliega en |
|------|----------|-----------------|
| `main` | Código en producción. Siempre estable. | production |
| `develop` | Integración de lo próximo a salir. | staging |
| `feature/<nombre>` | Una funcionalidad o cambio. Sale de `develop`. | — |
| `hotfix/<nombre>` | Arreglo urgente de producción. Sale de `main`. | — |

```
feature/mi-cambio ──PR──► develop ──PR──► main
                          (staging)      (production)
```

Reglas:
- Nadie hace push directo a `main` ni a `develop`: todo entra por **Pull Request**.
- Un PR solo se mergea si los checks (`Tests`, `Docker build`) están en verde.
- Un `hotfix` se mergea a `main` y luego también a `develop`, para no perder el arreglo.

Ejemplo de trabajo diario:
```
git checkout develop
git pull
git checkout -b feature/nuevo-endpoint
# ... cambios ...
git add .
git commit -m "Agregar endpoint X"
git push -u origin feature/nuevo-endpoint
# en GitHub: abrir PR feature/nuevo-endpoint -> develop
```

## CI/CD (GitHub Actions)

- `.github/workflows/pr.yaml`: en cada Pull Request hacia `develop` o `main`
  corre los tests y comprueba que la imagen Docker se construye. No publica nada.
- `.github/workflows/cicd.yaml`: en cada push (o merge) a `develop` o `main`:
  1. **CI**: tests → build de la imagen → push a Docker Hub.
  2. **CD**: despliega en `staging` (desde `develop`) o `production` (desde `main`).
     Por ahora el despliegue solo imprime la imagen; los comandos reales de
     Kubernetes están comentados en el workflow.

Etiquetas de la imagen en Docker Hub:

| Rama | Etiquetas |
|------|-----------|
| `develop` | `develop`, `develop-<sha>` |
| `main` | `latest`, `main-<sha>` |

La etiqueta con el sha es única y nunca cambia: sirve para saber exactamente
qué commit está desplegado y para volver atrás (rollback).

Secrets necesarios en el repositorio de GitHub: `DOCKER_USERNAME`, `DOCKER_PASSWORD`.

## Kubernetes

```
kubectl create namespace app
kubectl apply -f k8s/app.yaml
```
(Cambia `<tu-usuario-docker>` en `k8s/app.yaml` por tu usuario de Docker Hub.)
