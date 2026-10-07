import { type FormEvent, useState } from 'react';
import { login, signup } from '../auth';
import { Brand } from './Brand';

export type AuthMode = 'login' | 'signup';

function EyeIcon({ hidden }: { hidden: boolean }) {
  return <svg width="18" height="18" viewBox="0 0 24 24" fill="none" aria-hidden="true">
    <path d="M2.5 12s3.4-6 9.5-6 9.5 6 9.5 6-3.4 6-9.5 6-9.5-6-9.5-6Z" stroke="currentColor" strokeWidth="1.7" />
    <circle cx="12" cy="12" r="2.8" stroke="currentColor" strokeWidth="1.7" />
    {hidden ? <path d="m4 4 16 16" stroke="currentColor" strokeWidth="1.9" strokeLinecap="round" /> : null}
  </svg>;
}

export function AuthPage({ mode }: { mode: AuthMode }) {
  const isSignup = mode === 'signup';
  const [showPassword, setShowPassword] = useState(false);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError('');
    const form = new FormData(event.currentTarget);
    const email = String(form.get('email') || '').trim();
    const password = String(form.get('password') || '');
    const username = String(form.get('username') || '').trim();
    const confirmation = String(form.get('confirmPassword') || '');

    if (isSignup && password !== confirmation) {
      setError('Passwords do not match.');
      return;
    }

    setBusy(true);
    try {
      if (isSignup) await signup({ username, email, password });
      else await login({ email, password });
      window.location.hash = '/';
    } catch (caught) {
      setError(caught instanceof TypeError
        ? 'Could not reach the authentication service. Check that the backend is running and try again.'
        : caught instanceof Error ? caught.message : 'Authentication failed. Please try again.');
    } finally {
      setBusy(false);
    }
  }

  return <div className="auth-shell">
    <header className="auth-topbar">
      <Brand />
      <a className="back-link" href="#/">Back to dashboard</a>
    </header>
    <main className="auth-main">
      <section className="auth-intro" aria-labelledby="auth-intro-heading">
        <div className="auth-intro-copy">
          <span className="eyebrow">Engineering analytics</span>
          <h1 id="auth-intro-heading">Turn delivery data into clearer decisions.</h1>
          <p>Track merge speed, spot review bottlenecks, and keep your team moving with one focused workspace.</p>
        </div>
        <div className="auth-color-strip" aria-hidden="true"><span /><span /><span /><span /></div>
      </section>

      <section className="auth-form-area" aria-label={isSignup ? 'Create an account' : 'Sign in'}>
        <div className="auth-card">
          <div className="auth-heading">
            <h2>{isSignup ? 'Create your account' : 'Welcome back'}</h2>
            <p>{isSignup ? 'Start exploring your team’s delivery metrics.' : 'Sign in to continue to Merge Analytics.'}</p>
          </div>

          <form className="auth-form" onSubmit={handleSubmit}>
            {isSignup ? <label htmlFor="username">Username
              <input id="username" name="username" type="text" autoComplete="username" minLength={3} maxLength={40} required placeholder="your-username" />
            </label> : null}
            <label htmlFor="email">Email address
              <input id="email" name="email" type="email" autoComplete="email" required placeholder="you@example.com" />
            </label>
            <label htmlFor="password">Password
              <span className="password-field">
                <input id="password" name="password" type={showPassword ? 'text' : 'password'} autoComplete={isSignup ? 'new-password' : 'current-password'} minLength={8} required placeholder="At least 8 characters" />
                <button type="button" className="password-toggle" onClick={() => setShowPassword(value => !value)} aria-label={showPassword ? 'Hide password' : 'Show password'} aria-pressed={showPassword}>
                  <EyeIcon hidden={showPassword} />
                </button>
              </span>
            </label>
            {isSignup ? <label htmlFor="confirmPassword">Confirm password
              <input id="confirmPassword" name="confirmPassword" type={showPassword ? 'text' : 'password'} autoComplete="new-password" minLength={8} required placeholder="Enter your password again" />
            </label> : null}

            {error ? <div className="auth-error" role="alert">{error}</div> : null}
            <button className="auth-submit" type="submit" disabled={busy}>{busy ? (isSignup ? 'Creating account…' : 'Signing in…') : (isSignup ? 'Create account' : 'Sign in')}</button>
          </form>

          <p className="auth-switch">{isSignup ? 'Already have an account?' : 'New to Merge Analytics?'}{' '}
            <a href={isSignup ? '#/login' : '#/signup'}>{isSignup ? 'Sign in' : 'Create an account'}</a>
          </p>
        </div>
      </section>
    </main>
  </div>;
}
