const BASE_URL = import.meta.env.VITE_API_BASE_URL || "/api";

export async function shortenUrl(fullUrl: string, customAlias?: string) {
    const res = await fetch(`${BASE_URL}/shorten`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ fullUrl, customAlias }),
    });

    if (!res.ok) {
        const error = await res.json();
        throw new Error(error.message || "Failed to shorten URL");
    }

    return await res.json(); // { shortUrl }
}

export async function listUrls() {
    const res = await fetch(`${BASE_URL}/urls`);
    if (!res.ok) throw new Error("Failed to fetch URLs");
    return await res.json(); // List of UrlResponse
}

export async function deleteAlias(alias: string) {
    const res = await fetch(`${BASE_URL}/${alias}`, { method: "DELETE" });
    if (!res.ok) {
        const error = await res.json();
        throw new Error(error.message || "Failed to delete alias");
    }
}
