package com.poloplan.catalogo;

import java.util.List;

/**
 * Catálogo por defecto para nuevos entrenadores (waterpolo + sala + natación).
 * Grupos musculares: PECHO, ESPALDA, HOMBRO, BICEPS, TRICEPS, CUADRICEPS,
 *   ISQUIOS, GLUTEO, GEMELO, ANTEBRAZO, CORE, FULL_BODY
 */
public final class CatalogoPlantilla {

  private CatalogoPlantilla() {}

  public record Gym(String nombre, String grupo, String patron, String equipamiento, String tipo, boolean unilateral, String descripcion) {}
  public record Waterpolo(String nombre, String objetivo, String categoria, String intensidad, String material, Integer duracionMin, Integer jMin, Integer jMax, String descripcion) {}
  public record Natacion(String nombre, String estilo, String tipoBloque, String intensidad, String material, Integer metrosBase, String descripcion) {}

  public static List<Gym> gimnasio() {
    return List.of(

      // ── PECHO ────────────────────────────────────────────────────────
      new Gym("Press banca con barra",             "PECHO", "EMPUJE_HORIZONTAL", "BARRA",         "FUERZA",      false, "Banco elevado 10°, espalda entera apoyada, codos a 45°, sin bloquear"),
      new Gym("Press banca inclinado con barra",   "PECHO", "EMPUJE_HORIZONTAL", "BARRA",         "FUERZA",      false, "Banco entre 45° y 60°, barra hasta la clavícula"),
      new Gym("Press banca declinado",             "PECHO", "EMPUJE_HORIZONTAL", "BARRA",         "HIPERTROFIA", false, "Banco a -10°, énfasis en porción inferior del pecho"),
      new Gym("Press banca en multipower",         "PECHO", "EMPUJE_HORIZONTAL", "MAQUINA",       "HIPERTROFIA", false, "Igual que press con barra pero con guía fija"),
      new Gym("Press banca en prensa horizontal",  "PECHO", "EMPUJE_HORIZONTAL", "MAQUINA",       "HIPERTROFIA", false, "Espalda entera en respaldo, hombros y codos alineados, sin bloquear codos"),
      new Gym("Press banca con mancuernas",        "PECHO", "EMPUJE_HORIZONTAL", "MANCUERNA",     "HIPERTROFIA", false, "Mayor amplitud de movimiento, supinación opcional"),
      new Gym("Press banca inclinado con mancuernas","PECHO","EMPUJE_HORIZONTAL","MANCUERNA",     "HIPERTROFIA", false, "Banco a 45°-60°, mayor rango y estabilidad"),
      new Gym("Aperturas con mancuernas",          "PECHO", "EMPUJE_HORIZONTAL", "MANCUERNA",     "HIPERTROFIA", false, "Codos perpendiculares al cuerpo, descenso controlado hasta el plano del pecho"),
      new Gym("Pullover con barra",                "PECHO", "EMPUJE_HORIZONTAL", "BARRA",         "HIPERTROFIA", false, "Tumbado en banco, brazos extendidos, rango amplio"),
      new Gym("Flexiones",                         "PECHO", "EMPUJE_HORIZONTAL", "PESO_CORPORAL", "RESISTENCIA", false, "Manos algo más abiertas que hombros, espalda alineada, sin bloquear codos"),
      new Gym("Fondos en paralelas",               "PECHO", "EMPUJE_HORIZONTAL", "PESO_CORPORAL", "FUERZA",      false, "Manos hacia dentro, codos hacia afuera, axilas hasta la barra"),
      new Gym("Pectoral contractora",              "PECHO", "EMPUJE_HORIZONTAL", "MAQUINA",       "HIPERTROFIA", false, "Brazos paralelos al suelo, espalda completa en el respaldo"),
      new Gym("Aducciones polea alta",             "PECHO", "EMPUJE_HORIZONTAL", "POLEA",         "HIPERTROFIA", false, "Codos semi-flexionados, tronco inclinado 45°, juntar dedos por delante del cuerpo"),

      // ── ESPALDA ──────────────────────────────────────────────────────
      new Gym("Dominadas",                         "ESPALDA","TRACCION_VERTICAL",  "PESO_CORPORAL","FUERZA",      false, "Barbilla por encima de la barra, recorrido completo, sin balanceos"),
      new Gym("Dominadas agarre supino",           "ESPALDA","TRACCION_VERTICAL",  "PESO_CORPORAL","HIPERTROFIA", false, "Agarre supino a la anchura de hombros, mayor activación del bíceps"),
      new Gym("Jalón al pecho",                    "ESPALDA","TRACCION_VERTICAL",  "POLEA",        "HIPERTROFIA", false, "Piernas bien sujetas, barra hasta el trapecio por delante"),
      new Gym("Jalón dorsal polea alta",           "ESPALDA","TRACCION_VERTICAL",  "POLEA",        "HIPERTROFIA", false, "Inclinación leve hacia atrás, palmas hacia nosotros, codos pegados al cuerpo"),
      new Gym("Remo con barra",                    "ESPALDA","TRACCION_HORIZONTAL","BARRA",        "FUERZA",      false, "Tronco paralelo, espalda recta y fija, movimiento controlado"),
      new Gym("Remo con mancuerna a un brazo",     "ESPALDA","TRACCION_HORIZONTAL","MANCUERNA",    "HIPERTROFIA", true,  "Mano libre apoyada en banco, codo pegado al cuerpo, subida vertical"),
      new Gym("Remo pecho con mancuerna",          "ESPALDA","TRACCION_HORIZONTAL","MANCUERNA",    "HIPERTROFIA", true,  "Palma perpendicular al tronco, codo hacia afuera y lo más alto posible"),
      new Gym("Remo deltoide posterior con mancuerna","ESPALDA","TRACCION_HORIZONTAL","MANCUERNA", "HIPERTROFIA", true,  "Brazo hacia atrás pegado al cuerpo"),
      new Gym("Remo a una mano con barra",         "ESPALDA","TRACCION_HORIZONTAL","BARRA",        "FUERZA",      true,  "Variante unilateral con barra, gran activación dorsal"),
      new Gym("Remo polea baja",                   "ESPALDA","TRACCION_HORIZONTAL","POLEA",        "HIPERTROFIA", false, "Sentados, espalda recta, llevar agarre hasta pasar el tronco, sin balanceo"),
      new Gym("Remo al pecho polea baja",          "ESPALDA","TRACCION_HORIZONTAL","POLEA",        "HIPERTROFIA", false, "Agarre a la altura de las clavículas, palmas en pronación y codos abiertos"),
      new Gym("Remo prensa convencional",          "ESPALDA","TRACCION_HORIZONTAL","MAQUINA",      "HIPERTROFIA", false, "Mismo patrón que remo polea baja en máquina"),
      new Gym("Remo al pecho en prensa",           "ESPALDA","TRACCION_HORIZONTAL","MAQUINA",      "HIPERTROFIA", false, "Igual que remo al pecho polea baja pero en máquina"),
      new Gym("Remo en máquina",                   "ESPALDA","TRACCION_HORIZONTAL","MAQUINA",      "HIPERTROFIA", false, "Volumen dorsal con apoyo de pecho, sin demanda de estabilidad"),
      new Gym("Pullover con mancuerna",            "ESPALDA","TRACCION_VERTICAL",  "MANCUERNA",    "HIPERTROFIA", false, "Tumbado en banco, rango amplio para dorsal y serrato"),
      new Gym("Pullover en máquina",               "ESPALDA","TRACCION_VERTICAL",  "MAQUINA",      "HIPERTROFIA", false, "Recorrido controlado, énfasis en contracción dorsal"),
      new Gym("Pullover en polea alta",            "ESPALDA","TRACCION_VERTICAL",  "POLEA",        "HIPERTROFIA", false, "Dorsal y serrato con tensión constante"),
      new Gym("Face pull",                         "ESPALDA","TRACCION_HORIZONTAL","POLEA",        "HIPERTROFIA", false, "Deltoides posterior y escápulas, agarre a altura de ojos"),

      // ── HOMBRO ───────────────────────────────────────────────────────
      new Gym("Press militar con barra",           "HOMBRO","EMPUJE_VERTICAL",    "BARRA",         "FUERZA",      false, "De pie o sentado, barra desde hombros hacia arriba, sin arquear espalda"),
      new Gym("Press hombro tras nuca",            "HOMBRO","EMPUJE_VERTICAL",    "BARRA",         "FUERZA",      false, "Sentado, banco ligeramente reclinado, bajar hasta altura de muñecas"),
      new Gym("Press hombro con mancuernas",       "HOMBRO","EMPUJE_VERTICAL",    "MANCUERNA",     "HIPERTROFIA", false, "Palmas al frente, subir y bajar a la vez, con respaldo si hay carga alta"),
      new Gym("Press hombro en máquina",           "HOMBRO","EMPUJE_VERTICAL",    "MAQUINA",       "HIPERTROFIA", false, "Igual que press de hombros con barra, con guía fija"),
      new Gym("Press Arnold",                      "HOMBRO","EMPUJE_VERTICAL",    "MANCUERNA",     "HIPERTROFIA", false, "Recorrido completo con rotación de muñecas, mayor activación deltoides"),
      new Gym("Elevaciones laterales",             "HOMBRO","EMPUJE_VERTICAL",    "MANCUERNA",     "HIPERTROFIA", false, "Elevar hasta paralelo al suelo, no girar muñecas, codo fijo"),
      new Gym("Elevaciones laterales polea baja",  "HOMBRO","EMPUJE_VERTICAL",    "POLEA",         "HIPERTROFIA", true,  "Posición lateral a la polea, palma mirando la polea, tensión constante"),
      new Gym("Elevaciones laterales en máquina",  "HOMBRO","EMPUJE_VERTICAL",    "MAQUINA",       "HIPERTROFIA", false, "Mayor control del movimiento y tensión constante"),
      new Gym("Elevaciones frontales",             "HOMBRO","EMPUJE_VERTICAL",    "MANCUERNA",     "HIPERTROFIA", false, "Codos ligeramente flexionados, subir hasta paralelo al suelo"),
      new Gym("Pájaros con mancuernas",            "HOMBRO","TRACCION_HORIZONTAL","MANCUERNA",     "HIPERTROFIA", false, "Deltoides posterior, tronco horizontal, codos ligeramente flexionados"),
      new Gym("Pájaros en poleas",                 "HOMBRO","TRACCION_HORIZONTAL","POLEA",         "HIPERTROFIA", false, "Tensión constante en deltoides posterior durante todo el recorrido"),
      new Gym("Remo al mentón con barra",          "HOMBRO","EMPUJE_VERTICAL",    "BARRA",         "HIPERTROFIA", false, "Subir codos hacia arriba y afuera, barra pegada al cuerpo"),
      new Gym("Encogimiento de hombros",           "HOMBRO","ESTABILIDAD",        "MANCUERNA",     "HIPERTROFIA", false, "Carga pegada al cuerpo, subir hombros al máximo"),
      new Gym("Landmine press",                    "HOMBRO","EMPUJE_VERTICAL",    "BARRA",         "FUERZA",      true,  "Empuje unilateral funcional, buena transferencia deportiva"),

      // ── BÍCEPS ───────────────────────────────────────────────────────
      new Gym("Curl de bíceps con barra",          "BICEPS","TRACCION_HORIZONTAL","BARRA",         "HIPERTROFIA", false, "Agarre supino a la anchura de hombros, sin mover los codos ni impulso"),
      new Gym("Curl con mancuernas",               "BICEPS","TRACCION_HORIZONTAL","MANCUERNA",     "HIPERTROFIA", false, "Sentado o de pie, cuatro variantes: sin supinación, con supinación, con flexión de muñeca, en pronación"),
      new Gym("Curl martillo",                     "BICEPS","TRACCION_HORIZONTAL","MANCUERNA",     "HIPERTROFIA", false, "Agarre neutro, trabaja braquial y antebrazo"),
      new Gym("Curl en banco predicador",          "BICEPS","TRACCION_HORIZONTAL","BARRA",         "HIPERTROFIA", false, "Pecho y parte posterior de brazos pegados al acolchado, evitar hiperextensión"),
      new Gym("Curl en polea baja",                "BICEPS","TRACCION_HORIZONTAL","POLEA",         "HIPERTROFIA", false, "Tensión constante en toda la fase excéntrica y concéntrica"),

      // ── TRÍCEPS ──────────────────────────────────────────────────────
      new Gym("Press francés",                     "TRICEPS","EMPUJE_HORIZONTAL", "BARRA",         "HIPERTROFIA", false, "Tumbado, barra baja hasta la frente o detrás de la cabeza"),
      new Gym("Extensiones de tríceps en polea",   "TRICEPS","EMPUJE_HORIZONTAL", "POLEA",         "HIPERTROFIA", false, "Tronco ligeramente inclinado, brazos pegados al cuerpo, con barra o cuerda"),
      new Gym("Extensiones de tríceps sobre la cabeza en polea","TRICEPS","EMPUJE_VERTICAL","POLEA","HIPERTROFIA",false,"Inclinado hacia delante, cadera-hombro-codo alineados"),
      new Gym("Extensión de tríceps con mancuerna de pie","TRICEPS","EMPUJE_VERTICAL","MANCUERNA", "HIPERTROFIA", true,  "De pie, mano libre a la axila opuesta, hasta rozar trapecio"),
      new Gym("Patada de tríceps con mancuerna",   "TRICEPS","EMPUJE_HORIZONTAL", "MANCUERNA",     "HIPERTROFIA", true,  "Tronco paralelo, codo fijo, extensión completa sin sobrepasar vertical"),
      new Gym("Fondos en banco",                   "TRICEPS","EMPUJE_HORIZONTAL", "PESO_CORPORAL", "HIPERTROFIA", false, "Manos en banco detrás, descender controlado, tríceps y pecho inferior"),

      // ── CUÁDRICEPS ───────────────────────────────────────────────────
      new Gym("Sentadilla trasera con barra",      "CUADRICEPS","SENTADILLA",     "BARRA",         "FUERZA",      false, "Pies paralelos a la anchura de hombros, muslos paralelos al suelo, espalda recta"),
      new Gym("Sentadilla frontal",                "CUADRICEPS","SENTADILLA",     "BARRA",         "FUERZA",      false, "Barra en clavículas, torso vertical, mayor énfasis en cuádriceps"),
      new Gym("Sentadilla búlgara",                "CUADRICEPS","SENTADILLA",     "MANCUERNA",     "HIPERTROFIA", true,  "Pie trasero en banco, rodilla delantera sobre el pie"),
      new Gym("Prensa 45°",                        "CUADRICEPS","SENTADILLA",     "MAQUINA",       "HIPERTROFIA", false, "Espalda entera apoyada, distintas posiciones de pies"),
      new Gym("Extensiones de cuádriceps",         "CUADRICEPS","SENTADILLA",     "MAQUINA",       "HIPERTROFIA", false, "Eje de giro coincidente con rodilla, acolchados en el empeine"),
      new Gym("Zancada caminando",                 "CUADRICEPS","ZANCADA",        "MANCUERNA",     "HIPERTROFIA", true,  "Unilateral dinámico, buena transferencia a gestos deportivos"),
      new Gym("Zancada estática",                  "CUADRICEPS","ZANCADA",        "MANCUERNA",     "HIPERTROFIA", true,  "Control unilateral, rodilla delantera alineada con el pie"),
      new Gym("Step-up con mancuernas",            "CUADRICEPS","ZANCADA",        "MANCUERNA",     "HIPERTROFIA", true,  "Subir al cajón sin impulso, extensión completa de cadera"),
      new Gym("Sentadilla isométrica en pared",    "CUADRICEPS","SENTADILLA",     "PESO_CORPORAL", "RESISTENCIA", false, "Resistencia de cuádriceps, espalda pegada a la pared, 90° de rodilla"),
      new Gym("Sentadilla con salto",              "CUADRICEPS","SENTADILLA",     "PESO_CORPORAL", "POTENCIA",    false, "Descenso controlado, impulso máximo, amortiguación blanda"),

      // ── ISQUIOS ──────────────────────────────────────────────────────
      new Gym("Peso muerto convencional",          "ISQUIOS","BISAGRA",           "BARRA",         "FUERZA",      false, "Cadena posterior global, espalda neutra, cadera como bisagra"),
      new Gym("Peso muerto rumano",                "ISQUIOS","BISAGRA",           "BARRA",         "HIPERTROFIA", false, "Isquios y glúteo, rodillas ligeramente flexionadas, espalda recta"),
      new Gym("Peso muerto rumano con mancuernas", "ISQUIOS","BISAGRA",           "MANCUERNA",     "HIPERTROFIA", false, "Igual que con barra, mayor libertad de movimiento"),
      new Gym("Curl femoral tumbado",              "ISQUIOS","BISAGRA",           "MAQUINA",       "HIPERTROFIA", false, "Rótulas fuera del acolchado, soporte en el tendón de Aquiles"),
      new Gym("Nordic curl",                       "ISQUIOS","BISAGRA",           "PESO_CORPORAL", "FUERZA",      false, "Prevención de isquios, descenso excéntrico controlado"),

      // ── GLÚTEO ───────────────────────────────────────────────────────
      new Gym("Hip thrust con barra",              "GLUTEO","BISAGRA",            "BARRA",         "HIPERTROFIA", false, "Espalda apoyada en banco, extensión de cadera completa"),
      new Gym("Kettlebell swing",                  "GLUTEO","BISAGRA",            "KETTLEBELL",    "POTENCIA",    false, "Cadena posterior explosiva, impulso de cadera, control de core"),
      new Gym("Pull-through en polea",             "GLUTEO","BISAGRA",            "POLEA",         "HIPERTROFIA", false, "Glúteo con aprendizaje rápido, agarre entre piernas"),

      // ── GEMELO ───────────────────────────────────────────────────────
      new Gym("Elevación de gemelos de pie",       "GEMELO","ESTABILIDAD",        "MAQUINA",       "HIPERTROFIA", false, "Talones fuera de la plataforma, rango completo"),
      new Gym("Gemelo con mancuerna",              "GEMELO","ESTABILIDAD",        "MANCUERNA",     "HIPERTROFIA", true,  "Un pie en escalón, rodilla ligeramente flexionada"),
      new Gym("Gemelo sentado en máquina",         "GEMELO","ESTABILIDAD",        "MAQUINA",       "HIPERTROFIA", false, "Trabaja el sóleo, rodillas flexionadas a 90°"),

      // ── ANTEBRAZO ────────────────────────────────────────────────────
      new Gym("Flexiones de muñeca",               "ANTEBRAZO","ESTABILIDAD",     "BARRA",         "HIPERTROFIA", false, "De rodillas en banco, articulación libre, antebrazos apoyados"),
      new Gym("Extensiones de muñeca",             "ANTEBRAZO","ESTABILIDAD",     "BARRA",         "HIPERTROFIA", false, "De rodillas en banco, articulación libre, antebrazos apoyados"),
      new Gym("Farmer walk",                       "ANTEBRAZO","ESTABILIDAD",     "MANCUERNA",     "RESISTENCIA", false, "Agarre fuerte, paso controlado, core activado"),

      // ── CORE ─────────────────────────────────────────────────────────
      new Gym("Plancha frontal",                   "CORE","ESTABILIDAD",          "PESO_CORPORAL", "CORE",        false, "Anti-extensión, cuerpo rígido de talones a cabeza"),
      new Gym("Plancha lateral",                   "CORE","ESTABILIDAD",          "PESO_CORPORAL", "CORE",        true,  "Anti-lateral, cadera elevada, cuerpo alineado"),
      new Gym("Dead bug",                          "CORE","ESTABILIDAD",          "PESO_CORPORAL", "CORE",        false, "Control lumbo-pélvico, lumbar pegada al suelo"),
      new Gym("Pallof press",                      "CORE","ESTABILIDAD",          "POLEA",         "CORE",        false, "Anti-rotación, empuje y retirada controlada"),
      new Gym("Crunch en polea",                   "CORE","ESTABILIDAD",          "POLEA",         "HIPERTROFIA", false, "Flexión de tronco con resistencia, sin tirón de cuello"),
      new Gym("Hollow body hold",                  "CORE","ESTABILIDAD",          "PESO_CORPORAL", "CORE",        false, "Tensión global, lumbar pegada al suelo, brazos y piernas extendidos"),
      new Gym("Russian twist con disco",           "CORE","ROTACION",             "PESO_CORPORAL", "CORE",        false, "Rotación controlada, pies elevados, core siempre activo"),
      new Gym("Copenhagen plank",                  "CORE","ESTABILIDAD",          "PESO_CORPORAL", "CORE",        true,  "Aductores e ingle, pie superior en banco, cuerpo recto"),

      // ── FULL BODY ────────────────────────────────────────────────────
      new Gym("Burpees",                           "FULL_BODY","GENERAL",         "PESO_CORPORAL", "RESISTENCIA", false, "Condición general, secuencia fluida sin pausas"),
      new Gym("Remo en concept2",                  "FULL_BODY","TRACCION_HORIZONTAL","MAQUINA",    "RESISTENCIA", false, "Cardio de bajo impacto, cadena posterior y brazos"),
      new Gym("Battle ropes",                      "FULL_BODY","GENERAL",         "PESO_CORPORAL", "RESISTENCIA", false, "Alta intensidad metabólica, ondas alternas o simultáneas"),
      new Gym("Sled push",                         "FULL_BODY","SENTADILLA",      "MAQUINA",       "POTENCIA",    false, "Potencia horizontal, postura inclinada, pasos rápidos"),
      new Gym("Movilidad cadera 90/90",            "FULL_BODY","ESTABILIDAD",     "PESO_CORPORAL", "MOVILIDAD",   false, "Calentamiento articular de cadera, rotación interna y externa")
    );
  }

  public static List<Waterpolo> waterpolo() {
    return List.of(
      new Waterpolo("Balanza vertical (piernas verticales)", "Flotación y equilibrio", "TECNICA", "BAJA", "Ninguno", 8, 1, 14, "Piernas verticales, brazos fuera o sculling suave"),
      new Waterpolo("Balanza horizontal boca arriba", "Flotación y equilibrio", "TECNICA", "BAJA", "Ninguno", 8, 1, 14, "Cuerpo horizontal, cabeza atrás, cadera alta"),
      new Waterpolo("Balanza horizontal boca abajo", "Flotación y equilibrio", "TECNICA", "BAJA", "Ninguno", 8, 1, 14, "Cuerpo horizontal, mirada al fondo"),
      new Waterpolo("Balanza lateral", "Flotación y equilibrio", "TECNICA", "BAJA", "Ninguno", 6, 1, 14, "Un lado arriba, cambio de lado"),
      new Waterpolo("Eggbeater estático", "Sostener posición en vertical", "TECNICA", "MEDIA", "Ninguno", 10, 1, 14, "Patada alterna de eggbeater sin desplazamiento"),
      new Waterpolo("Eggbeater con manos en cabeza", "Estabilidad vertical", "TECNICA", "MEDIA", "Ninguno", 8, 1, 14, "Manos fuera del agua, torso erguido"),
      new Waterpolo("Piernas horizontal (patada de crol)", "Propulsión horizontal", "TECNICA", "MEDIA", "Tabla opcional", 12, 2, 14, "Manos en tabla o borde, patada de crol"),
      new Waterpolo("Piernas vertical (eggbeater)", "Sostén y desplazamiento vertical", "TECNICA", "MEDIA", "Ninguno", 12, 2, 14, "Desplazamiento en vertical solo con piernas"),
      new Waterpolo("Piernas vertical con balón", "Sostén con objeto", "TECNICA", "ALTA", "Balón", 10, 2, 12, "Eggbeater manteniendo balón arriba"),
      new Waterpolo("Patada de delfín en vertical", "Potencia de pierna", "FISICA", "ALTA", "Ninguno", 8, 2, 14, "Impulsos verticales tipo delfín"),
      new Waterpolo("Sculling de piernas en grupo", "Coordinación", "TECNICA", "BAJA", "Ninguno", 10, 4, 14, "Parejas o tríos sincronizados"),
      new Waterpolo("Pases por tríos en estático", "Precisión de pase", "TECNICA", "MEDIA", "Balón", 15, 3, 3, "Triángulo fijo, pase a mano y atrapada"),
      new Waterpolo("Pases por tríos en desplazamiento", "Pase en movimiento", "TECNICA", "MEDIA", "Balón", 15, 3, 3, "Trío avanza patada eggbeater"),
      new Waterpolo("Pase a la banda y recepción", "Pase lateral", "TECNICA", "MEDIA", "Balón", 12, 4, 8, "Dos bandas, rotación de pases"),
      new Waterpolo("Pase en movimiento 4v0", "Circulación de balón", "TECNICA", "MEDIA", "Balón", 15, 4, 8, "Cuadrado en desplazamiento"),
      new Waterpolo("Pase con finta previa", "Engaño y pase", "TECNICA", "ALTA", "Balón", 12, 2, 6, "Finta de tiro o pase y entrega"),
      new Waterpolo("Recepción y pase en una mano", "Control bajo presión", "TECNICA", "ALTA", "Balón", 12, 2, 6, "Recepción protegida"),
      new Waterpolo("Lanzamiento desde el agua", "Tiro con base", "TECNICA", "MEDIA", "Balón, portería", 15, 2, 14, "Recepción y tiro sin botar"),
      new Waterpolo("Lanzamiento contra pared", "Volumen de tiro", "TECNICA", "MEDIA", "Balón, pared", 12, 1, 14, "Serie de tiros rápidos a pared"),
      new Waterpolo("Tiro desde 5 metros", "Tiro lejano", "TECNICA", "ALTA", "Balón, portería", 15, 2, 14, "Posición de penal o exterior"),
      new Waterpolo("Tiro en movimiento lateral", "Tiro en desplazamiento", "TECNICA", "ALTA", "Balón, portería", 12, 2, 8, "Eggbeater + giro de hombros"),
      new Waterpolo("Tiro tras recepción en superioridad", "Tiro táctico", "TACTICA", "ALTA", "Balón, portería", 15, 6, 8, "Simular 6v5"),
      new Waterpolo("Remate de centro", "Finalización", "TECNICA", "ALTA", "Balón, portería", 12, 4, 8, "Centros desde banda"),
      new Waterpolo("Tiro con oposición (1v1 portero)", "Toma de decisión", "TACTICA", "ALTA", "Balón, portería", 15, 2, 2, "Delantero vs portero"),
      new Waterpolo("6 contra 5 (superioridad)", "Ataque en superioridad", "TACTICA", "ALTA", "Balón, porterías", 20, 12, 14, "Situación real de mano"),
      new Waterpolo("5 contra 6 (inferioridad)", "Defensa en inferioridad", "TACTICA", "ALTA", "Balón, porterías", 20, 12, 14, "Bloqueo y transición"),
      new Waterpolo("Contraataque 3 contra 2", "Transición rápida", "TACTICA", "ALTA", "Balón, porterías", 15, 6, 8, "Desde recuperación a gol"),
      new Waterpolo("Juego reducido 4 contra 4", "Toma de decisiones", "TACTICA", "MEDIA", "Balón, porterías", 20, 8, 10, "Medio campo"),
      new Waterpolo("Situación de centro y segunda línea", "Ataque posicional", "TACTICA", "MEDIA", "Balón, porterías", 18, 10, 14, "Centros y rebotes"),
      new Waterpolo("Presión en zona en 6v6", "Defensa organizada", "TACTICA", "ALTA", "Balón, porterías", 20, 12, 14, "Defensa por zonas"),
      new Waterpolo("Circuito de fuerza en piscina", "Fuerza general", "FISICA", "ALTA", "Balón, conos", 25, 6, 14,
        "Estaciones: 1) Eggbeater máx 30s 2) Pase largo x20 3) Tiro potente x10 4) Piernas vertical 40s 5) Plancha en borde 30s 6) Sprint 15m x4. Rotar 3-4 rondas."),
      new Waterpolo("Sprints 15 metros con balón", "Velocidad con balón", "FISICA", "ALTA", "Balón", 12, 2, 14, "Salidas desde agua o borde"),
      new Waterpolo("Sprints 25 metros sin balón", "Velocidad pura", "FISICA", "ALTA", "Ninguno", 10, 2, 14, "Series de velocidad"),
      new Waterpolo("Resistencia 4x100 metros", "Capacidad aeróbica", "FISICA", "MEDIA", "Ninguno", 20, 4, 14, "Nado continuo o con pausa corta"),
      new Waterpolo("Duelos 1 contra 1 en banda", "Fuerza y habilidad", "FISICA", "ALTA", "Balón", 15, 2, 2, "Ganar la banda"),
      new Waterpolo("Saltos verticales desde el agua", "Potencia", "FISICA", "ALTA", "Ninguno", 10, 2, 14, "Máx altura de salida"),
      new Waterpolo("Rondo 4v2 en espacio reducido", "Pase y presión", "JUEGO", "MEDIA", "Balón", 12, 6, 8, "Posesión en cuadrado"),
      new Waterpolo("Partido adaptado 7v7", "Integración global", "JUEGO", "MEDIA", "Balón, porterías", 30, 14, 16, "Reglas simplificadas"),
      new Waterpolo("Control de balón en eggbeater", "Manejo de balón", "TECNICA", "BAJA", "Balón", 10, 2, 14, "Golpes suaves de control"),
      new Waterpolo("Regate 1v1 en banda", "Superar defensa", "TECNICA", "ALTA", "Balón", 12, 2, 2, "Finta y salida"),
      new Waterpolo("Bloqueo y desplazamiento defensivo", "Defensa individual", "TACTICA", "MEDIA", "Balón", 15, 2, 2, "Mantener posición"),
      new Waterpolo("Transición defensa-ataque 6v6", "Cambio de fase", "TACTICA", "ALTA", "Balón, porterías", 20, 12, 14, "Tras pérdida o recuperación"),
      new Waterpolo("Penaltis (serie de 5)", "Ejecución bajo presión", "TACTICA", "ALTA", "Balón, portería", 15, 2, 14, "Línea de 5m"),
      new Waterpolo("Calentamiento técnico general", "Activación", "TECNICA", "BAJA", "Balón", 15, 4, 14, "Pases, control, piernas suaves")
    );
  }

  public static List<Natacion> natacion() {
    return List.of(
      new Natacion("Calentamiento crol suave", "CROL", "CALENTAMIENTO", "BAJA", "NINGUNO", 400, "400 m crol fácil"),
      new Natacion("Calentamiento variado estilos", "ESTILOS", "CALENTAMIENTO", "BAJA", "NINGUNO", 300, "Mezcla de estilos"),
      new Natacion("Técnica crol — brazada", "CROL", "TECNICA", "BAJA", "NINGUNO", 50, "Series cortas enfocadas en brazada"),
      new Natacion("Técnica crol — piernas con tabla", "CROL", "TECNICA", "BAJA", "TABLA", 50, "Patada con tabla"),
      new Natacion("Pull crol con boya", "CROL", "PRINCIPAL", "MEDIA", "PULL", 100, "Solo brazos con pull-buoy"),
      new Natacion("Series 100 m crol", "CROL", "PRINCIPAL", "MEDIA", "NINGUNO", 100, "Bloque principal aeróbico"),
      new Natacion("Series 50 m crol fuerte", "CROL", "VELOCIDAD", "ALTA", "NINGUNO", 50, "Umbral/VO2 corto"),
      new Natacion("Series 200 m crol", "CROL", "RESISTENCIA", "MEDIA", "NINGUNO", 200, "Resistencia aeróbica"),
      new Natacion("Series 25 m sprint", "CROL", "VELOCIDAD", "ALTA", "NINGUNO", 25, "Máxima velocidad"),
      new Natacion("Nado espalda técnico", "ESPALDA", "PRINCIPAL", "MEDIA", "NINGUNO", 100, "Trabajo de espalda"),
      new Natacion("Series braza 50 m", "BRAZA", "PRINCIPAL", "MEDIA", "NINGUNO", 50, "Braza controlada"),
      new Natacion("Mariposa técnica 25 m", "MARIPOSA", "TECNICA", "ALTA", "NINGUNO", 25, "Técnica mariposa"),
      new Natacion("Estilos 4x100", "ESTILOS", "PRINCIPAL", "MEDIA", "NINGUNO", 100, "Crol, espalda, braza, mariposa"),
      new Natacion("Pies con aletas", "PIES", "PRINCIPAL", "MEDIA", "ALETAS", 50, "Trabajo de pierna con aletas"),
      new Natacion("Brazos con palas", "BRAZOS", "PRINCIPAL", "MEDIA", "PALAS", 50, "Potencia de brazada"),
      new Natacion("Combinado piernas/brazos", "MIXTO", "PRINCIPAL", "MEDIA", "TABLA", 100, "Alternar piernas y brazos"),
      new Natacion("Vuelta a la calma crol", "CROL", "VUELTA_CALMA", "BAJA", "NINGUNO", 200, "200 m suaves"),
      new Natacion("Vuelta a la calma espalda", "ESPALDA", "VUELTA_CALMA", "BAJA", "NINGUNO", 200, "Relajación espalda"),
      new Natacion("Recuperación activa 100 m", "CROL", "RECUPERACION", "BAJA", "NINGUNO", 100, "Entre bloques intensos"),
      new Natacion("Test 400 m continuo", "CROL", "RESISTENCIA", "MEDIA", "NINGUNO", 400, "Referencia de ritmo"),
      new Natacion("Piramide 50-100-150-100-50", "CROL", "PRINCIPAL", "MEDIA", "NINGUNO", 100, "Progresión de metros"),
      new Natacion("Series con tubo respirador", "CROL", "TECNICA", "MEDIA", "TUBO", 50, "Trabajo de rollo y alineación")
    );
  }
}
