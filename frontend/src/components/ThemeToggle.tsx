import { useTheme } from "../lib/theme";

export default function ThemeToggle() {
  const { theme, toggle } = useTheme();
  const isDark = theme === "dark";

  return (
    <button
      type="button"
      className={`theme-toggle-pill${isDark ? " is-dark" : ""}`}
      onClick={toggle}
      aria-pressed={isDark}
      title={isDark ? "Activar modo claro" : "Activar modo oscuro"}
    >
      <span className="theme-toggle-icon" aria-hidden>{isDark ? "☀️" : "🌙"}</span>
      <span className="theme-toggle-label">{isDark ? "Claro" : "Oscuro"}</span>
    </button>
  );
}
