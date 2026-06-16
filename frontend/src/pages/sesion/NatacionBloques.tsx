import { FormEvent, useEffect, useState } from "react";
import { api } from "../../lib/api";
import { Dialog } from "../../lib/dialog";
import SugerirNatacionPanel from "../../components/sugerencias/SugerirNatacionPanel";

interface Bloque {
  id: number;
  orden: number;
  nombre?: string;
  tipoBloque?: string;
  descripcion?: string;
  series?: number;
  metrosPorSerie?: number;
  descansoSeg?: number;
  intensidadAE?: string;
  material?: string;
  notas?: string;
  metrosTotales?: number;
  cargaEstimada?: number;
}

const TIPOS_BLOQUE = [
  { value: "CALENTAMIENTO", label: "Calentamiento" },
  { value: "TECNICA", label: "Técnica" },
  { value: "PIERNAS", label: "Piernas" },
  { value: "MATERIAL", label: "Material" },
  { value: "PRINCIPAL", label: "Principal" },
  { value: "VUELTA_CALMA", label: "Vuelta a la calma" },
];

const AE_OPTIONS = ["AE1", "AE2", "AE3", "AE4", "AE5"];

const AE_COLORS: Record<string, { bg: string; color: string }> = {
  AE1: { bg: "#dcfce7", color: "#15803d" },
  AE2: { bg: "#dbeafe", color: "#1d4ed8" },
  AE3: { bg: "#fef3c7", color: "#b45309" },
  AE4: { bg: "#fee2e2", color: "#b91c1c" },
  AE5: { bg: "#f3e8ff", color: "#7e22ce" },
};

const TIPO_LABEL: Record<string, string> = {
  CALENTAMIENTO: "Calentamiento",
  TECNICA: "Técnica",
  PIERNAS: "Piernas",
  MATERIAL: "Material",
  PRINCIPAL: "Principal",
  VUELTA_CALMA: "Vuelta a la calma",
};

export default function NatacionBloques({
  planId,
  sesionId,
  editable = true,
}: {
  planId: number;
  sesionId: number;
  editable?: boolean;
}) {
  const [bloques, setBloques] = useState<Bloque[]>([]);
  const [creando, setCreando] = useState(false);

  const refresh = async () => {
    const data = await api.get<Bloque[]>(`/api/planificaciones/${planId}/sesiones/${sesionId}/natacion/bloques`);
    setBloques(data);
  };
  useEffect(() => { refresh(); /* eslint-disable-next-line */ }, [planId, sesionId]);

  const eliminar = async (b: Bloque) => {
    if (!confirm("¿Eliminar bloque?")) return;
    await api.delete(`/api/planificaciones/${planId}/sesiones/${sesionId}/natacion/bloques/${b.id}`);
    refresh();
  };

  const totalMetros = bloques.reduce((acc, b) => acc + (b.metrosTotales ?? 0), 0);
  const totalCarga = bloques.reduce((acc, b) => acc + (b.cargaEstimada ?? 0), 0);

  return (
    <div>
      {editable && (
        <SugerirNatacionPanel
          planId={planId}
          sesionId={sesionId}
          onApplied={refresh}
          defaultOpen={bloques.length === 0}
        />
      )}

      <div className="toolbar">
        <span className="muted" style={{ fontSize: "0.88rem" }}>
          {bloques.length} bloque(s) · {totalMetros} m · carga {totalCarga.toFixed(1)}
        </span>
        <div className="spacer" />
        {editable && (
          <button className="primary" onClick={() => setCreando(true)}>Añadir bloque</button>
        )}
      </div>

      {bloques.length === 0 && (
        <p className="muted" style={{ textAlign: "center", padding: "2rem 0" }}>
          {editable
            ? "Sin bloques. Usa el asistente o añade uno manualmente."
            : "Sin bloques registrados."}
        </p>
      )}

      <div style={{ display: "flex", flexDirection: "column", gap: "0.6rem" }}>
        {bloques.map((b) => {
          const aeStyle = b.intensidadAE ? AE_COLORS[b.intensidadAE] ?? {} : {};
          return (
            <div key={b.id} className="card" style={{ marginBottom: 0 }}>
              <div style={{ display: "flex", alignItems: "flex-start", gap: "0.6rem", marginBottom: "0.5rem", flexWrap: "wrap" }}>
                <span style={{ color: "var(--muted)", fontSize: "0.85rem", minWidth: "1.5rem", paddingTop: "0.1rem" }}>
                  {b.orden}.
                </span>
                {b.tipoBloque && (
                  <span style={{
                    background: "var(--accent-nat-soft)", color: "var(--accent-nat)",
                    fontSize: "0.78rem", fontWeight: 700, padding: "0.2rem 0.6rem",
                    borderRadius: "999px", letterSpacing: "0.04em",
                  }}>
                    {TIPO_LABEL[b.tipoBloque] ?? b.tipoBloque}
                  </span>
                )}
                {b.intensidadAE && (
                  <span style={{
                    ...aeStyle, fontSize: "0.78rem", fontWeight: 700,
                    padding: "0.2rem 0.6rem", borderRadius: "999px",
                  }}>
                    {b.intensidadAE}
                  </span>
                )}
                <div style={{ marginLeft: "auto", display: "flex", alignItems: "center", gap: "0.75rem" }}>
                  {b.metrosTotales != null && b.metrosTotales > 0 && (
                    <span style={{ fontSize: "0.9rem", fontWeight: 600 }}>{b.metrosTotales} m</span>
                  )}
                  {b.cargaEstimada != null && b.cargaEstimada > 0 && (
                    <span className="muted" style={{ fontSize: "0.83rem" }}>carga {b.cargaEstimada.toFixed(1)}</span>
                  )}
                  {editable && (
                    <button
                      className="ghost"
                      style={{ color: "var(--danger)", padding: "0.2rem 0.5rem", fontSize: "0.83rem" }}
                      onClick={() => eliminar(b)}
                    >
                      Borrar
                    </button>
                  )}
                </div>
              </div>

              {(b.series != null || b.metrosPorSerie != null) && (
                <p style={{ margin: "0 0 0.35rem 1.8rem", fontSize: "0.92rem", fontWeight: 600 }}>
                  {b.series ?? "—"}×{b.metrosPorSerie ?? "—"} m
                  {b.descansoSeg != null && b.descansoSeg > 0 && (
                    <span className="muted" style={{ fontWeight: 400, marginLeft: "0.5rem", fontSize: "0.83rem" }}>
                      · {b.descansoSeg}s desc.
                    </span>
                  )}
                </p>
              )}

              {b.material && (
                <p style={{ margin: "0 0 0.35rem 1.8rem", fontSize: "0.83rem", color: "var(--accent-nat)", fontWeight: 600 }}>
                  Material: {b.material}
                </p>
              )}

              {b.descripcion && (
                <p style={{ margin: "0 0 0 1.8rem", fontSize: "0.84rem", color: "var(--muted)", lineHeight: 1.5 }}>
                  {b.descripcion}
                </p>
              )}

              {b.notas && !b.descripcion && (
                <p style={{ margin: "0 0 0 1.8rem", fontSize: "0.84rem", color: "var(--muted)" }}>
                  {b.notas}
                </p>
              )}
            </div>
          );
        })}
      </div>

      <CrearBloqueDialog
        open={creando}
        onClose={() => setCreando(false)}
        onSaved={() => { setCreando(false); refresh(); }}
        planId={planId}
        sesionId={sesionId}
      />
    </div>
  );
}

function CrearBloqueDialog({ open, onClose, onSaved, planId, sesionId }: {
  open: boolean; onClose: () => void; onSaved: () => void; planId: number; sesionId: number;
}) {
  const [tipoBloque, setTipoBloque] = useState("PRINCIPAL");
  const [intensidadAE, setIntensidadAE] = useState("AE2");
  const [series, setSeries] = useState("");
  const [metrosPorSerie, setMetros] = useState("");
  const [descansoSeg, setDescanso] = useState("");
  const [descripcion, setDescripcion] = useState("");
  const [material, setMaterial] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (!open) return;
    setError(null);
    setTipoBloque("PRINCIPAL"); setIntensidadAE("AE2");
    setSeries(""); setMetros(""); setDescanso(""); setDescripcion(""); setMaterial("");
  }, [open]);

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setError(null);
    try {
      await api.post(`/api/planificaciones/${planId}/sesiones/${sesionId}/natacion/bloques`, {
        tipoBloque,
        nombre: TIPO_LABEL[tipoBloque] ?? tipoBloque,
        intensidadAE: intensidadAE || null,
        series: series ? Number(series) : null,
        metrosPorSerie: metrosPorSerie ? Number(metrosPorSerie) : null,
        descansoSeg: descansoSeg ? Number(descansoSeg) : null,
        descripcion: descripcion || null,
        material: material || null,
      });
      onSaved();
    } catch (err: any) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Dialog
      open={open}
      title="Nuevo bloque"
      onClose={onClose}
      actions={
        <>
          <button onClick={onClose}>Cancelar</button>
          <button className="primary" form="form-nat-bloque" type="submit" disabled={submitting}>
            {submitting ? "Creando…" : "Crear"}
          </button>
        </>
      }
    >
      <form id="form-nat-bloque" onSubmit={onSubmit}>
        <div className="row">
          <div className="row cols-2">
            <div>
              <label>Tipo de bloque</label>
              <select value={tipoBloque} onChange={(e) => setTipoBloque(e.target.value)}>
                {TIPOS_BLOQUE.map((t) => (
                  <option key={t.value} value={t.value}>{t.label}</option>
                ))}
              </select>
            </div>
            <div>
              <label>Intensidad (AE)</label>
              <select value={intensidadAE} onChange={(e) => setIntensidadAE(e.target.value)}>
                {AE_OPTIONS.map((ae) => (
                  <option key={ae} value={ae}>{ae}</option>
                ))}
              </select>
            </div>
          </div>
          <div className="row cols-3">
            <div>
              <label>Series</label>
              <input type="number" min={1} max={50} value={series} onChange={(e) => setSeries(e.target.value)} />
            </div>
            <div>
              <label>Metros / serie</label>
              <input type="number" min={25} max={5000} step={25} value={metrosPorSerie} onChange={(e) => setMetros(e.target.value)} />
            </div>
            <div>
              <label>Descanso (s)</label>
              <input type="number" min={0} max={600} value={descansoSeg} onChange={(e) => setDescanso(e.target.value)} />
            </div>
          </div>
          <div>
            <label>Material (opcional)</label>
            <input value={material} onChange={(e) => setMaterial(e.target.value)} placeholder="TABLA, PALAS, PULL-BUOY…" />
          </div>
          <div>
            <label>Descripción</label>
            <textarea
              value={descripcion}
              onChange={(e) => setDescripcion(e.target.value)}
              placeholder="Qué hay que hacer, cómo ejecutarlo…"
              style={{ minHeight: "80px" }}
            />
          </div>
        </div>
        {error && <p className="error">{error}</p>}
      </form>
    </Dialog>
  );
}
