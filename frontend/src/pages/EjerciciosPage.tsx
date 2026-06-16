import { useState } from "react";
import { NavLink, Navigate, Route, Routes } from "react-router-dom";
import CatalogoSeedBar from "../components/CatalogoSeedBar";
import EjerciciosWaterpolo from "./ejercicios/EjerciciosWaterpolo";
import EjerciciosNatacion from "./ejercicios/EjerciciosNatacion";
import EjerciciosGimnasio from "./ejercicios/EjerciciosGimnasio";

export default function EjerciciosPage() {
  const [seedKey, setSeedKey] = useState(0);

  return (
    <div>
      <h1>Banco de ejercicios</h1>
      <CatalogoSeedBar onDone={() => setSeedKey((k) => k + 1)} />
      <div className="tabs">
        <NavLink to="waterpolo" className={({ isActive }) => (isActive ? "active" : "")}><button>Waterpolo</button></NavLink>
        <NavLink to="natacion" className={({ isActive }) => (isActive ? "active" : "")}><button>Natación</button></NavLink>
        <NavLink to="gimnasio" className={({ isActive }) => (isActive ? "active" : "")}><button>Gimnasio</button></NavLink>
      </div>
      <Routes key={seedKey}>
        <Route index element={<Navigate to="waterpolo" replace />} />
        <Route path="waterpolo" element={<EjerciciosWaterpolo />} />
        <Route path="natacion" element={<EjerciciosNatacion />} />
        <Route path="gimnasio" element={<EjerciciosGimnasio />} />
      </Routes>
    </div>
  );
}
