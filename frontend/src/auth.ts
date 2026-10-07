import { API_BASE_URL } from './api';

export interface AuthUser {
  id: string;
  username: string;
  email: string;
}

export interface AuthSession {
  user: AuthUser;
  token?: string;
}

export interface LoginInput {
  email: string;
  password: string;
}

export interface SignupInput extends LoginInput {
  username: string;
}

async function authRequest(path: string, body: LoginInput | SignupInput): Promise<AuthSession> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    method: 'POST',
    credentials: 'include',
    headers: {
      Accept: 'application/json',
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(body),
  });

  const result = await response.json().catch(() => null) as (Partial<AuthSession> & { message?: string }) | null;
  if (!response.ok) {
    throw new Error(result?.message || `Authentication failed with HTTP ${response.status}. Please try again.`);
  }
  if (!result?.user?.id || !result.user.username || !result.user.email) {
    throw new Error('The authentication service returned an unexpected response.');
  }
  return result as AuthSession;
}

export function login(input: LoginInput): Promise<AuthSession> {
  return authRequest('/auth/login', input);
}

export function signup(input: SignupInput): Promise<AuthSession> {
  return authRequest('/auth/signup', input);
}
