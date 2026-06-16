# WPlan
Herramienta de planificación deportiva que permita estructurar, almacenar y analizar la información necesaria para aportar un soporte inteligente al proceso de entrenamiento.

## Probar la API (Docker + sesión con cookie) — PowerShell

### Levantar servicios (una línea)

```powershell
cd "C:\Users\Enriq\OneDrive\Escritorio\CEU\TFG\WPlan\WPlan"; docker compose up -d --build
```

### Comprobar API viva (una línea)

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/health"
```

### Crear sesiones (cookies) para 2 usuarios (una línea)

```powershell
$base="http://localhost:8080"; $sJuanjo=New-Object Microsoft.PowerShell.Commands.WebRequestSession; $sJose=New-Object Microsoft.PowerShell.Commands.WebRequestSession
```

### Registrar usuarios (una línea cada uno)

```powershell
Invoke-RestMethod -Uri "$base/api/auth/register" -Method Post -ContentType "application/json; charset=utf-8" -Body (@{ email="juanjo@ejemplo.com"; password="Juanjo123"; name="Juanjo" } | ConvertTo-Json) -WebSession $sJuanjo
```

```powershell
Invoke-RestMethod -Uri "$base/api/auth/register" -Method Post -ContentType "application/json; charset=utf-8" -Body (@{ email="jose@ejemplo.com"; password="Jose1234"; name="Jose" } | ConvertTo-Json) -WebSession $sJose
```

> Si el usuario ya existe, devolverá 409. En ese caso, usa el login.

### Login (una línea cada uno)

```powershell
Invoke-RestMethod -Uri "$base/api/auth/login" -Method Post -ContentType "application/json; charset=utf-8" -Body (@{ email="juanjo@ejemplo.com"; password="Juanjo123" } | ConvertTo-Json) -WebSession $sJuanjo
```

```powershell
Invoke-RestMethod -Uri "$base/api/auth/login" -Method Post -ContentType "application/json; charset=utf-8" -Body (@{ email="jose@ejemplo.com"; password="Jose1234" } | ConvertTo-Json) -WebSession $sJose
```

### /me (una línea cada uno)

```powershell
Invoke-RestMethod -Uri "$base/api/auth/me" -WebSession $sJuanjo
```

```powershell
Invoke-RestMethod -Uri "$base/api/auth/me" -WebSession $sJose
```

### Crear planificaciones (IDs independientes por usuario) (una línea cada uno)

```powershell
$pJuanjo1 = Invoke-RestMethod -Uri "$base/api/planificaciones" -Method Post -ContentType "application/json; charset=utf-8" -Body (@{ nombre="Plan Juanjo #1"; fechaInicio="2026-04-01"; fechaFin="2026-04-30"; notas="Solo Juanjo" } | ConvertTo-Json) -WebSession $sJuanjo; $pJuanjo1
```

```powershell
$pJose1 = Invoke-RestMethod -Uri "$base/api/planificaciones" -Method Post -ContentType "application/json; charset=utf-8" -Body (@{ nombre="Plan Jose #1"; fechaInicio="2026-05-01"; fechaFin="2026-05-31"; notas="Solo Jose" } | ConvertTo-Json) -WebSession $sJose; $pJose1
```

### Aislamiento: un usuario no accede a las planificaciones del otro (una línea cada uno)

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJose1.id)" -WebSession $sJuanjo
```

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)" -WebSession $sJose
```

> Esperado: 404 en ambos casos (no existe para ese usuario).

## Jugadores (CRUD) — PowerShell

> Endpoints: `/api/planificaciones/{planId}/jugadores` (donde `planId` es el `id` público de la planificación: `numero`).

### Crear jugador (una línea)

```powershell
$j1 = Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/jugadores" -Method Post -ContentType "application/json; charset=utf-8" -Body (@{ nombre="Pepe"; apellidos="Pérez"; fechaNacimiento="2004-01-15"; posicion="Boya"; notas="capitán" } | ConvertTo-Json) -WebSession $sJuanjo; $j1
```

### Listar jugadores (una línea)

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/jugadores" -WebSession $sJuanjo
```

### Obtener jugador por id (una línea)

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/jugadores/$($j1.id)" -WebSession $sJuanjo
```

### Actualizar jugador (una línea)

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/jugadores/$($j1.id)" -Method Put -ContentType "application/json; charset=utf-8" -Body (@{ nombre="Pepe"; apellidos="Pérez"; fechaNacimiento="2004-01-15"; posicion="Defensa"; notas="actualizado" } | ConvertTo-Json) -WebSession $sJuanjo
```

> **Borrar jugador** y el **aislamiento** de jugadores están más abajo (después de asistencia), para no eliminar `$j1` antes de usarlo en las sesiones.

## Calendario y sesiones (CRUD + rango + 3 partes) — PowerShell

> Endpoints: `/api/planificaciones/{planId}/sesiones` con `?desde=YYYY-MM-DD&hasta=YYYY-MM-DD`.  
> Cada sesión del día puede tener **3 partes opcionales**: `gimnasio`, `natacion`, `waterpolo` (si no quieres una parte, envíala como `null` o no la incluyas).

### Crear sesión del día con las 3 partes (una línea)

```powershell
$s1 = Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/sesiones" -Method Post -ContentType "application/json; charset=utf-8" -Body (@{ fecha="2026-04-08"; horaInicio="18:00"; horaFin="19:30"; lugar="Piscina"; tipo="ENTRENO"; estado="PLANIFICADA"; objetivo="Trabajo de piernas"; gimnasio=@{ objetivo="Fuerza"; notas="Sentadilla" }; natacion=@{ objetivo="I3"; notas="4x50" }; waterpolo=@{ objetivo="Táctica"; notas="6v6" } } | ConvertTo-Json -Depth 8) -WebSession $sJuanjo; $s1
```

### Listar sesiones por rango (una línea)

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/sesiones?desde=2026-04-01&hasta=2026-04-30" -WebSession $sJuanjo
```

### Obtener sesión por id (una línea)

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/sesiones/$($s1.id)" -WebSession $sJuanjo
```

## Asistencia de jugadores por sesión (usa `$s1` y `$j1` de arriba) — PowerShell

> Endpoints: `/api/planificaciones/{planId}/sesiones/{sesionId}/asistencias`  
> Marcar/actualizar: `PUT .../asistencias/{jugadorId}` (upsert). **Hazlo antes de borrar la sesión** (`$s1`).

### Marcar asistencia (una línea)

```powershell
$a1 = Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/sesiones/$($s1.id)/asistencias/$($j1.id)" -Method Put -ContentType "application/json; charset=utf-8" -Body (@{ estado="PRESENTE"; nota="llegó puntual" } | ConvertTo-Json) -WebSession $sJuanjo; $a1
```

### Cambiar a AUSENTE (una línea)

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/sesiones/$($s1.id)/asistencias/$($j1.id)" -Method Put -ContentType "application/json; charset=utf-8" -Body (@{ estado="AUSENTE"; nota="lesión" } | ConvertTo-Json) -WebSession $sJuanjo
```

### Listar asistencias de una sesión (una línea)

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/sesiones/$($s1.id)/asistencias" -WebSession $sJuanjo
```

### Borrar asistencia de un jugador (una línea)

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/sesiones/$($s1.id)/asistencias/$($j1.id)" -Method Delete -WebSession $sJuanjo
```

### Aislamiento: otro usuario no puede acceder al jugador (una línea)

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/jugadores/$($j1.id)" -WebSession $sJose
```

> Esperado: 404.

### Borrar jugador (una línea)

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/jugadores/$($j1.id)" -Method Delete -WebSession $sJuanjo
```

### Actualizar sesión (quitar Natación dejando `natacion=$null`) (una línea)

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/sesiones/$($s1.id)" -Method Put -ContentType "application/json; charset=utf-8" -Body (@{ fecha="2026-04-08"; horaInicio="18:15"; horaFin="19:45"; lugar="Piscina"; tipo="ENTRENO"; estado="REALIZADA"; objetivo="Ajustes"; notas="OK"; gimnasio=@{ objetivo="Fuerza"; notas="Sentadilla + core" }; natacion=$null; waterpolo=@{ objetivo="Táctica"; notas="superioridades" } } | ConvertTo-Json -Depth 8) -WebSession $sJuanjo
```

### Borrar sesión (una línea)

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/sesiones/$($s1.id)" -Method Delete -WebSession $sJuanjo
```

### Aislamiento: otro usuario no puede acceder (una línea)

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/sesiones/$($s1.id)" -WebSession $sJose
```

> Esperado: 404.

## Bancos de ejercicios (CRUD por entrenador) — PowerShell

> Endpoints: `/api/ejercicios/waterpolo`, `/api/ejercicios/gimnasio`, `/api/ejercicios/natacion`. Los ejercicios pertenecen al usuario logueado y son reutilizables entre sus planificaciones.

### Crear ejercicio de waterpolo (una línea)

```powershell
$ejW = Invoke-RestMethod -Uri "$base/api/ejercicios/waterpolo" -Method Post -ContentType "application/json; charset=utf-8" -Body (@{ nombre="6 contra 5"; objetivo="Superioridad numérica"; categoria="TACTICA"; intensidad="ALTA"; material="Balón"; duracionMinSugerida=20; jugadoresMin=11; jugadoresMax=14; descripcion="Ataque con un jugador menos" } | ConvertTo-Json) -WebSession $sJuanjo; $ejW
```

### Crear ejercicio de gimnasio (una línea)

```powershell
$ejG = Invoke-RestMethod -Uri "$base/api/ejercicios/gimnasio" -Method Post -ContentType "application/json; charset=utf-8" -Body (@{ nombre="Sentadilla con barra"; grupoMuscular="PIERNAS"; patron="SENTADILLA"; equipamiento="BARRA"; tipo="FUERZA"; unilateral=$false; descripcion="Compuesto bilateral" } | ConvertTo-Json) -WebSession $sJuanjo; $ejG
```

### Crear ejercicio de natación (una línea)

```powershell
$ejN = Invoke-RestMethod -Uri "$base/api/ejercicios/natacion" -Method Post -ContentType "application/json; charset=utf-8" -Body (@{ nombre="Series 100m crol"; estilo="CROL"; tipoBloque="PRINCIPAL"; intensidad="MEDIA"; material="NINGUNO"; metrosBase=100; descripcion="Trabajo aeróbico" } | ConvertTo-Json) -WebSession $sJuanjo; $ejN
```

### Listar y obtener (una línea cada uno)

```powershell
Invoke-RestMethod -Uri "$base/api/ejercicios/waterpolo" -WebSession $sJuanjo
```

```powershell
Invoke-RestMethod -Uri "$base/api/ejercicios/gimnasio/$($ejG.id)" -WebSession $sJuanjo
```

### Actualizar ejercicio (una línea)

```powershell
Invoke-RestMethod -Uri "$base/api/ejercicios/natacion/$($ejN.id)" -Method Put -ContentType "application/json; charset=utf-8" -Body (@{ nombre="Series 100m crol fuerte"; estilo="CROL"; tipoBloque="PRINCIPAL"; intensidad="ALTA"; material="NINGUNO"; metrosBase=100 } | ConvertTo-Json) -WebSession $sJuanjo
```

### Borrar ejercicio (una línea)

```powershell
Invoke-RestMethod -Uri "$base/api/ejercicios/waterpolo/$($ejW.id)" -Method Delete -WebSession $sJuanjo
```

## Asignación de ejercicios a una sesión — PowerShell

> Endpoints:
> - Waterpolo (lista ordenada con duración por ejercicio): `/api/planificaciones/{planId}/sesiones/{sesionId}/waterpolo/ejercicios`
> - Gimnasio (lista ordenada con series/reps/RIR/descanso): `/api/planificaciones/{planId}/sesiones/{sesionId}/gimnasio/ejercicios`
> - Natación (estructurado por bloques con items dentro): `/api/planificaciones/{planId}/sesiones/{sesionId}/natacion/bloques`

### Añadir ejercicio al bloque de waterpolo de una sesión (una línea)

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/sesiones/$($s1.id)/waterpolo/ejercicios" -Method Post -ContentType "application/json; charset=utf-8" -Body (@{ ejercicioId=$ejW.id; orden=1; duracionMin=15; notas="Empezar suave" } | ConvertTo-Json) -WebSession $sJuanjo
```

### Añadir ejercicio al bloque de gimnasio (una línea)

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/sesiones/$($s1.id)/gimnasio/ejercicios" -Method Post -ContentType "application/json; charset=utf-8" -Body (@{ ejercicioId=$ejG.id; orden=1; series=4; repeticiones=5; pesoKg=80; rir=2; descansoSeg=180 } | ConvertTo-Json) -WebSession $sJuanjo
```

## Sugerencias asistidas con Drools — PowerShell

> Endpoints: `/api/sugerencias/gimnasio` y `/api/sugerencias/natacion`. Las reglas se aplican sobre el banco del usuario logueado.

### Sugerir entreno de gimnasio (una línea)

```powershell
Invoke-RestMethod -Uri "$base/api/sugerencias/gimnasio" -Method Post -ContentType "application/json; charset=utf-8" -Body (@{ fase="PRINCIPAL"; grupos=@("PIERNAS","PECHO","CORE"); tipo="FUERZA"; intensidad="ALTA"; minutos=60; maxEjercicios=5 } | ConvertTo-Json) -WebSession $sJuanjo
```

### Sugerir entreno de natación (una línea)

```powershell
Invoke-RestMethod -Uri "$base/api/sugerencias/natacion" -Method Post -ContentType "application/json; charset=utf-8" -Body (@{ metrosObjetivo=2000; intensidad="MEDIA"; estiloPreferente="CROL"; conCalentamiento=$true; conVueltaCalma=$true; maxItems=6 } | ConvertTo-Json) -WebSession $sJuanjo
```

## Frontend (Vite + React + TypeScript)

La SPA queda disponible en `http://localhost:8081/` cuando se levanta el `docker compose`. Pantallas:

- `/login`, `/register` — autenticación con cookie de sesión.
- `/planificaciones` — listado y creación de planificaciones.
- `/planificaciones/:id` — detalle con pestañas Jugadores, Sesiones (calendario) y Sesión.
- `/ejercicios/{waterpolo|gimnasio|natacion}` — banco de ejercicios.
- `/sugerencias` — formulario que llama al motor Drools y muestra los ejercicios propuestos.

### Logout (una línea)

```powershell
Invoke-RestMethod -Uri "$base/api/auth/logout" -Method Post -WebSession $sJuanjo
```
