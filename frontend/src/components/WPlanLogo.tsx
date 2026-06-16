export default function WPlanLogo({ size = 34 }: { size?: number }) {
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 34 34"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      style={{ flexShrink: 0, display: "block" }}
    >
      <defs>
        <linearGradient id="wplan-lg" x1="0" y1="0" x2="34" y2="34" gradientUnits="userSpaceOnUse">
          <stop stopColor="#3b82f6" />
          <stop offset="1" stopColor="#1d4ed8" />
        </linearGradient>
      </defs>
      <rect width="34" height="34" rx="9" fill="url(#wplan-lg)" />
      {/* W letterform */}
      <path
        d="M8 11 L11.5 23 L17 15.5 L22.5 23 L26 11"
        stroke="white"
        strokeWidth="2.4"
        strokeLinecap="round"
        strokeLinejoin="round"
        fill="none"
      />
      {/* Wave accent */}
      <path
        d="M10 27 Q13.5 25.2 17 27 Q20.5 28.8 24 27"
        stroke="rgba(255,255,255,0.4)"
        strokeWidth="1.6"
        strokeLinecap="round"
        fill="none"
      />
    </svg>
  );
}
