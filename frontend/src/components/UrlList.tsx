import { Button } from "@/components/ui/button";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";

type Props = {
  urls: { alias: string; fullUrl: string; shortUrl: string }[];
  loading: boolean;
};

export function UrlList({ urls, loading }: Props) {
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
