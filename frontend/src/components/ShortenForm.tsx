import { useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { toast } from "sonner";
import { shortenUrl } from "@/lib/api";

type Props = {
  onSuccess: () => void;
};

export function ShortenForm({ onSuccess }: Props) {
  const [fullUrl, setFullUrl] = useState("");
  const [customAlias, setCustomAlias] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setLoading(true);
    try {
      const data = await shortenUrl(fullUrl, customAlias || undefined);
      toast.success("URL shortened successfully", {
        description: data.shortUrl,
      });
      setFullUrl("");
      setCustomAlias("");
      onSuccess();
    } catch (err: any) {
      toast.error("Error shortening URL", {
        description: err.message,
      });
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
