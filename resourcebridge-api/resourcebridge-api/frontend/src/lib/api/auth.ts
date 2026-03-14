import { apiFetch } from './client';
import type { AuthResponse } from '$lib/types';

export const login = (email: string, password: string) =>
  apiFetch<AuthResponse>('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ email, password }),
  });

export const register = (data: {
  name: string;
  email: string;
  password: string;
  role: string;
  organizationId: number;
}) =>
  apiFetch<AuthResponse>('/auth/register', {
    method: 'POST',
    body: JSON.stringify(data),
  });
