const BASE_URL = import.meta.env.VITE_API_BASE_URL || "/api";

function readCookie(name: string): string | null {
    const match = document.cookie.split("; ").find((row) => row.trim().startsWith(name + "="));
    return match ? decodeURIComponent(match.split("=")[1]) : null;
}

async function ensureCsrf() {
    if (readCookie("XSRF-TOKEN")) return;
    await fetch(`${BASE_URL}/urls`, { credentials: "include" });
}


export async function shortenUrl(fullUrl: string, customAlias?: string) {
    await ensureCsrf();
    const xsrf = readCookie("XSRF-TOKEN") ?? "";

    const res = await fetch(`${BASE_URL}/shorten`, {
        method: "POST",
        credentials: "include",
        headers: {
            "Content-Type": "application/json",
            "X-XSRF-TOKEN": xsrf,
        },
        body: JSON.stringify({ fullUrl, customAlias }),
    });

    if (!res.ok) {
        const error = await res.json().catch(() => ({ message: res.statusText }));
        throw new Error(error.message || "Failed to shorten URL");
    }

    return res.json();
}

export async function listUrls() {
    const res = await fetch(`${BASE_URL}/urls`, { credentials: "include" });
    if (!res.ok) throw new Error("Failed to fetch URLs");
    return res.json();
}

export async function deleteAlias(alias: string) {
    await ensureCsrf();
    const res = await fetch(`${BASE_URL}/${alias}`, {
        method: "DELETE",
        credentials: "include",
        headers: {
            "X-XSRF-TOKEN": readCookie("XSRF-TOKEN") ?? "",
        },
    });
    if (!res.ok) {
        const error = await res.json().catch(() => ({ message: res.statusText }));
        throw new Error(error.message || "Failed to delete alias");
    }
}
