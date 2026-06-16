import { useState } from "react";
import { api } from "../lib/api";

interface SeedResponse {
  gimnasioAnadidos: number;
  natacionAnadidos: number;
  waterpoloAnadidos: number;
  realizado: boolean;
  mensaje: string;
}

export default function CatalogoSeedBar({ onDone }: { onDone: () => void }) {
  const [loading, setLoading] = useState(false);
  const [msg, setMsg] = useState<string | null>(null);
  const [err, setErr] = useState<string | null>(null);

  const cargar = async (forzar: boolean) => {
    setLoading(true);
    setMsg(null);
    setErr(null);
    try {
      const r = await api.post<SeedResponse>(
        `/api/ejercicios/catalogo/inicializar?forzar=${forzar}`
      );
      if (r.gimnasioAnadidos + r.natacionAnadidos + r.waterpoloAnadidos === 0) {
        setMsg(r.mensaje);
      } else {
        setMsg(
          `Añadidos: ${r.gimnasioAnadidos} gimnasio, ${r.natacionAnadidos} natación, ${r.waterpoloAnadidos} waterpolo.`
        );
      }
      onDone();
    } catch (e: unknown) {
      setErr(e instanceof Error ? e.message : "Error al cargar catálogo");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="catalogo-seed-bar">
      <div>
        <strong>Catálogo base WPlan</strong>
        <p className="muted" style={{ margin: "0.2rem 0 0", fontSize: "0.85rem" }}>
          Más de 50 ejercicios de gimnasio, 40 de waterpolo y 20 de natación listos para usar.
        </p>
      </div>
      <div className="toolbar" style={{ marginBottom: 0 }}>
        <button type="button" className="primary" disabled={loading} onClick={() => cargar(false)}>
          {loading ? "Cargando…" : "Cargar si está vacío"}
        </button>
        <button type="button" disabled={loading} onClick={() => cargar(true)}>
          Añadir faltantes
        </button>
      </div>
      {msg && <p className="success" style={{ margin: "0.5rem 0 0" }}>{msg}</p>}
      {err && <p className="error" style={{ margin: "0.5rem 0 0" }}>{err}</p>}
    </div>
  );
}
