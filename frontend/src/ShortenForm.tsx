import { useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { toast } from "sonner";

export function ShortenForm() {
  const [fullUrl, setFullUrl] = useState("");
  const [customAlias, setCustomAlias] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await fetch("/shorten", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ fullUrl, customAlias: customAlias || undefined }),
      });

      if (!res.ok) {
        const error = await res.json();
        throw new Error(error.message || "Failed to shorten URL");
      }

      const data = await res.json();
      toast({ title: "Shortened!", description: data.shortUrl });
    } catch (err: any) {
      toast({ title: "Error", description: err.message, variant: "destructive" });
    } finally {
      setLoading(false);
    }
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <Label htmlFor="fullUrl">Full URL</Label>
        <Input
          id="fullUrl"
          value={fullUrl}
          onChange={(e) => setFullUrl(e.target.value)}
          placeholder="https://example.com/very/long/url"
          required
        />
      </div>
      <div>
        <Label htmlFor="customAlias">Custom Alias (optional)</Label>
        <Input
          id="customAlias"
          value={customAlias}
          onChange={(e) => setCustomAlias(e.target.value)}
          placeholder="my-custom-alias"
        />
      </div>
      <Button type="submit" disabled={loading}>
        {loading ? "Shortening..." : "Shorten URL"}
      </Button>
    </form>
  );
}
