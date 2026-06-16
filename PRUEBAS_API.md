# Pruebas de la API — PowerShell

Comandos para probar todos los endpoints con autenticación por cookie (sesión).  
Requiere la aplicación levantada con `docker compose up -d --build`.

## Preparación (ejecutar una vez por sesión de terminal)

```powershell
$base = "http://localhost:8080"
$sJuanjo = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$sJose   = New-Object Microsoft.PowerShell.Commands.WebRequestSession
```

## Auth

### Registrar usuarios

```powershell
Invoke-RestMethod -Uri "$base/api/auth/register" -Method Post -ContentType "application/json; charset=utf-8" -Body (@{ email="juanjo@ejemplo.com"; password="Juanjo123"; name="Juanjo" } | ConvertTo-Json) -WebSession $sJuanjo
```

```powershell
Invoke-RestMethod -Uri "$base/api/auth/register" -Method Post -ContentType "application/json; charset=utf-8" -Body (@{ email="jose@ejemplo.com"; password="Jose1234"; name="Jose" } | ConvertTo-Json) -WebSession $sJose
```

> Si el usuario ya existe devuelve 409. En ese caso usa el login.

### Login

```powershell
Invoke-RestMethod -Uri "$base/api/auth/login" -Method Post -ContentType "application/json; charset=utf-8" -Body (@{ email="juanjo@ejemplo.com"; password="Juanjo123" } | ConvertTo-Json) -WebSession $sJuanjo
```

```powershell
Invoke-RestMethod -Uri "$base/api/auth/login" -Method Post -ContentType "application/json; charset=utf-8" -Body (@{ email="jose@ejemplo.com"; password="Jose1234" } | ConvertTo-Json) -WebSession $sJose
```

### /me

```powershell
Invoke-RestMethod -Uri "$base/api/auth/me" -WebSession $sJuanjo
```

```powershell
Invoke-RestMethod -Uri "$base/api/auth/me" -WebSession $sJose
```

### Logout

```powershell
Invoke-RestMethod -Uri "$base/api/auth/logout" -Method Post -WebSession $sJuanjo
```

---

## Planificaciones

### Crear

```powershell
$pJuanjo1 = Invoke-RestMethod -Uri "$base/api/planificaciones" -Method Post -ContentType "application/json; charset=utf-8" -Body (@{ nombre="Plan Juanjo #1"; fechaInicio="2026-04-01"; fechaFin="2026-04-30"; notas="Solo Juanjo" } | ConvertTo-Json) -WebSession $sJuanjo; $pJuanjo1
```

```powershell
$pJose1 = Invoke-RestMethod -Uri "$base/api/planificaciones" -Method Post -ContentType "application/json; charset=utf-8" -Body (@{ nombre="Plan Jose #1"; fechaInicio="2026-05-01"; fechaFin="2026-05-31"; notas="Solo Jose" } | ConvertTo-Json) -WebSession $sJose; $pJose1
```

### Aislamiento entre usuarios

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJose1.id)" -WebSession $sJuanjo
```

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)" -WebSession $sJose
```

> Esperado: 404 en ambos casos.

---

## Jugadores (CRUD)

> Endpoints: `/api/planificaciones/{planId}/jugadores`

### Crear

```powershell
$j1 = Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/jugadores" -Method Post -ContentType "application/json; charset=utf-8" -Body (@{ nombre="Pepe"; apellidos="Pérez"; fechaNacimiento="2004-01-15"; posicion="Boya"; notas="capitán" } | ConvertTo-Json) -WebSession $sJuanjo; $j1
```

### Listar

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/jugadores" -WebSession $sJuanjo
```

### Obtener por id

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/jugadores/$($j1.id)" -WebSession $sJuanjo
```

### Actualizar

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/jugadores/$($j1.id)" -Method Put -ContentType "application/json; charset=utf-8" -Body (@{ nombre="Pepe"; apellidos="Pérez"; fechaNacimiento="2004-01-15"; posicion="Defensa"; notas="actualizado" } | ConvertTo-Json) -WebSession $sJuanjo
```

### Borrar (hacer después de las pruebas de asistencia)

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/jugadores/$($j1.id)" -Method Delete -WebSession $sJuanjo
```

### Aislamiento

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/jugadores/$($j1.id)" -WebSession $sJose
```

> Esperado: 404.

---

## Sesiones del día (CRUD + rango)

> Endpoints: `/api/planificaciones/{planId}/sesiones`  
> Cada sesión puede tener hasta 3 partes: `gimnasio`, `natacion`, `waterpolo`.

### Crear con las 3 partes

```powershell
$s1 = Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/sesiones" -Method Post -ContentType "application/json; charset=utf-8" -Body (@{ fecha="2026-04-08"; horaInicio="18:00"; horaFin="19:30"; lugar="Piscina"; tipo="ENTRENO"; estado="PLANIFICADA"; objetivo="Trabajo de piernas"; gimnasio=@{ objetivo="Fuerza"; notas="Sentadilla" }; natacion=@{ objetivo="I3"; notas="4x50" }; waterpolo=@{ objetivo="Táctica"; notas="6v6" } } | ConvertTo-Json -Depth 8) -WebSession $sJuanjo; $s1
```

### Listar por rango

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/sesiones?desde=2026-04-01&hasta=2026-04-30" -WebSession $sJuanjo
```

### Obtener por id

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/sesiones/$($s1.id)" -WebSession $sJuanjo
```

### Actualizar (quitar natación)

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/sesiones/$($s1.id)" -Method Put -ContentType "application/json; charset=utf-8" -Body (@{ fecha="2026-04-08"; horaInicio="18:15"; horaFin="19:45"; lugar="Piscina"; tipo="ENTRENO"; estado="REALIZADA"; objetivo="Ajustes"; notas="OK"; gimnasio=@{ objetivo="Fuerza"; notas="Sentadilla + core" }; natacion=$null; waterpolo=@{ objetivo="Táctica"; notas="superioridades" } } | ConvertTo-Json -Depth 8) -WebSession $sJuanjo
```

### Borrar

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/sesiones/$($s1.id)" -Method Delete -WebSession $sJuanjo
```

### Aislamiento

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/sesiones/$($s1.id)" -WebSession $sJose
```

> Esperado: 404.

---

## Asistencia de jugadores por sesión

> Endpoints: `/api/planificaciones/{planId}/sesiones/{sesionId}/asistencias`

### Marcar asistencia

```powershell
$a1 = Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/sesiones/$($s1.id)/asistencias/$($j1.id)" -Method Put -ContentType "application/json; charset=utf-8" -Body (@{ estado="PRESENTE"; nota="llegó puntual" } | ConvertTo-Json) -WebSession $sJuanjo; $a1
```

### Cambiar a AUSENTE

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/sesiones/$($s1.id)/asistencias/$($j1.id)" -Method Put -ContentType "application/json; charset=utf-8" -Body (@{ estado="AUSENTE"; nota="lesión" } | ConvertTo-Json) -WebSession $sJuanjo
```

### Listar asistencias de una sesión

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/sesiones/$($s1.id)/asistencias" -WebSession $sJuanjo
```

### Borrar asistencia

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/sesiones/$($s1.id)/asistencias/$($j1.id)" -Method Delete -WebSession $sJuanjo
```

---

## Banco de ejercicios (CRUD)

> Endpoints: `/api/ejercicios/waterpolo`, `/api/ejercicios/gimnasio`, `/api/ejercicios/natacion`

### Crear ejercicio de waterpolo

```powershell
$ejW = Invoke-RestMethod -Uri "$base/api/ejercicios/waterpolo" -Method Post -ContentType "application/json; charset=utf-8" -Body (@{ nombre="6 contra 5"; objetivo="Superioridad numérica"; categoria="TACTICA"; intensidad="ALTA"; material="Balón"; duracionMinSugerida=20; jugadoresMin=11; jugadoresMax=14; descripcion="Ataque con un jugador menos" } | ConvertTo-Json) -WebSession $sJuanjo; $ejW
```

### Crear ejercicio de gimnasio

```powershell
$ejG = Invoke-RestMethod -Uri "$base/api/ejercicios/gimnasio" -Method Post -ContentType "application/json; charset=utf-8" -Body (@{ nombre="Sentadilla con barra"; grupoMuscular="PIERNAS"; patron="SENTADILLA"; equipamiento="BARRA"; tipo="FUERZA"; unilateral=$false; descripcion="Compuesto bilateral" } | ConvertTo-Json) -WebSession $sJuanjo; $ejG
```

### Crear ejercicio de natación

```powershell
$ejN = Invoke-RestMethod -Uri "$base/api/ejercicios/natacion" -Method Post -ContentType "application/json; charset=utf-8" -Body (@{ nombre="Series 100m crol"; estilo="CROL"; tipoBloque="PRINCIPAL"; intensidad="MEDIA"; material="NINGUNO"; metrosBase=100; descripcion="Trabajo aeróbico" } | ConvertTo-Json) -WebSession $sJuanjo; $ejN
```

### Listar y obtener

```powershell
Invoke-RestMethod -Uri "$base/api/ejercicios/waterpolo" -WebSession $sJuanjo
```

```powershell
Invoke-RestMethod -Uri "$base/api/ejercicios/gimnasio/$($ejG.id)" -WebSession $sJuanjo
```

### Actualizar

```powershell
Invoke-RestMethod -Uri "$base/api/ejercicios/natacion/$($ejN.id)" -Method Put -ContentType "application/json; charset=utf-8" -Body (@{ nombre="Series 100m crol fuerte"; estilo="CROL"; tipoBloque="PRINCIPAL"; intensidad="ALTA"; material="NINGUNO"; metrosBase=100 } | ConvertTo-Json) -WebSession $sJuanjo
```

### Borrar

```powershell
Invoke-RestMethod -Uri "$base/api/ejercicios/waterpolo/$($ejW.id)" -Method Delete -WebSession $sJuanjo
```

---

## Asignación de ejercicios a una sesión

> Endpoints:
> - Waterpolo: `/api/planificaciones/{planId}/sesiones/{sesionId}/waterpolo/ejercicios`
> - Gimnasio: `/api/planificaciones/{planId}/sesiones/{sesionId}/gimnasio/ejercicios`
> - Natación (por bloques): `/api/planificaciones/{planId}/sesiones/{sesionId}/natacion/bloques`

### Añadir ejercicio al bloque de waterpolo

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/sesiones/$($s1.id)/waterpolo/ejercicios" -Method Post -ContentType "application/json; charset=utf-8" -Body (@{ ejercicioId=$ejW.id; orden=1; duracionMin=15; notas="Empezar suave" } | ConvertTo-Json) -WebSession $sJuanjo
```

### Añadir ejercicio al bloque de gimnasio

```powershell
Invoke-RestMethod -Uri "$base/api/planificaciones/$($pJuanjo1.id)/sesiones/$($s1.id)/gimnasio/ejercicios" -Method Post -ContentType "application/json; charset=utf-8" -Body (@{ ejercicioId=$ejG.id; orden=1; series=4; repeticiones=5; pesoKg=80; rir=2; descansoSeg=180 } | ConvertTo-Json) -WebSession $sJuanjo
```

---

## Sugerencias con Drools

> Endpoints: `/api/sugerencias/gimnasio`, `/api/sugerencias/natacion`

### Sugerir entreno de gimnasio

```powershell
Invoke-RestMethod -Uri "$base/api/sugerencias/gimnasio" -Method Post -ContentType "application/json; charset=utf-8" -Body (@{ fase="PRINCIPAL"; grupos=@("PIERNAS","PECHO","CORE"); tipo="FUERZA"; intensidad="ALTA"; minutos=60; maxEjercicios=5 } | ConvertTo-Json) -WebSession $sJuanjo
```

### Sugerir entreno de natación

```powershell
Invoke-RestMethod -Uri "$base/api/sugerencias/natacion" -Method Post -ContentType "application/json; charset=utf-8" -Body (@{ metrosObjetivo=2000; intensidad="MEDIA"; estiloPreferente="CROL"; conCalentamiento=$true; conVueltaCalma=$true; maxItems=6 } | ConvertTo-Json) -WebSession $sJuanjo
```
