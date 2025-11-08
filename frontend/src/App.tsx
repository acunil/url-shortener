import { useEffect, useState } from "react";
import { ShortenForm } from "@/components/ShortenForm";
import { UrlList } from "@/components/UrlList";
import { listUrls } from "@/lib/api";

export default function App() {
  const [urls, setUrls] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  async function fetchUrls() {
    try {
      const data = await listUrls();
      setUrls(data);
    } catch (err) {
      console.error("Failed to fetch URLs", err);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    fetchUrls();
  }, []);

  return (
    <main className="max-w-2xl mx-auto py-10 px-4">
      <header className="flex items-center gap-3 mb-6">
        <img
          src="/favicon.ico"
          alt="App favicon"
          className="h-8 w-8"
        />
        <h1 className="text-2xl font-bold">URL Shortener</h1>
      </header>
      <ShortenForm onSuccess={fetchUrls} />
      <UrlList urls={urls} loading={loading} setUrls={setUrls} onRefresh={fetchUrls} />
    </main>
  );
}
