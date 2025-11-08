import { useState } from "react";
import { toast } from "sonner";

type Props = {
    text: string;
    ariaLabel?: string;
    className?: string;
};

export default function CopyButton({ text, ariaLabel = "Copy short URL", className = "" }: Props) {
    const [copying, setCopying] = useState(false);

    async function handleCopy() {
        if (!text) return;
        setCopying(true);
        try {
            if (navigator.clipboard && navigator.clipboard.writeText) {
                await navigator.clipboard.writeText(text);
            } else {
                // fallback for older browsers
                const ta = document.createElement("textarea");
                ta.value = text;
                ta.style.position = "fixed";
                ta.style.left = "-9999px";
                document.body.appendChild(ta);
                ta.select();
                document.execCommand("copy");
                document.body.removeChild(ta);
            }
            toast.success("Copied to clipboard", { description: text });
        } catch (err) {
            console.error("Copy failed", err);
            toast.error("Failed to copy URL");
        } finally {
            setCopying(false);
        }
    }

    return (
        <button
            type="button"
            onClick={handleCopy}
            disabled={copying}
            aria-label={ariaLabel}
            title={ariaLabel}
            className={`inline-flex items-center gap-2 px-2 py-1 text-xs rounded-md hover:bg-slate-100 disabled:opacity-60 ${className}`}
        >
            {/* Simple clipboard SVG icon */}
            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect>
                <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
            </svg>
        </button>
    );
}
