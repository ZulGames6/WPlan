import { FormEvent, useEffect, useState } from "react";
import { Dialog } from "../lib/dialog";
import { useAuth } from "../lib/auth";
import { api } from "../lib/api";

export default function ProfileModal({ open, onClose }: { open: boolean; onClose: () => void }) {
  const { user, refresh } = useAuth();
  const [name, setName]           = useState("");
  const [curPwd, setCurPwd]       = useState("");
  const [newPwd, setNewPwd]       = useState("");
  const [newPwd2, setNewPwd2]     = useState("");
  const [error, setError]         = useState<string | null>(null);
  const [ok, setOk]               = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (open) {
      setName(user?.name || "");
      setCurPwd(""); setNewPwd(""); setNewPwd2("");
      setError(null); setOk(null);
    }
  }, [open, user]);

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (newPwd && newPwd !== newPwd2) { setError("Las contraseñas nuevas no coinciden"); return; }
    if (newPwd && newPwd.length < 6)  { setError("La contraseña debe tener al menos 6 caracteres"); return; }
    setSubmitting(true); setError(null); setOk(null);
    try {
      await api.put("/api/auth/me", {
        name:            name.trim() || null,
        currentPassword: curPwd     || null,
        newPassword:     newPwd     || null,
      });
      await refresh();
      setOk("Perfil actualizado correctamente");
      setCurPwd(""); setNewPwd(""); setNewPwd2("");
    } catch (err: any) {
      setError(err.message || "Error al guardar");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Dialog open={open} title="Mi perfil" onClose={onClose} actions={
      <>
        <button onClick={onClose}>Cerrar</button>
        <button className="primary" form="form-profile" type="submit" disabled={submitting}>
          {submitting ? "Guardando…" : "Guardar cambios"}
        </button>
      </>
    }>
      <form id="form-profile" onSubmit={onSubmit}>
        {/* Info section */}
        <div style={{ display: "flex", alignItems: "center", gap: "1rem", marginBottom: "1.5rem", padding: "1rem", background: "var(--surface-muted)", borderRadius: "var(--radius-sm)" }}>
          <div style={{
            width: "3rem", height: "3rem", borderRadius: "50%",
            background: "linear-gradient(135deg,#1e3a5f,#1d4ed8)",
            display: "flex", alignItems: "center", justifyContent: "center",
            fontWeight: 700, fontSize: "1.2rem", color: "#93c5fd", flexShrink: 0,
          }}>
            {(user?.name || user?.email || "?").charAt(0).toUpperCase()}
          </div>
          <div>
            <p style={{ margin: 0, fontWeight: 600 }}>{user?.name || "Sin nombre"}</p>
            <p style={{ margin: 0, fontSize: "0.8125rem", color: "var(--muted)" }}>{user?.email}</p>
          </div>
        </div>

        <div className="row">
          <div>
            <label>Nombre</label>
            <input
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="Tu nombre"
            />
          </div>

          <div style={{ borderTop: "1px solid var(--border)", paddingTop: "1rem", marginTop: "0.25rem" }}>
            <p style={{ margin: "0 0 0.75rem", fontSize: "0.8125rem", fontWeight: 600, color: "var(--muted)" }}>
              Cambiar contraseña <span style={{ fontWeight: 400 }}>(opcional)</span>
            </p>
            <div className="row">
              <div>
                <label>Contraseña actual</label>
                <input type="password" value={curPwd} onChange={(e) => setCurPwd(e.target.value)} placeholder="••••••••" />
              </div>
              <div className="row cols-2">
                <div>
                  <label>Nueva contraseña</label>
                  <input type="password" value={newPwd} onChange={(e) => setNewPwd(e.target.value)} placeholder="••••••••" />
                </div>
                <div>
                  <label>Repetir nueva</label>
                  <input type="password" value={newPwd2} onChange={(e) => setNewPwd2(e.target.value)} placeholder="••••••••" />
                </div>
              </div>
            </div>
          </div>
        </div>

        {error && <p className="error" style={{ marginTop: "0.75rem" }}>{error}</p>}
        {ok    && <p className="success" style={{ marginTop: "0.75rem" }}>{ok}</p>}
      </form>
    </Dialog>
  );
}
