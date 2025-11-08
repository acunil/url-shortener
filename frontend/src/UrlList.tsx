import { useEffect, useState } from "react";
import { listUrls } from "@/lib/api";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";

type UrlEntry = {
  alias: string;
  shortUrl: string;
  fullUrl: string;
};

export function UrlList() {
  const [urls, setUrls] = useState<UrlEntry[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchUrls() {
      try {
        const data = await listUrls();
        setUrls(data);
      } catch (err: any) {
        toast.error("Failed to load URLs", { description: err.message });
      } finally {
        setLoading(false);
      }
    }

    fetchUrls();
  }, []);

  if (loading) return <p className="text-sm text-muted-foreground">Loading URLs...</p>;

  return (
    <div className="mt-8">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Alias</TableHead>
            <TableHead>Short URL</TableHead>
            <TableHead>Full URL</TableHead>
            <TableHead>Actions</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {urls.map((url) => (
            <TableRow key={url.alias}>
              <TableCell>{url.alias}</TableCell>
              <TableCell>
                <a
                  href={url.shortUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-blue-600 underline"
                >
                  {url.shortUrl}
                </a>
              </TableCell>
              <TableCell>
                <a
                  href={url.fullUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-blue-600 underline"
                >
                  {url.fullUrl}
                </a>
              </TableCell>
              <TableCell>
                <Button variant="outline" size="sm" disabled>
                  Delete
                </Button>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  );
}
