import { FormEvent, useState } from "react";
import { api } from "../lib/api";

interface SugerenciaGym {
  ejercicioId: number;
  nombre: string;
  grupoMuscular?: string;
  patron?: string;
  equipamiento?: string;
  tipo?: string;
  orden?: number;
  series?: number;
  repeticiones?: number;
  porcRm?: number;
  rir?: number;
  descansoSeg?: number;
  tempo?: string;
  puntuacion?: number;
  motivo?: string;
}
interface BloqueNat {
  tipo: string;
  descripcion?: string;
  intensidadAE?: string;
  series?: number;
  metrosPorSerie?: number;
  descansoSeg?: number;
  material?: string;
  metrosTotales?: number;
  cargaEstimada?: number;
  motivo?: string;
}
interface NatResp {
  objetivo: string;
  volumenTotal: number;
  cargaTotal: number;
  bloques: BloqueNat[];
}

export default function SugerenciasPage() {
  return (
    <div>
      <h1>Sugerencias asistidas (Drools)</h1>
      <p className="muted">
        El motor combina las reglas con tu banco de ejercicios para proponerte sesiones personalizadas.
        Cada ejercicio recibe series, repeticiones, %RM, RIR, descanso y tempo adaptados a sus
        características y a la sesión.
      </p>
      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 16 }}>
        <div className="card"><h3>Gimnasio</h3><SugerirGimnasio /></div>
        <div className="card"><h3>Natación</h3><SugerirNatacion /></div>
      </div>
    </div>
  );
}

function SugerirGimnasio() {
  const [fase, setFase] = useState("PRINCIPAL");
  const [tipo, setTipo] = useState("HIPERTROFIA");
  const [intensidad, setIntensidad] = useState("MEDIA");
  const [nivel, setNivel] = useState("INTERMEDIO");
  const [grupos, setGrupos] = useState<string[]>([]);
  const [excluirEq, setExcluirEq] = useState<string[]>([]);
  const [minutos, setMinutos] = useState("60");
  const [max, setMax] = useState("6");
  const [resultado, setResultado] = useState<SugerenciaGym[] | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setSubmitting(true); setError(null);
    try {
      const res = await api.post<{ sugerencias: SugerenciaGym[] }>("/api/sugerencias/gimnasio", {
        fase, tipo, intensidad, nivel, grupos, excluirEquipamiento: excluirEq,
        minutos: minutos ? Number(minutos) : null,
        maxEjercicios: max ? Number(max) : null,
      });
      setResultado(res.sugerencias);
    } catch (err: any) { setError(err.message); } finally { setSubmitting(false); }
  };

  const toggle = (arr: string[], v: string) => arr.includes(v) ? arr.filter((x) => x !== v) : [...arr, v];

  return (
    <form onSubmit={onSubmit}>
      <div className="row">
        <div className="row cols-4">
          <div>
            <label>Fase</label>
            <select value={fase} onChange={(e) => setFase(e.target.value)}>
              <option value="PRINCIPAL">Principal</option><option value="AUXILIAR">Auxiliar</option>
              <option value="ACTIVACION">Activación</option><option value="RECUPERACION">Recuperación</option>
            </select>
          </div>
          <div>
            <label>Tipo</label>
            <select value={tipo} onChange={(e) => setTipo(e.target.value)}>
              {["FUERZA","HIPERTROFIA","POTENCIA","RESISTENCIA","MOVILIDAD","ACTIVACION"].map((t) => <option key={t} value={t}>{t}</option>)}
            </select>
          </div>
          <div>
            <label>Intensidad</label>
            <select value={intensidad} onChange={(e) => setIntensidad(e.target.value)}>
              <option value="BAJA">Baja</option><option value="MEDIA">Media</option><option value="ALTA">Alta</option>
            </select>
          </div>
          <div>
            <label>Nivel</label>
            <select value={nivel} onChange={(e) => setNivel(e.target.value)}>
              <option value="INICIACION">Iniciación</option>
              <option value="INTERMEDIO">Intermedio</option>
              <option value="AVANZADO">Avanzado</option>
            </select>
          </div>
        </div>
        <div>
          <label>Grupos musculares prioritarios</label>
          <div style={{ display: "flex", gap: 6, flexWrap: "wrap" }}>
            {["PECTORAL","ESPALDA","HOMBRO","CUADRICEPS","ISQUIOS","GLUTEO","CORE","FULL_BODY","BICEPS","TRICEPS"].map((g) => (
              <label key={g} style={{ display: "inline-flex", gap: 4, alignItems: "center" }}>
                <input type="checkbox" checked={grupos.includes(g)} onChange={() => setGrupos(toggle(grupos, g))} /> {g}
              </label>
            ))}
          </div>
        </div>
        <div>
          <label>Equipamiento NO disponible</label>
          <div style={{ display: "flex", gap: 6, flexWrap: "wrap" }}>
            {["BARRA","MANCUERNA","MAQUINA","POLEA","KETTLEBELL","PESO_CORPORAL","BANDAS"].map((g) => (
              <label key={g} style={{ display: "inline-flex", gap: 4, alignItems: "center" }}>
                <input type="checkbox" checked={excluirEq.includes(g)} onChange={() => setExcluirEq(toggle(excluirEq, g))} /> {g}
              </label>
            ))}
          </div>
        </div>
        <div className="row cols-2">
          <div><label>Minutos</label><input type="number" min={10} max={240} value={minutos} onChange={(e) => setMinutos(e.target.value)} /></div>
          <div><label>Máx. ejercicios</label><input type="number" min={1} max={20} value={max} onChange={(e) => setMax(e.target.value)} /></div>
        </div>
      </div>
      <div className="dialog-actions">
        <button className="primary" type="submit" disabled={submitting}>{submitting ? "Calculando…" : "Sugerir"}</button>
      </div>
      {error && <p className="error">{error}</p>}
      {resultado && (
        <div style={{ marginTop: 12 }}>
          {resultado.length === 0 ? <p className="muted">Sin sugerencias. Crea más ejercicios o cambia el objetivo.</p> : (
            <table>
              <thead>
                <tr>
                  <th>#</th><th>Ejercicio</th><th>Tipo</th><th>Series×Reps</th>
                  <th>%RM</th><th>RIR</th><th>Desc</th><th>Tempo</th><th>Motivo</th>
                </tr>
              </thead>
              <tbody>
                {resultado.map((r) => (
                  <tr key={r.ejercicioId}>
                    <td><strong>{r.orden ?? ""}</strong></td>
                    <td>{r.nombre}<br /><span className="muted" style={{ fontSize: 11 }}>{r.grupoMuscular} · {r.patron}</span></td>
                    <td>{r.tipo ?? ""}</td>
                    <td><strong>{r.series ?? "-"}×{r.repeticiones ?? "-"}</strong></td>
                    <td>{r.porcRm != null ? `${r.porcRm}%` : "-"}</td>
                    <td>{r.rir ?? "-"}</td>
                    <td>{r.descansoSeg ?? ""}s</td>
                    <td>{r.tempo ?? "-"}</td>
                    <td className="muted" style={{ fontSize: 11 }}>{r.motivo}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      )}
    </form>
  );
}

function SugerirNatacion() {
  const [objetivo, setObjetivo] = useState("POTENCIA_AEROBICA");
  const [metros, setMetros] = useState("2500");
  const [nivel, setNivel] = useState("INTERMEDIO");
  const [estilo, setEstilo] = useState("LIBRE");
  const [calentamiento, setCalentamiento] = useState(true);
  const [vuelta, setVuelta] = useState(true);
  const [resp, setResp] = useState<NatResp | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setSubmitting(true); setError(null);
    try {
      const r = await api.post<NatResp>(
        "/api/sugerencias/natacion",
        {
          objetivo,
          metrosObjetivo: Number(metros),
          conCalentamiento: calentamiento,
          conVueltaCalma: vuelta,
          nivel,
          estiloPreferente: estilo,
        }
      );
      setResp(r);
    } catch (err: any) { setError(err.message); } finally { setSubmitting(false); }
  };

  return (
    <form onSubmit={onSubmit}>
      <div className="row">
        <div className="row cols-2">
          <div>
            <label>Objetivo fisiológico</label>
            <select value={objetivo} onChange={(e) => setObjetivo(e.target.value)}>
              <option value="VELOCIDAD">Velocidad (AE5)</option>
              <option value="POTENCIA_ANAEROBICA_LACTICA">Potencia anaeróbica láctica (AE4)</option>
              <option value="CAPACIDAD_ANAEROBICA_LACTICA">Capacidad anaeróbica láctica (AE4)</option>
              <option value="UMBRAL_ANAEROBICO_LACTICO">Umbral anaeróbico láctico (AE3)</option>
              <option value="POTENCIA_AEROBICA">Potencia aeróbica / VO2max (AE3)</option>
              <option value="CAPACIDAD_AEROBICA">Capacidad aeróbica (AE2)</option>
              <option value="UMBRAL_AEROBICO">Umbral aeróbico (AE2)</option>
            </select>
          </div>
          <div>
            <label>Metros objetivo</label>
            <input type="number" min={500} max={8000} step={100} value={metros} onChange={(e) => setMetros(e.target.value)} />
          </div>
        </div>
        <div className="row cols-2">
          <div>
            <label>Nivel</label>
            <select value={nivel} onChange={(e) => setNivel(e.target.value)}>
              <option value="INICIACION">Iniciación</option>
              <option value="INTERMEDIO">Intermedio</option>
              <option value="AVANZADO">Avanzado</option>
            </select>
          </div>
          <div>
            <label>Estilo preferente</label>
            <select value={estilo} onChange={(e) => setEstilo(e.target.value)}>
              {["LIBRE","ESPALDA","BRAZA","MARIPOSA","COMBINADO"].map((s) => <option key={s} value={s}>{s}</option>)}
            </select>
          </div>
        </div>
        <div style={{ display: "flex", gap: 12 }}>
          <label style={{ display: "inline-flex", gap: 4 }}>
            <input type="checkbox" checked={calentamiento} onChange={(e) => setCalentamiento(e.target.checked)} /> Calentamiento
          </label>
          <label style={{ display: "inline-flex", gap: 4 }}>
            <input type="checkbox" checked={vuelta} onChange={(e) => setVuelta(e.target.checked)} /> Vuelta a la calma
          </label>
        </div>
      </div>
      <div className="dialog-actions">
        <button className="primary" type="submit" disabled={submitting}>{submitting ? "Calculando…" : "Sugerir"}</button>
      </div>
      {error && <p className="error">{error}</p>}
      {resp && (
        <div style={{ marginTop: 12 }}>
          <p className="muted">
            Volumen total: <strong>{resp.volumenTotal} m</strong> ·
            Carga estimada: <strong>{Math.round(resp.cargaTotal)}</strong> ·
            Objetivo: <strong>{resp.objetivo}</strong>
          </p>
          {resp.bloques.length === 0 ? <p className="muted">Sin bloques generados.</p> : (
            <table>
              <thead>
                <tr>
                  <th>#</th><th>Bloque</th><th>AE</th>
                  <th>Series×m</th><th>Desc</th><th>Material</th><th>Total</th><th>Detalle</th>
                </tr>
              </thead>
              <tbody>
                {resp.bloques.map((b, i) => (
                  <tr key={i}>
                    <td>{i + 1}</td>
                    <td><strong>{b.tipo}</strong></td>
                    <td>{b.intensidadAE}</td>
                    <td><strong>{b.series ?? "-"}×{b.metrosPorSerie ?? "-"}</strong></td>
                    <td>{b.descansoSeg != null ? `${b.descansoSeg}s` : "-"}</td>
                    <td>{b.material ?? "-"}</td>
                    <td>{b.metrosTotales ?? ""} m</td>
                    <td className="muted" style={{ fontSize: 11 }}>
                      <div>{b.descripcion}</div>
                      <div style={{ marginTop: 4 }}><em>{b.motivo}</em></div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      )}
    </form>
  );
}
