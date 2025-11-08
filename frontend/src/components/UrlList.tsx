import { Button } from "@/components/ui/button";
import { Trash, Hourglass } from "lucide-react";
import { toast } from "sonner";
import { useState } from "react";
import { deleteAlias } from "@/lib/api";
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
  fullUrl: string;
  shortUrl: string;
};

type Props = {
  urls: UrlEntry[];
  setUrls: (u: UrlEntry[]) => void;
  // optional: fallback refetch
  onRefresh?: () => Promise<void>;
  loading: boolean;
};

export function UrlList({ urls, setUrls, onRefresh, loading }: Props) {
  const [deleting, setDeleting] = useState<Record<string, boolean>>({});
  if (loading) return <p className="text-sm text-muted-foreground">Loading URLs...</p>;

  async function handleDelete(alias: string) {
    // optional confirmation
    if (!confirm(`Delete alias "${alias}"? This cannot be undone.`)) return;

    // optimistic update
    const prev = urls;
    const next = urls.filter((u) => u.alias !== alias);
    setUrls(next);
    setDeleting((s) => ({ ...s, [alias]: true }));

    try {
      await deleteAlias(alias);
      toast.success("Deleted", { description: `Alias "${alias}" was removed.` });

      // If parent prefers to refetch authoritative data
      if (onRefresh) await onRefresh();
    } catch (err: any) {
      // rollback
      setUrls(prev);
      toast.error("Delete failed", { description: err?.message ?? "Unknown error" });
    } finally {
      setDeleting((s) => {
        const copy = { ...s };
        delete copy[alias];
        return copy;
      });
    }
  }

  return (
    <div className="mt-8">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Alias</TableHead>
            <TableHead>Short URL</TableHead>
            <TableHead>Full URL</TableHead>
            <TableHead></TableHead>
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
                <Button
                  variant="destructive"
                  size="sm"
                  onClick={() => handleDelete(url.alias)}
                  disabled={!!deleting[url.alias]}
                  aria-label={deleting[url.alias] ? `Deleting ${url.alias}` : `Delete ${url.alias}`}
                  title={deleting[url.alias] ? "Deleting…" : "Delete"}
                  className={!!deleting[url.alias] ? "cursor-not-allowed" : "cursor-pointer"}
                >
                  {deleting[url.alias] ? (
                    <Hourglass className="h-4 w-4 animate-pulse" aria-hidden />
                  ) : (
                    <Trash className="h-4 w-4" aria-hidden />
                  )}
                </Button>

              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  );
}
