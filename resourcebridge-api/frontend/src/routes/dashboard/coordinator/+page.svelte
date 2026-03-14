<script lang="ts">
  import { onMount } from 'svelte';
  import { auth } from '$lib/stores/auth';
  import { getAll as getAllNeeds } from '$lib/api/needs';
  import { getOffered } from '$lib/api/donations';
  import { getAll as getAllTransfers, createTransfer, updateStatus as updateTransferStatus } from '$lib/api/transfers';
  import { getAll as getOrgs } from '$lib/api/organizations';
  import { showToast } from '$lib/stores/toast';
  import UrgencyBadge from '$lib/components/UrgencyBadge.svelte';
  import StatusBadge from '$lib/components/StatusBadge.svelte';
  import Modal from '$lib/components/Modal.svelte';
  import EmptyState from '$lib/components/EmptyState.svelte';
  import type { Need, Donation, Transfer, Organization, TransferStatus } from '$lib/types';

  let needs = $state<Need[]>([]);
  let donations = $state<Donation[]>([]);
  let transfers = $state<Transfer[]>([]);
  let organizations = $state<Organization[]>([]);
  let loading = $state(true);
  let matchOpen = $state(false);

  let matchDonationId = $state<number | null>(null);
  let matchOrgId = $state<number | null>(null);
  let matchQty = $state(1);
  let matchSubmitting = $state(false);

  onMount(async () => {
    const token = $auth.token!;
    try {
      const [n, d, t, o] = await Promise.all([
        getAllNeeds(token),
        getOffered(token),
        getAllTransfers(token),
        getOrgs()
      ]);
      needs = n;
      donations = d;
      transfers = t;
      organizations = o;
    } catch (e: any) {
      showToast('Failed to load data', 'error');
    } finally {
      loading = false;
    }
  });

  let urgentNeeds = $derived(needs.filter(n => !n.fulfilled && (n.urgency === 'CRITICAL' || n.urgency === 'HIGH')));
  let stats = $derived({
    openNeeds: needs.filter(n => !n.fulfilled).length,
    offeredDonations: donations.length,
    pendingTransfers: transfers.filter(t => t.status === 'PENDING').length,
    criticalNeeds: needs.filter(n => !n.fulfilled && n.urgency === 'CRITICAL').length
  });

  function openMatch(donationId?: number, orgId?: number) {
    matchDonationId = donationId ?? (donations[0]?.id ?? null);
    matchOrgId = orgId ?? (organizations[0]?.id ?? null);
    matchQty = 1;
    matchOpen = true;
  }

  async function submitMatch() {
    if (!matchDonationId || !matchOrgId) return;
    matchSubmitting = true;
    try {
      await createTransfer({
        donation: { id: matchDonationId },
        toOrganization: { id: matchOrgId },
        quantityAssigned: Number(matchQty),
        coordinator: { id: $auth.id }
      }, $auth.token!);
      showToast('Transfer created successfully!');
      matchOpen = false;
      transfers = await getAllTransfers($auth.token!);
    } catch (e: any) {
      showToast(e.message || 'Failed to create transfer', 'error');
    } finally {
      matchSubmitting = false;
    }
  }

  async function changeTransferStatus(id: number, status: TransferStatus) {
    try {
      await updateTransferStatus(id, status, $auth.token!);
      transfers = transfers.map(t => t.id === id ? { ...t, status } : t);
      showToast('Status updated');
    } catch {
      showToast('Failed to update status', 'error');
    }
  }
</script>

<svelte:head><title>Coordinator Dashboard — ResourceBridge</title></svelte:head>

<div class="space-y-6">
  <!-- HEADER -->
  <div class="flex items-center justify-between">
    <div>
      <h1 class="text-2xl font-bold text-gray-900">Coordinator Dashboard</h1>
      <p class="text-sm text-gray-500 mt-0.5">Welcome, {$auth.name}</p>
    </div>
    <button onclick={() => openMatch()} class="bg-brand-500 hover:bg-brand-600 text-white font-semibold px-4 py-2 rounded-lg text-sm transition-colors">
      + Match Donation
    </button>
  </div>

  <!-- STATS -->
  {#if !loading}
  <div class="grid grid-cols-2 lg:grid-cols-4 gap-4">
    {#each [
      { label: 'Open Needs', value: stats.openNeeds, color: 'text-blue-600' },
      { label: 'Offered Donations', value: stats.offeredDonations, color: 'text-green-600' },
      { label: 'Pending Transfers', value: stats.pendingTransfers, color: 'text-orange-600' },
      { label: 'Critical Needs', value: stats.criticalNeeds, color: 'text-red-600' }
    ] as s}
      <div class="bg-white rounded-xl p-4 shadow-sm border border-gray-100">
        <div class="text-3xl font-bold {s.color}">{s.value}</div>
        <div class="text-sm text-gray-500 mt-1">{s.label}</div>
      </div>
    {/each}
  </div>
  {/if}

  <!-- TWO PANELS -->
  <div class="grid lg:grid-cols-2 gap-6">
    <!-- URGENT NEEDS -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100">
      <div class="flex items-center justify-between p-4 border-b border-gray-50">
        <h2 class="font-semibold text-gray-900">🚨 Urgent Needs</h2>
        <span class="text-xs text-gray-400">{urgentNeeds.length} items</span>
      </div>
      <div class="divide-y divide-gray-50 max-h-80 overflow-y-auto">
        {#if loading}
          {#each [1,2,3] as _}
            <div class="h-16 bg-gray-50 animate-pulse m-2 rounded-lg"></div>
          {/each}
        {:else if urgentNeeds.length === 0}
          <EmptyState message="No urgent needs right now" icon="✅" />
        {:else}
          {#each urgentNeeds as need}
            <div class="flex items-center justify-between px-4 py-3 hover:bg-gray-50 transition-colors">
              <div>
                <div class="text-sm font-medium text-gray-900">{need.item?.name}</div>
                <div class="text-xs text-gray-400">{need.organization?.name} · {need.quantityNeeded} {need.item?.unit}</div>
              </div>
              <div class="flex items-center gap-2">
                <UrgencyBadge urgency={need.urgency} />
                <button onclick={() => openMatch()} class="text-xs bg-brand-500 text-white px-2 py-1 rounded font-medium hover:bg-brand-600 transition-colors">Match</button>
              </div>
            </div>
          {/each}
        {/if}
      </div>
    </div>

    <!-- AVAILABLE DONATIONS -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100">
      <div class="flex items-center justify-between p-4 border-b border-gray-50">
        <h2 class="font-semibold text-gray-900">🎁 Available Donations</h2>
        <span class="text-xs text-gray-400">{donations.length} offered</span>
      </div>
      <div class="divide-y divide-gray-50 max-h-80 overflow-y-auto">
        {#if loading}
          {#each [1,2,3] as _}
            <div class="h-16 bg-gray-50 animate-pulse m-2 rounded-lg"></div>
          {/each}
        {:else if donations.length === 0}
          <EmptyState message="No donations offered yet" icon="📭" />
        {:else}
          {#each donations as don}
            <div class="flex items-center justify-between px-4 py-3 hover:bg-gray-50 transition-colors">
              <div>
                <div class="text-sm font-medium text-gray-900">{don.item?.name}</div>
                <div class="text-xs text-gray-400">
                  {don.donorName || 'Anonymous'} · {don.quantity} {don.item?.unit}
                  {#if don.expiryDate}<span class="text-red-400"> · exp {don.expiryDate}</span>{/if}
                </div>
              </div>
              <button onclick={() => openMatch(don.id)} class="text-xs bg-brand-500 text-white px-2 py-1 rounded font-medium hover:bg-brand-600 transition-colors">Assign</button>
            </div>
          {/each}
        {/if}
      </div>
    </div>
  </div>

  <!-- TRANSFERS TABLE -->
  <div class="bg-white rounded-xl shadow-sm border border-gray-100">
    <div class="p-4 border-b border-gray-50">
      <h2 class="font-semibold text-gray-900">🚚 All Transfers</h2>
    </div>
    {#if loading}
      <div class="p-4 space-y-2">
        {#each [1,2,3] as _}
          <div class="h-12 bg-gray-50 animate-pulse rounded-lg"></div>
        {/each}
      </div>
    {:else if transfers.length === 0}
      <EmptyState message="No transfers yet" icon="📦" />
    {:else}
      <div class="overflow-x-auto">
        <table class="w-full text-sm">
          <thead class="bg-gray-50 text-xs text-gray-500 uppercase tracking-wide">
            <tr>
              <th class="text-left px-4 py-3">Item</th>
              <th class="text-left px-4 py-3">To Organization</th>
              <th class="text-left px-4 py-3">Qty</th>
              <th class="text-left px-4 py-3">Status</th>
              <th class="text-left px-4 py-3">Action</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-50">
            {#each transfers as t}
              <tr class="hover:bg-gray-50 transition-colors">
                <td class="px-4 py-3 font-medium text-gray-900">{t.donation?.item?.name}</td>
                <td class="px-4 py-3 text-gray-600">{t.toOrganization?.name}</td>
                <td class="px-4 py-3 text-gray-600">{t.quantityAssigned}</td>
                <td class="px-4 py-3"><StatusBadge status={t.status} /></td>
                <td class="px-4 py-3">
                  {#if t.status === 'PENDING'}
                    <button onclick={() => changeTransferStatus(t.id, 'IN_TRANSIT')} class="text-xs text-brand-600 hover:underline font-medium">Mark In Transit</button>
                  {:else if t.status === 'IN_TRANSIT'}
                    <button onclick={() => changeTransferStatus(t.id, 'COMPLETED')} class="text-xs text-green-600 hover:underline font-medium">Mark Completed</button>
                  {:else}
                    <span class="text-xs text-gray-300">—</span>
                  {/if}
                </td>
              </tr>
            {/each}
          </tbody>
        </table>
      </div>
    {/if}
  </div>
</div>

<!-- MATCH MODAL -->
<Modal bind:open={matchOpen} title="Match Donation to Shelter">
  {#snippet children()}
  <div class="space-y-4">
    <div>
      <label class="block text-sm font-medium text-gray-700 mb-1.5">Donation</label>
      <select bind:value={matchDonationId} class="w-full rounded-lg border-gray-200 text-sm focus:border-brand-500 focus:ring-brand-500">
        {#each donations as d}
          <option value={d.id}>{d.item?.name} — {d.quantity} {d.item?.unit} ({d.donorName || 'Anonymous'})</option>
        {/each}
      </select>
    </div>
    <div>
      <label class="block text-sm font-medium text-gray-700 mb-1.5">Send to Organization</label>
      <select bind:value={matchOrgId} class="w-full rounded-lg border-gray-200 text-sm focus:border-brand-500 focus:ring-brand-500">
        {#each organizations as org}
          <option value={org.id}>{org.name}</option>
        {/each}
      </select>
    </div>
    <div>
      <label class="block text-sm font-medium text-gray-700 mb-1.5">Quantity to assign</label>
      <input type="number" bind:value={matchQty} min="1" class="w-full rounded-lg border-gray-200 text-sm focus:border-brand-500 focus:ring-brand-500" />
    </div>
    <button onclick={submitMatch} disabled={matchSubmitting} class="w-full bg-brand-500 hover:bg-brand-600 disabled:opacity-60 text-white font-semibold py-2.5 rounded-lg transition-colors text-sm">
      {matchSubmitting ? 'Creating…' : 'Create Transfer'}
    </button>
  </div>
  {/snippet}
</Modal>
