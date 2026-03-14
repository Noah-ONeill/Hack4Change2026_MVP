import { writable } from 'svelte/store';

interface Toast {
  id: number;
  message: string;
  type: 'success' | 'error';
}

const { subscribe, update } = writable<Toast[]>([]);
let nextId = 0;

export const toasts = { subscribe };

export function showToast(message: string, type: 'success' | 'error' = 'success') {
  const id = nextId++;
  update(list => [...list, { id, message, type }]);
  setTimeout(() => {
    update(list => list.filter(t => t.id !== id));
  }, 3500);
}
