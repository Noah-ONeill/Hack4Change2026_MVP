import { apiFetch } from './client';
import type { Donation, DonationStatus } from '$lib/types';

export const getOffered = (token: string) => apiFetch<Donation[]>('/donations', {}, token);
export const getAll = (token: string) => apiFetch<Donation[]>('/donations/all', {}, token);
export const createDonation = (data: object) =>
  apiFetch<Donation>('/donations', { method: 'POST', body: JSON.stringify(data) });
export const updateStatus = (id: number, status: DonationStatus, token: string) =>
  apiFetch<Donation>(`/donations/${id}/status?status=${status}`, { method: 'PATCH' }, token);
