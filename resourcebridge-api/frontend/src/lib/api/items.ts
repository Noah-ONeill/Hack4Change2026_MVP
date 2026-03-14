import { apiFetch } from './client';
import type { Item } from '$lib/types';

export const getAll = () => apiFetch<Item[]>('/items');
