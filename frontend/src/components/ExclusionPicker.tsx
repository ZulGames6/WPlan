/** Selector minimalista: desplegable para añadir + etiquetas para quitar. */
export default function ExclusionPicker({
  label,
  options,
  selected,
  onChange,
  placeholder = "Añadir exclusión…",
}: {
  label: string;
  options: readonly string[];
  selected: string[];
  onChange: (next: string[]) => void;
  placeholder?: string;
}) {
  const disponibles = options.filter((o) => !selected.includes(o));

  const anadir = (valor: string) => {
    if (!valor || selected.includes(valor)) return;
    onChange([...selected, valor]);
  };

  const quitar = (valor: string) => onChange(selected.filter((x) => x !== valor));

  return (
    <div className="exclusion-picker">
      <label>{label}</label>
      <div className="exclusion-row">
        <select
          value=""
          onChange={(e) => {
            anadir(e.target.value);
            e.target.value = "";
          }}
          disabled={disponibles.length === 0}
        >
          <option value="">{disponibles.length === 0 ? "Ninguna más" : placeholder}</option>
          {disponibles.map((o) => (
            <option key={o} value={o}>{o}</option>
          ))}
        </select>
        {selected.length > 0 && (
          <div className="tag-list">
            {selected.map((o) => (
              <span key={o} className="tag">
                {o}
                <button type="button" className="tag-x" onClick={() => quitar(o)} aria-label={`Quitar ${o}`}>×</button>
              </span>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
