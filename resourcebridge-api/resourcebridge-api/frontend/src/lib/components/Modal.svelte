<script lang="ts">
  import type { Snippet } from 'svelte';
  let { open = $bindable(false), title = '', children }: { open?: boolean; title?: string; children: Snippet } = $props();
</script>

{#if open}
  <div class="fixed inset-0 z-40 flex items-center justify-center">
    <button class="absolute inset-0 bg-black/40" onclick={() => (open = false)} aria-label="Close modal"></button>
    <div class="relative z-50 bg-white rounded-2xl shadow-2xl p-6 w-full max-w-md mx-4">
      <div class="flex items-center justify-between mb-4">
        <h2 class="text-lg font-semibold text-gray-900">{title}</h2>
        <button onclick={() => (open = false)} class="text-gray-400 hover:text-gray-600 text-xl leading-none">&times;</button>
      </div>
      {@render children()}
    </div>
  </div>
{/if}
