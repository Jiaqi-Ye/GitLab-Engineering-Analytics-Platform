import { useEffect, useRef, useState } from 'react';
import { fetchAnalytics, webLink, type Analytics, type MergeRequest } from './api';

const numberFormat = new Intl.NumberFormat('en-US', { maximumFractionDigits: 2 });
const dateFormat = new Intl.DateTimeFormat('en-US', {
  month: 'short', day: 'numeric', year: 'numeric', timeZone: 'UTC',
});
const timestampFormat = new Intl.DateTimeFormat('en-US', {
  month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit',
  hour12: false, timeZone: 'UTC',
});

function hours(value: number | null): string {
  return value === null ? '—' : numberFormat.format(value);
}

function MergeIcon({ small = false }: { small?: boolean }) {
  return <svg width={small ? 18 : 22} height={small ? 18 : 22} viewBox="0 0 24 24" fill="none" aria-hidden="true">
    <circle cx="6" cy="5" r="2.5" stroke="currentColor" strokeWidth="1.8" />
    <circle cx="6" cy="19" r="2.5" stroke="currentColor" strokeWidth="1.8" />
    <circle cx="18" cy="6" r="2.5" stroke="currentColor" strokeWidth="1.8" />
    <path d="M6 7.5v9M18 8.5v1a6 6 0 0 1-6 6H6" stroke="currentColor" strokeWidth="1.8" />
  </svg>;
}

function RefreshIcon() {
  return <svg width="17" height="17" viewBox="0 0 24 24" fill="none" aria-hidden="true">
    <path d="M20 10a8 8 0 1 0-1.5 7M20 4v6h-6" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" />
  </svg>;
}

function GitLabMark() {
  return <svg className="gitlab-mark" viewBox="0 0 36 36" role="img" aria-label="GitLab">
    <path fill="#e24329" d="m18 32.1 6.6-20.3H11.4L18 32.1Z" />
    <path fill="#fc6d26" d="m18 32.1-13-9.5 6.4-10.8L18 32.1Zm0 0 13-9.5-6.4-10.8L18 32.1Z" />
    <path fill="#fca326" d="M5 22.6 2.8 16a1.1 1.1 0 0 1 0-.7L6.5 4.1c.2-.6 1-.6 1.2 0l3.7 7.7L5 22.6Zm26 0 2.2-6.6a1.1 1.1 0 0 0 0-.7L29.5 4.1c-.2-.6-1-.6-1.2 0l-3.7 7.7L31 22.6Z" />
  </svg>;
}

function ChevronIcon() {
  return <svg width="16" height="16" viewBox="0 0 24 24" fill="none" aria-hidden="true">
    <path d="m9 6 6 6-6 6" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" />
  </svg>;
}

function Metric({ label, value, note, featured = false }: {
  label: string; value: number | null; note: string; featured?: boolean;
}) {
  return <article className={`metric${featured ? ' metric-featured' : ''}`}>
    <p className="metric-label">{label}</p>
    <p className="metric-value">{hours(value)}{label !== 'Merged requests' && value !== null ? <span>hrs</span> : null}</p>
    <p className="metric-note">{note}</p>
  </article>;
}

function RequestChart({ requests }: { requests: MergeRequest[] }) {
  const recent = requests.slice(0, 12);
  const maximum = Math.max(1, ...recent.map(request => request.mergeTimeHours));
  return <section id="trends" className="panel chart-panel" aria-labelledby="chart-heading">
    <div className="section-heading">
      <div><p className="eyebrow">FROM OPEN TO MERGED</p><h2 id="chart-heading">Time per merge request</h2></div>
      <span className="quiet-tag">Hours</span>
    </div>
    <p className="section-description">{requests.length > 12 ? 'The 12 most recently merged requests' : 'All merged requests in this snapshot'}, newest first.</p>
    <div className="chart" role="img" aria-label={`Time-to-merge bar chart for ${recent.length} requests. Exact hours and dates are provided in the table below.`}>
      {recent.map(request => <div className="bar-row" key={request.iid} title={`${request.title}: ${hours(request.mergeTimeHours)} hours`}>
        <span className="bar-label">!{request.iid}</span>
        <div className="bar-track"><div className="bar-fill" style={{ width: `${request.mergeTimeHours / maximum * 100}%` }} /></div>
        <span className="bar-value">{hours(request.mergeTimeHours)}<span> h</span></span>
      </div>)}
      <div className="chart-axis"><span>0 h</span><span>{hours(maximum / 2)} h</span><span>{hours(maximum)} h</span></div>
    </div>
  </section>;
}

function RequestTable({ requests, source }: { requests: MergeRequest[]; source: Analytics['source'] }) {
  const [query, setQuery] = useState('');
  const needle = query.trim().toLowerCase();
  const filtered = requests.filter(request => `${request.iid} ${request.title}`.toLowerCase().includes(needle));

  return <section id="requests" className="panel table-panel" aria-labelledby="table-heading">
    <div className="section-heading table-heading">
      <div><h2 id="table-heading">Merged requests <span className="count-badge">{requests.length}</span></h2>
        <p className="section-description">Creation and merge timestamps are shown in UTC.</p>
      </div>
      <label className="search"><span className="sr-only">Search merge requests by title or IID</span>
        <svg width="17" height="17" viewBox="0 0 24 24" fill="none" aria-hidden="true"><circle cx="10.5" cy="10.5" r="6.5" stroke="currentColor" strokeWidth="1.8" /><path d="m16 16 5 5" stroke="currentColor" strokeWidth="1.8" /></svg>
        <input value={query} onChange={event => setQuery(event.target.value)} type="search" placeholder="Search title or IID…" />
      </label>
    </div>
    <div className="table-scroll" tabIndex={0} role="region" aria-label="Merge request details; scroll horizontally on small screens">
      <table>
        <caption className="sr-only">Merged requests, ordered by most recent merge. Time-to-merge is elapsed hours from creation to merge.</caption>
        <thead><tr><th scope="col">Merge request</th><th scope="col">Created · UTC</th><th scope="col">Merged · UTC</th><th scope="col" className="numeric">Time to merge</th></tr></thead>
        <tbody>{filtered.map(request => {
          const href = webLink(request.webUrl);
          return <tr key={request.iid}>
            <td><div className="request-title"><span className="request-icon"><MergeIcon small /></span><div>
              {href ? <a href={href} target="_blank" rel="noreferrer">{request.title}<span className="external" aria-hidden="true">↗</span><span className="sr-only"> (opens GitLab in a new tab)</span></a> : <span>{request.title}</span>}
              <span className="request-iid">!{request.iid}<span className="merged-label">Merged</span></span>
            </div></div></td>
            <td className="timestamp"><time dateTime={request.createdAt}>{timestampFormat.format(new Date(request.createdAt))}</time></td>
            <td className="timestamp"><time dateTime={request.mergedAt}>{timestampFormat.format(new Date(request.mergedAt))}</time></td>
            <td className="numeric duration">{hours(request.mergeTimeHours)} <span>hrs</span></td>
          </tr>;
        })}</tbody>
      </table>
      {filtered.length === 0 ? <p className="no-results" role="status">No requests match “{query}”. Try another title or IID.</p> : null}
    </div>
    <div className="table-footer"><span>{filtered.length} of {requests.length} requests</span><span>{source === 'demo' ? 'Representative demo data' : 'GitLab data · cached in MySQL'}</span></div>
  </section>;
}

function Dashboard({ data }: { data: Analytics }) {
  const requests = [...data.mergeRequests].sort((a, b) => Date.parse(b.mergedAt) - Date.parse(a.mergedAt));
  const earliestCreated = requests.length > 0 ? requests.reduce((earliest, request) =>
    request.createdAt < earliest ? request.createdAt : earliest, requests[0].createdAt) : null;
  const projectHref = webLink(data.project.url);

  return <>
    <section id="overview" className="project-context" aria-label="Project and data source">
      <div className="project-name"><span className="project-avatar" aria-hidden="true"><MergeIcon /></span><div>
        <span className="context-label">PROJECT</span>
        {projectHref ? <a href={projectHref} target="_blank" rel="noreferrer">{data.project.name} <span aria-hidden="true">↗</span><span className="sr-only"> (opens GitLab in a new tab)</span></a> : <strong>{data.project.name}</strong>}
        <span className="project-id">ID {data.project.id}</span>
      </div></div>
      <div className="source-context"><span className={`source-badge ${data.source}`}><span className="status-dot" />{data.source === 'demo' ? 'DEMO DATA' : 'LIVE GITLAB · CACHED'}</span>
        <span className="synced">Last synced <time dateTime={data.syncedAt}>{timestampFormat.format(new Date(data.syncedAt))} UTC</time></span>
      </div>
    </section>
    {data.source === 'demo' ? <div className="demo-note"><strong>Demo mode</strong><span>Representative sample requests stored in MySQL. These are not live project activity.</span></div> : null}
    <section className="metrics" aria-label="Merge request summary">
      <Metric label="Merged requests" value={data.summary.mergedCount} note="In the current snapshot" />
      <Metric label="Average time to merge" value={data.summary.averageMergeTimeHours} note="From creation to merge" featured />
      <Metric label="Fastest merge" value={data.summary.fastestMergeTimeHours} note="Shortest elapsed time" />
      <Metric label="Slowest merge" value={data.summary.slowestMergeTimeHours} note="Longest elapsed time" />
    </section>
    {requests.length > 0 ? <>
      <div className="analysis-layout"><RequestChart requests={requests} /><aside id="about-metric" className="reading-note" aria-labelledby="reading-heading">
        <span className="note-icon" aria-hidden="true">↗</span><p className="eyebrow">READING THIS METRIC</p><h2 id="reading-heading">Every merge has a timeline.</h2>
        <p>Time to merge measures the full elapsed time between opening a request and merging it.</p>
        <div className="formula"><span>Created</span><span aria-hidden="true">→</span><span>Merged</span></div>
        <p>It includes drafting, waiting, review, and iteration. It is <strong>not a measure of review duration</strong>.</p>
        <div className="coverage"><span className="context-label">INCLUDED ACTIVITY</span><strong>{dateFormat.format(new Date(earliestCreated!))} – {dateFormat.format(new Date(requests[0].mergedAt))}</strong><p>Dates span the included requests. Open requests and closed, unmerged requests are excluded.</p></div>
      </aside></div>
      <RequestTable requests={requests} source={data.source} />
    </> : <section className="panel empty-state"><MergeIcon /><h2>No merged requests yet</h2><p>This project has no merged requests in the current snapshot. Refresh after a request is merged to see its time-to-merge.</p></section>}
  </>;
}

export default function App() {
  const [data, setData] = useState<Analytics | null>(null);
  const [busy, setBusy] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState('');
  const requestRef = useRef<AbortController | null>(null);

  async function load(sync: boolean) {
    requestRef.current?.abort();
    const controller = new AbortController();
    requestRef.current = controller;
    setBusy(true);
    setError(null);
    setNotice('');
    try {
      const result = await fetchAnalytics(sync, controller.signal);
      if (controller.signal.aborted) return;
      setData(result);
      if (sync) setNotice(result.source === 'demo' ? 'Demo snapshot refreshed.' : 'GitLab data synced successfully.');
    } catch (caught) {
      if (controller.signal.aborted) return;
      setError(caught instanceof TypeError ? 'Could not reach the analytics service. Check that the backend is running and try again.'
        : caught instanceof Error ? caught.message : 'Could not load analytics. Please try again.');
    } finally {
      if (!controller.signal.aborted) setBusy(false);
    }
  }

  useEffect(() => {
    void load(false);
    return () => requestRef.current?.abort();
  }, []);

  return <div className="app-shell">
    <a className="skip-link" href="#main">Skip to analytics</a>
    <header className="topbar">
      <div className="topbar-content">
        <a className="brand" href="#overview" aria-label="T29 GitLab Analytics home">
          <GitLabMark />
          <span>GitLab <strong>Analytics</strong></span>
          <span className="brand-divider" aria-hidden="true" />
          <span className="team-mark">T29</span>
        </a>
        <nav className="utility-nav" aria-label="Utility navigation">
          <a href="#requests">Merge requests</a>
          <a href="#about-metric">Metric guide</a>
          <span className="course-label">UW–Madison <span>·</span> CS 506</span>
        </nav>
      </div>
    </header>
    <nav className="product-nav" aria-label="Product navigation">
      <div className="product-nav-content">
        <a className="product-nav-active" href="#overview">Project overview</a>
        <a href="#trends">Analytics</a>
        <a href="#requests">Merge requests</a>
        <a href="#about-metric">Documentation</a>
      </div>
    </nav>
    <div className="workspace">
      <aside className="sidebar" aria-label="Analytics sections">
        <p className="sidebar-title">Project analytics</p>
        <nav>
          <a className="sidebar-active" href="#overview"><span>Overview</span><ChevronIcon /></a>
          <a href="#trends"><span>Merge time</span><ChevronIcon /></a>
          <a href="#requests"><span>Merged requests</span><ChevronIcon /></a>
        </nav>
        <div className="sidebar-group">
          <p>Explore</p>
          <span className="sidebar-muted">CI / CD trends <small>Coming soon</small></span>
          <span className="sidebar-muted">Contributor activity <small>Coming soon</small></span>
        </div>
        <div className="sidebar-brand">
          <span className="team-mark team-mark-large">T29</span>
          <div><strong>Team 29</strong><span>CS 506 project</span></div>
        </div>
      </aside>
      <main id="main" className="page">
        <nav className="breadcrumbs" aria-label="Breadcrumb">
          <a href="#overview">T29</a><span>/</span><a href="#trends">Analytics</a><span>/</span><strong>Merge requests</strong>
        </nav>
        <div className="page-heading"><div><p className="eyebrow">MERGE REQUEST ANALYTICS</p><h1>Time to merge</h1><p className="subtitle">Understand how long work moves from creation to merge, spot outliers, and follow delivery pace.</p></div>
          <button className="refresh-button" onClick={() => void load(Boolean(data))} disabled={busy}><span className={busy ? 'spinning' : ''}><RefreshIcon /></span>{busy ? (data ? 'Refreshing…' : 'Loading…') : data?.source === 'demo' ? 'Refresh demo data' : 'Refresh GitLab data'}</button>
        </div>
        <div className="live-notice" role="status" aria-live="polite">{notice}</div>
        {error ? <div className="error-message" role="alert"><div><strong>{data ? 'Refresh failed' : 'Unable to load analytics'}</strong><p>{error}</p>{data ? <p>Your last loaded snapshot is still shown below.</p> : null}</div><button className="retry-button" disabled={busy} onClick={() => void load(Boolean(data))}>Try again</button></div> : null}
        {data ? <Dashboard data={data} /> : busy ? <section className="panel loading-state" role="status"><span className="loading-dot" /><h2>Loading project analytics</h2><p>Fetching the latest saved snapshot from the analytics service…</p></section> : null}
        <footer className="page-footer"><span>T29 / GitLab Analytics</span><span>Student project · creation-to-merge · UTC timestamps</span></footer>
      </main>
      <aside className="on-this-page" aria-label="On this page">
        <p>On this page</p>
        <nav><a href="#overview">Project snapshot</a><a href="#trends">Time per request</a><a href="#about-metric">About this metric</a><a href="#requests">Merged requests</a></nav>
        <div className="palette-card" aria-label="GitLab inspired palette"><span className="swatch orange" /><span className="swatch red" /><span className="swatch yellow" /><span className="swatch purple" /><p>GitLab-inspired palette</p></div>
      </aside>
    </div>
  </div>;
}
