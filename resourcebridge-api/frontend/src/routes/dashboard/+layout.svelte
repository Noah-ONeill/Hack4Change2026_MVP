<script lang="ts">
  import { goto } from '$app/navigation';
  import { page } from '$app/stores';
  import { auth } from '$lib/stores/auth';
  import { onMount } from 'svelte';
  import type { Snippet } from 'svelte';

  let { children }: { children: Snippet } = $props();

  onMount(() => {
    auth.init();
    if (!$auth.token) goto('/login');
  });

  function logout() {
    auth.clear();
    goto('/');
  }

  let currentPath = $derived($page.url.pathname);

  const staffNav = [
    { href: '/dashboard/staff', label: 'Dashboard', icon: '📊' },
    { href: '/dashboard/staff#inventory', label: 'Inventory', icon: '📦' },
    { href: '/dashboard/staff#needs', label: 'Our Needs', icon: '📋' },
    { href: '/dashboard/staff#transfers', label: 'Incoming', icon: '🚚' },
  ];
</script>

{#if $auth.token}
<div class="min-h-screen flex bg-gray-50">
  <!-- SIDEBAR -->
  <aside class="w-56 bg-white border-r border-gray-100 flex flex-col fixed h-full shadow-sm">
    <div class="p-4 border-b border-gray-100">
      <a href="/" class="flex items-center gap-2">
        <div class="w-8 h-8 bg-brand-500 rounded-lg flex items-center justify-center flex-shrink-0">
          <span class="text-white font-bold text-sm">RB</span>
        </div>
        <span class="font-bold text-gray-900 text-sm">ResourceBridge</span>
      </a>
    </div>

    <nav class="flex-1 p-3 space-y-1">
      {#each staffNav as item}
        <a
          href={item.href}
          class="flex items-center gap-2.5 px-3 py-2 rounded-lg text-sm font-medium transition-colors
            {currentPath === item.href ? 'bg-brand-50 text-brand-700' : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'}">
          <span>{item.icon}</span>
          {item.label}
        </a>
      {/each}
    </nav>

    <div class="p-3 border-t border-gray-100">
      <div class="px-3 py-2 mb-1">
        <div class="text-xs font-semibold text-gray-900 truncate">{$auth.name}</div>
        <div class="text-xs text-gray-400 truncate">{$auth.email}</div>
        <div class="inline-flex items-center mt-1 px-2 py-0.5 bg-brand-100 text-brand-700 rounded-full text-xs font-medium">
          Shelter Staff
        </div>
      </div>
      <button
        onclick={logout}
        class="w-full text-left px-3 py-2 text-sm text-red-600 hover:bg-red-50 rounded-lg font-medium transition-colors">
        Sign out
      </button>
    </div>
  </aside>

  <!-- MAIN CONTENT -->
  <main class="flex-1 ml-56 p-6">
    {@render children()}
  </main>
</div>
{/if}
