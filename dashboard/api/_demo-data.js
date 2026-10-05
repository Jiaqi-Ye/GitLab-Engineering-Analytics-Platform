const mergeRequests = [
  { iid: 106, title: 'DEMO: Document deployment steps', createdAt: '2026-09-26T08:00:00Z', mergedAt: '2026-09-26T12:45:00Z', mergeTimeHours: 4.75, webUrl: null },
  { iid: 105, title: 'DEMO: Show loading and error states', createdAt: '2026-09-25T09:00:00Z', mergedAt: '2026-09-25T21:00:00Z', mergeTimeHours: 12, webUrl: null },
  { iid: 103, title: 'DEMO: Connect database cache', createdAt: '2026-09-23T14:00:00Z', mergedAt: '2026-09-24T14:00:00Z', mergeTimeHours: 24, webUrl: null },
  { iid: 104, title: 'DEMO: Improve table labels', createdAt: '2026-09-24T10:00:00Z', mergedAt: '2026-09-24T10:45:00Z', mergeTimeHours: 0.75, webUrl: null },
  { iid: 102, title: 'DEMO: Build analytics endpoint', createdAt: '2026-09-22T08:00:00Z', mergedAt: '2026-09-22T14:00:00Z', mergeTimeHours: 6, webUrl: null },
  { iid: 101, title: 'DEMO: Add project overview', createdAt: '2026-09-21T09:00:00Z', mergedAt: '2026-09-21T11:30:00Z', mergeTimeHours: 2.5, webUrl: null },
];

export function createDemoAnalytics() {
  return {
    project: {
      id: '158108',
      name: 'Project 29',
      url: 'https://git.doit.wisc.edu/projects/158108',
    },
    source: 'demo',
    syncedAt: new Date().toISOString(),
    mergeRequests,
    summary: {
      mergedCount: mergeRequests.length,
      averageMergeTimeHours: 8.33,
      fastestMergeTimeHours: 0.75,
      slowestMergeTimeHours: 24,
    },
  };
}
