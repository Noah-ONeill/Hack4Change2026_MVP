<script lang="ts">
  import { onMount } from 'svelte';
  import { getUnfulfilled } from '$lib/api/needs';
  import { getLatest } from '$lib/api/announcements';
  import { getAll as getOrgs } from '$lib/api/organizations';
  import UrgencyBadge from '$lib/components/UrgencyBadge.svelte';
  import StatusBadge from '$lib/components/StatusBadge.svelte';
  import type { Need, Announcement, Organization } from '$lib/types';

  let needs = $state<Need[]>([]);
  let announcements = $state<Announcement[]>([]);
  let organizations = $state<Organization[]>([]);
  let loading = $state(true);

  let criticalNeeds = $derived(needs.filter(n => n.urgency === 'CRITICAL' || n.urgency === 'HIGH').slice(0, 3));
  let recentAnnouncements = $derived(announcements.slice(0, 5));

  onMount(async () => {
    try {
      const [n, a, o] = await Promise.all([getUnfulfilled(), getLatest(), getOrgs()]);
      needs = n;
      announcements = a;
      organizations = o;
    } catch (e) {
      // API not available, show empty state
    } finally {
      loading = false;
    }
  });
</script>

<svelte:head><title>ResourceBridge — Connecting Donors with Shelters</title></svelte:head>

<div class="min-h-screen bg-gray-50">
  <!-- NAV -->
  <nav class="bg-white border-b border-gray-100 px-4 py-3">
    <div class="max-w-6xl mx-auto flex items-center justify-between">
      <div class="flex items-center gap-2">
        <div class="w-8 h-8 bg-brand-500 rounded-lg flex items-center justify-center">
          <span class="text-white font-bold text-sm">RB</span>
        </div>
        <span class="font-bold text-gray-900 text-lg">ResourceBridge</span>
      </div>
      <div class="flex items-center gap-3">
        <a href="/donate" class="text-sm text-gray-600 hover:text-brand-600 font-medium">Browse Needs</a>
        <a href="/login" class="text-sm bg-brand-500 hover:bg-brand-600 text-white px-4 py-2 rounded-lg font-medium transition-colors">Staff Login</a>
      </div>
    </div>
  </nav>

  <!-- HERO -->
  <div class="bg-gradient-to-br from-brand-500 to-brand-700 text-white">
    <div class="max-w-6xl mx-auto px-4 py-16 md:py-24">
      <div class="max-w-2xl">
        <div class="inline-flex items-center gap-2 bg-white/20 rounded-full px-3 py-1 text-sm font-medium mb-6">
          <span>🏘️</span> Serving Moncton, NB communities
        </div>
        <h1 class="text-4xl md:text-5xl font-bold mb-4 leading-tight">
          Connecting donors<br/>with shelters that<br/>need them most
        </h1>
        <p class="text-lg text-brand-100 mb-8 max-w-lg">
          No more phone calls, spreadsheets, or missed donations. ResourceBridge connects donors, shelter staff, and coordinators in one simple platform — so resources get to the people who need them most.
        </p>
        <div class="flex flex-wrap gap-3">
          <a href="/donate" class="bg-white text-brand-600 font-semibold px-6 py-3 rounded-xl hover:bg-brand-50 transition-colors shadow-sm">
            I want to donate
          </a>
          <a href="/login" class="bg-brand-600 text-white font-semibold px-6 py-3 rounded-xl hover:bg-brand-700 transition-colors border border-brand-400">
            Staff / Coordinator login
          </a>
        </div>
      </div>
    </div>
  </div>

  <!-- STATS -->
  <div class="bg-white border-b border-gray-100">
    <div class="max-w-6xl mx-auto px-4 py-6">
      <div class="grid grid-cols-3 gap-4 text-center">
        <div>
          <div class="text-2xl font-bold text-brand-600">{needs.length}</div>
          <div class="text-xs text-gray-500 mt-0.5">Open needs</div>
        </div>
        <div>
          <div class="text-2xl font-bold text-brand-600">{organizations.length}</div>
          <div class="text-xs text-gray-500 mt-0.5">Organizations</div>
        </div>
        <div>
          <div class="text-2xl font-bold text-brand-600">{needs.filter(n => n.urgency === 'CRITICAL').length}</div>
          <div class="text-xs text-gray-500 mt-0.5">Critical needs</div>
        </div>
      </div>
    </div>
  </div>

  <!-- HOW IT WORKS -->
  <div class="max-w-6xl mx-auto px-4 py-12">
    <h2 class="text-xl font-bold text-gray-900 text-center mb-2">How it works</h2>
    <p class="text-sm text-gray-500 text-center mb-8">Three roles, one coordinated workflow</p>
    <div class="grid md:grid-cols-3 gap-4 relative">
      <!-- connector line desktop -->
      <div class="hidden md:block absolute top-10 left-1/3 right-1/3 h-0.5 bg-brand-200 z-0"></div>

      {#each [
        { step: '1', emoji: '🎁', role: 'Donor', color: 'bg-blue-50 border-blue-100', badge: 'bg-blue-100 text-blue-700', title: 'Donor offers goods', desc: 'Browses what shelters need and submits a donation — no account needed, takes 60 seconds.' },
        { step: '2', emoji: '📋', role: 'Coordinator', color: 'bg-brand-50 border-brand-100', badge: 'bg-brand-100 text-brand-700', title: 'Coordinator matches', desc: 'Reviews all donations and needs, then assigns the right goods to the right shelter.' },
        { step: '3', emoji: '📦', role: 'Shelter Staff', color: 'bg-green-50 border-green-100', badge: 'bg-green-100 text-green-700', title: 'Shelter receives', desc: 'Staff confirms receipt, updates inventory, and posts new needs — keeping the loop closed.' }
      ] as s}
        <div class="relative z-10 bg-white rounded-2xl border {s.color} p-5 shadow-sm">
          <div class="flex items-center gap-3 mb-3">
            <div class="w-10 h-10 rounded-xl {s.color} border flex items-center justify-center text-xl">{s.emoji}</div>
            <div>
              <span class="inline-block px-2 py-0.5 rounded-full text-xs font-semibold {s.badge} mb-0.5">{s.role}</span>
              <div class="font-semibold text-gray-900 text-sm">{s.title}</div>
            </div>
          </div>
          <p class="text-xs text-gray-500 leading-relaxed">{s.desc}</p>
        </div>
      {/each}
    </div>
  </div>

  <div class="max-w-6xl mx-auto px-4 py-10 grid md:grid-cols-2 gap-8">
    <!-- URGENT NEEDS -->
    <div>
      <div class="flex items-center justify-between mb-4">
        <h2 class="text-lg font-bold text-gray-900">🚨 Urgent Needs</h2>
        <a href="/donate" class="text-sm text-brand-600 font-medium hover:underline">View all →</a>
      </div>
      {#if loading}
        <div class="space-y-3">
          {#each [1,2,3] as _}
            <div class="h-20 bg-gray-100 rounded-xl animate-pulse"></div>
          {/each}
        </div>
      {:else if criticalNeeds.length === 0}
        <div class="bg-white rounded-xl p-6 text-center text-gray-400 text-sm border border-gray-100">
          No urgent needs at the moment 🎉
        </div>
      {:else}
        <div class="space-y-3">
          {#each criticalNeeds as need}
            <div class="bg-white rounded-xl p-4 shadow-sm border border-gray-100 flex items-center justify-between">
              <div>
                <div class="font-medium text-gray-900">{need.item?.name}</div>
                <div class="text-xs text-gray-500 mt-0.5">{need.organization?.name} · needs {need.quantityNeeded} {need.item?.unit}</div>
              </div>
              <div class="flex items-center gap-2">
                <UrgencyBadge urgency={need.urgency} />
                <a href="/donate" class="text-xs bg-brand-500 text-white px-3 py-1.5 rounded-lg font-medium hover:bg-brand-600 transition-colors">
                  Donate
                </a>
              </div>
            </div>
          {/each}
        </div>
      {/if}
    </div>

    <!-- ANNOUNCEMENTS -->
    <div>
      <h2 class="text-lg font-bold text-gray-900 mb-4">📢 Latest Announcements</h2>
      {#if loading}
        <div class="space-y-3">
          {#each [1,2,3] as _}
            <div class="h-16 bg-gray-100 rounded-xl animate-pulse"></div>
          {/each}
        </div>
      {:else if recentAnnouncements.length === 0}
        <div class="bg-white rounded-xl p-6 text-center text-gray-400 text-sm border border-gray-100">
          No announcements yet
        </div>
      {:else}
        <div class="space-y-3">
          {#each recentAnnouncements as ann}
            <div class="bg-white rounded-xl p-4 shadow-sm border border-gray-100">
              <div class="flex items-start justify-between gap-2">
                <div>
                  <div class="font-medium text-gray-900 text-sm">{ann.organization?.name}</div>
                  <div class="text-xs text-gray-500 mt-0.5">{ann.message || `${ann.quantity} ${ann.item?.unit} of ${ann.item?.name}`}</div>
                </div>
                <StatusBadge status={ann.type} />
              </div>
            </div>
          {/each}
        </div>
      {/if}
    </div>
  </div>

  <!-- ORGANIZATIONS -->
  <div class="bg-white border-t border-gray-100">
    <div class="max-w-6xl mx-auto px-4 py-10">
      <h2 class="text-lg font-bold text-gray-900 mb-6 text-center">Partner Organizations</h2>
      <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-3">
        {#each organizations as org}
          <div class="bg-gray-50 rounded-xl p-3 text-center">
            <div class="text-xs font-medium text-gray-700 leading-snug">{org.name}</div>
            <div class="text-xs text-gray-400 mt-1">{org.type}</div>
          </div>
        {/each}
      </div>
    </div>
  </div>

  <!-- FOOTER -->
  <footer class="bg-gray-900 text-gray-400 text-center text-xs py-6">
    Built for Hack4Change 2026 · Moncton, NB
  </footer>
</div>
