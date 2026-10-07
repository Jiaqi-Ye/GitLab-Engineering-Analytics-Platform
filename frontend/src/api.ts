export interface MergeRequest {
  iid: number;
  title: string;
  createdAt: string;
  mergedAt: string;
  mergeTimeHours: number;
  webUrl: string | null;
}

export interface Analytics {
  project: { id: string; name: string; url: string };
  source: 'gitlab' | 'demo';
  syncedAt: string;
  mergeRequests: MergeRequest[];
  summary: {
    mergedCount: number;
    averageMergeTimeHours: number | null;
    fastestMergeTimeHours: number | null;
    slowestMergeTimeHours: number | null;
  };
}

export const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || '/api').replace(/\/$/, '');

export async function fetchAnalytics(sync: boolean, signal: AbortSignal): Promise<Analytics> {
  const response = await fetch(`${API_BASE_URL}/analytics/merge-requests${sync ? '/sync' : ''}`, {
    method: sync ? 'POST' : 'GET',
    headers: { Accept: 'application/json' },
    signal,
  });

  if (!response.ok) {
    const error = await response.json().catch(() => null) as { message?: string } | null;
    throw new Error(error?.message || `The analytics service returned HTTP ${response.status}. Please try again.`);
  }

  const data = await response.json() as Analytics;
  if (!data.project || !Array.isArray(data.mergeRequests) || !data.summary ||
      !['gitlab', 'demo'].includes(data.source) || !data.syncedAt) {
    throw new Error('The analytics service returned an unexpected response. Please check the API configuration.');
  }
  return data;
}

// Treat API-provided links as data; allow only normal web navigation.
export function webLink(value: string | null | undefined): string | undefined {
  if (!value) return undefined;
  try {
    const url = new URL(value);
    return ['https:', 'http:'].includes(url.protocol) ? url.href : undefined;
  } catch {
    return undefined;
  }
}
