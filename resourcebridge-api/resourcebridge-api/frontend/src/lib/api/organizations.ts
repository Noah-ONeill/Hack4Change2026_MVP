import { apiFetch } from './client';
import type { Organization } from '$lib/types';

export const getAll = () => apiFetch<Organization[]>('/organizations');
