# WPlan

Herramienta de planificación deportiva para estructurar, almacenar y analizar la información necesaria en el proceso de entrenamiento de un equipo de waterpolo.

## Requisitos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado y en ejecución

## Levantar la aplicación

```powershell
docker compose up -d --build
```

Esto arranca tres servicios:

| Servicio  | URL                        | Descripción              |
|-----------|----------------------------|--------------------------|
| Frontend  | http://localhost:8081      | Aplicación web (React)   |
| API REST  | http://localhost:8080      | Backend (Spring Boot)    |
| MySQL     | localhost:3306             | Base de datos            |

La primera vez tarda más porque Docker construye las imágenes. Las siguientes arranca en segundos.

## Parar la aplicación

```powershell
docker compose down
```

Para parar **y borrar los datos** de la base de datos:

```powershell
docker compose down -v
```

## Comprobar que la API está viva

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/health"
```

## Estructura del proyecto

```
WPlan/
├── backend/          # API REST — Spring Boot + MySQL + Drools
├── frontend/         # SPA — React + TypeScript + Vite
└── docker-compose.yml
```

## Pantallas del frontend

- `/login`, `/register` — autenticación con cookie de sesión
- `/planificaciones` — listado y creación de planificaciones
- `/planificaciones/:id` — detalle con pestañas Jugadores, Sesiones y Sesión del día
- `/ejercicios/waterpolo`, `/ejercicios/gimnasio`, `/ejercicios/natacion` — banco de ejercicios
- `/sugerencias` — sugerencias generadas por el motor de reglas Drools

## Pruebas de la API

Ver [PRUEBAS_API.md](PRUEBAS_API.md) para comandos PowerShell que cubren todos los endpoints.
