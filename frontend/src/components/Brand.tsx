import teamLogo from '../logo.png';

function GitLabMark() {
  return <svg className="gitlab-mark" viewBox="0 0 36 36" aria-hidden="true">
    <path fill="#e24329" d="m18 32.1 6.6-20.3H11.4L18 32.1Z" />
    <path fill="#fc6d26" d="m18 32.1-13-9.5 6.4-10.8L18 32.1Zm0 0 13-9.5-6.4-10.8L18 32.1Z" />
    <path fill="#fca326" d="M5 22.6 2.8 16a1.1 1.1 0 0 1 0-.7L6.5 4.1c.2-.6 1-.6 1.2 0l3.7 7.7L5 22.6Zm26 0 2.2-6.6a1.1 1.1 0 0 0 0-.7L29.5 4.1c-.2-.6-1-.6-1.2 0l-3.7 7.7L31 22.6Z" />
  </svg>;
}

export function Brand({ className = '', href = '#/' }: { className?: string; href?: string }) {
  return <a className={`brand ${className}`.trim()} href={href} aria-label="Merge Analytics home">
    <GitLabMark />
    <img className="team-mark" src={teamLogo} alt="" />
    <span>Merge <strong>Analytics</strong></span>
  </a>;
}
