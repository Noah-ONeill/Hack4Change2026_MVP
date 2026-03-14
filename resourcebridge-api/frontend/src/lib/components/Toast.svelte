<script lang="ts">
  import { toasts } from '$lib/stores/toast';
</script>
<div class="fixed bottom-4 right-4 z-50 flex flex-col gap-2">
  {#each $toasts as toast (toast.id)}
    <div class="flex items-center gap-2 px-4 py-3 rounded-lg shadow-lg text-sm font-medium text-white transition-all
      {toast.type === 'success' ? 'bg-green-600' : 'bg-red-600'}">
      <span>{toast.type === 'success' ? '✓' : '✕'}</span>
      <span>{toast.message}</span>
    </div>
  {/each}
</div>
