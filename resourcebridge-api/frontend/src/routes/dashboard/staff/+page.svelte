<script lang="ts">
  import { onMount } from 'svelte';
  import { auth } from '$lib/stores/auth';
  import { getByOrg as getInventory, getExpiring, createInventory } from '$lib/api/inventory';
  import { getByOrg as getOrgNeeds, createNeed, fulfillNeed } from '$lib/api/needs';
  import { getByOrg as getTransfers } from '$lib/api/transfers';
  import { getByOrg as getAnnouncements, createAnnouncement } from '$lib/api/announcements';
  import { getAll as getItems } from '$lib/api/items';
  import { showToast } from '$lib/stores/toast';
  import StatusBadge from '$lib/components/StatusBadge.svelte';
  import UrgencyBadge from '$lib/components/UrgencyBadge.svelte';
  import EmptyState from '$lib/components/EmptyState.svelte';
  import type { Inventory, Need, Item, ItemCategory } from '$lib/types';

  const INV_CATEGORIES: { value: ItemCategory | ''; label: string; emoji: string }[] = [
    { value: '',         label: 'All',      emoji: '📦' },
    { value: 'FOOD',     label: 'Food',     emoji: '🍎' },
    { value: 'CLOTHING', label: 'Clothing', emoji: '👕' },
    { value: 'HYGIENE',  label: 'Hygiene',  emoji: '🧴' },
    { value: 'BEDDING',  label: 'Bedding',  emoji: '🛏️' },
    { value: 'OTHER',    label: 'Other',    emoji: '📁' },
  ];

  let invCategoryFilter = $state<ItemCategory | ''>('');

  let inventory = $state<Inventory[]>([]);
  let expiring = $state<Inventory[]>([]);
  let needs = $state<Need[]>([]);
  let transfers = $state<any[]>([]);
  let announcements = $state<any[]>([]);
  let items = $state<Item[]>([]);
  let loading = $state(true);

  // New Need form
  let needItemId = $state<number | null>(null);
  let needQty = $state(1);
  let needUrgency = $state('MEDIUM');
  let needSubmitting = $state(false);

  // New Inventory form
  let invItemId = $state<number | null>(null);
  let invQty = $state(1);
  let invExpiry = $state('');
  let invSubmitting = $state(false);

  // New Announcement form
  let annType = $state('SURPLUS');
  let annItemId = $state<number | null>(null);
  let annQty = $state(1);
  let annMessage = $state('');
  let annSubmitting = $state(false);

  onMount(async () => {
    const token = $auth.token!;
    const orgId = $auth.organizationId!;
    try {
      const [inv, exp, n, t, a, i] = await Promise.all([
        getInventory(orgId, token),
        getExpiring(orgId, 7, token),
        getOrgNeeds(orgId, token),
        getTransfers(orgId, token),
        getAnnouncements(orgId, token),
        getItems()
      ]);
      inventory = inv;
      expiring = exp;
      needs = n;
      transfers = t;
      announcements = a;
      items = i;
      if (i.length > 0) {
        needItemId = i[0].id;
        invItemId = i[0].id;
        annItemId = i[0].id;
      }
    } catch (e: any) {
      showToast('Failed to load data', 'error');
    } finally {
      loading = false;
    }
  });

  async function submitNeed() {
    if (!needItemId) return;
    needSubmitting = true;
    try {
      const created = await createNeed({
        organization: { id: $auth.organizationId },
        item: { id: needItemId },
        quantityNeeded: Number(needQty),
        urgency: needUrgency
      }, $auth.token!);
      needs = [...needs, created];
      showToast('Need posted successfully!');
      needQty = 1; needUrgency = 'MEDIUM';
    } catch (e: any) {
      showToast(e.message || 'Failed to post need', 'error');
    } finally {
      needSubmitting = false;
    }
  }

  async function submitInventory() {
    if (!invItemId) return;
    invSubmitting = true;
    try {
      const body: any = {
        organization: { id: $auth.organizationId },
        item: { id: invItemId },
        quantity: Number(invQty)
      };
      if (invExpiry) body.expiryDate = invExpiry;
      const created = await createInventory(body, $auth.token!);
      inventory = [...inventory, created];
      showToast('Inventory updated!');
      invQty = 1; invExpiry = '';
    } catch (e: any) {
      showToast(e.message || 'Failed to update inventory', 'error');
    } finally {
      invSubmitting = false;
    }
  }

  async function submitAnnouncement() {
    if (!annItemId) return;
    annSubmitting = true;
    try {
      const created = await createAnnouncement({
        organization: { id: $auth.organizationId },
        item: { id: annItemId },
        quantity: Number(annQty),
        type: annType,
        message: annMessage
      }, $auth.token!);
      announcements = [created, ...announcements];
      showToast('Announcement posted!');
      annMessage = ''; annQty = 1;
    } catch (e: any) {
      showToast(e.message || 'Failed to post announcement', 'error');
    } finally {
      annSubmitting = false;
    }
  }

  async function markNeedFulfilled(id: number) {
    try {
      await fulfillNeed(id, $auth.token!);
      needs = needs.map(n => n.id === id ? { ...n, fulfilled: true } : n);
      showToast('Need marked as fulfilled');
    } catch {
      showToast('Failed to update need', 'error');
    }
  }

  let openNeeds = $derived(needs.filter(n => !n.fulfilled));
  let filteredInventory = $derived(
    invCategoryFilter
      ? inventory.filter(inv => inv.item?.category === invCategoryFilter)
      : inventory
  );
</script>

<svelte:head><title>Staff Dashboard — ResourceBridge</title></svelte:head>

<div class="space-y-6">
  <div>
    <h1 class="text-2xl font-bold text-gray-900">Staff Dashboard</h1>
    <p class="text-sm text-gray-500 mt-0.5">Welcome, {$auth.name}</p>
  </div>

  <!-- EXPIRY ALERT -->
  {#if expiring.length > 0}
    <div class="bg-red-50 border border-red-100 rounded-xl p-4">
      <div class="flex items-center gap-2 mb-3">
        <span class="text-red-600 font-semibold text-sm">⚠️ {expiring.length} item{expiring.length > 1 ? 's' : ''} expiring within 7 days</span>
      </div>
      <div class="space-y-2">
        {#each expiring as inv}
          <div class="flex items-center justify-between bg-white rounded-lg px-3 py-2 text-sm">
            <span class="font-medium text-gray-800">{inv.item?.name}</span>
            <span class="text-gray-500">{inv.quantity} {inv.item?.unit}</span>
            <span class="text-red-600 font-medium">Expires {inv.expiryDate}</span>
          </div>
        {/each}
      </div>
    </div>
  {/if}

  <div class="grid lg:grid-cols-2 gap-6">
    <!-- INVENTORY -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100">
      <div class="p-4 border-b border-gray-50 flex items-center justify-between">
        <h2 class="font-semibold text-gray-900">📦 Inventory</h2>
        <span class="text-xs text-gray-400">{filteredInventory.length} / {inventory.length} items</span>
      </div>
      <!-- Category filter tabs -->
      <div class="px-4 pt-3 pb-2 flex gap-1.5 flex-wrap border-b border-gray-50">
        {#each INV_CATEGORIES as cat}
          {@const count = cat.value === '' ? inventory.length : inventory.filter(i => i.item?.category === cat.value).length}
          {#if count > 0 || cat.value === ''}
            <button
              onclick={() => invCategoryFilter = cat.value}
              class="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-medium border transition-all
                {invCategoryFilter === cat.value
                  ? 'bg-brand-500 text-white border-brand-500'
                  : 'bg-gray-50 text-gray-600 border-gray-200 hover:border-brand-300'}">
              {cat.emoji} {cat.label}
              {#if count > 0}<span class="opacity-70">{count}</span>{/if}
            </button>
          {/if}
        {/each}
      </div>
      <div class="divide-y divide-gray-50 max-h-48 overflow-y-auto">
        {#if loading}
          <div class="p-4 space-y-2">{#each [1,2,3] as _}<div class="h-10 bg-gray-50 animate-pulse rounded"></div>{/each}</div>
        {:else if filteredInventory.length === 0}
          {@const activeCat = INV_CATEGORIES.find(c => c.value === invCategoryFilter)}
          <EmptyState message={invCategoryFilter ? `No ${activeCat?.label} items yet` : 'No inventory yet'} icon={activeCat?.emoji ?? '📦'} />
        {:else}
          {#each filteredInventory as inv}
            {@const cat = INV_CATEGORIES.find(c => c.value === inv.item?.category)}
            <div class="flex items-center justify-between px-4 py-3 text-sm">
              <div>
                <span class="font-medium text-gray-800">{inv.item?.name}</span>
                {#if cat?.value}<span class="ml-1.5 text-xs text-gray-400">{cat.emoji}</span>{/if}
              </div>
              <div class="flex items-center gap-3">
                <span class="text-gray-600 font-medium">{inv.quantity} <span class="text-gray-400 font-normal">{inv.item?.unit}</span></span>
                {#if inv.expiryDate}<span class="text-xs text-orange-500 font-medium">exp {inv.expiryDate}</span>{/if}
              </div>
            </div>
          {/each}
        {/if}
      </div>
      <!-- Add Inventory -->
      <div class="p-4 border-t border-gray-50 bg-gray-50/50">
        <div class="text-xs font-medium text-gray-500 mb-2">Add to inventory</div>
        <div class="flex gap-2">
          <select bind:value={invItemId} class="flex-1 rounded-lg border-gray-200 text-xs focus:border-brand-500 focus:ring-brand-500">
            {#each items as item}<option value={item.id}>{item.name}</option>{/each}
          </select>
          <input type="number" bind:value={invQty} min="1" class="w-16 rounded-lg border-gray-200 text-xs focus:border-brand-500 focus:ring-brand-500" placeholder="Qty" />
          <button onclick={submitInventory} disabled={invSubmitting} class="bg-brand-500 text-white text-xs px-3 py-1.5 rounded-lg font-medium hover:bg-brand-600 transition-colors disabled:opacity-60">
            {invSubmitting ? '…' : 'Add'}
          </button>
        </div>
      </div>
    </div>

    <!-- NEEDS -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100">
      <div class="p-4 border-b border-gray-50 flex items-center justify-between">
        <h2 class="font-semibold text-gray-900">📋 Our Needs</h2>
        <span class="text-xs text-gray-400">{openNeeds.length} open</span>
      </div>
      <div class="divide-y divide-gray-50 max-h-48 overflow-y-auto">
        {#if loading}
          <div class="p-4 space-y-2">{#each [1,2,3] as _}<div class="h-10 bg-gray-50 animate-pulse rounded"></div>{/each}</div>
        {:else if openNeeds.length === 0}
          <EmptyState message="No open needs" icon="✅" />
        {:else}
          {#each openNeeds as need}
            <div class="flex items-center justify-between px-4 py-3 text-sm">
              <div>
                <span class="font-medium text-gray-800">{need.item?.name}</span>
                <span class="text-gray-400 text-xs ml-2">{need.quantityNeeded} {need.item?.unit}</span>
              </div>
              <div class="flex items-center gap-2">
                <UrgencyBadge urgency={need.urgency} />
                <button onclick={() => markNeedFulfilled(need.id)} class="text-xs text-green-600 hover:underline">Fulfilled</button>
              </div>
            </div>
          {/each}
        {/if}
      </div>
      <!-- Post Need -->
      <div class="p-4 border-t border-gray-50 bg-gray-50/50">
        <div class="text-xs font-medium text-gray-500 mb-2">Post a need</div>
        <div class="flex gap-2 flex-wrap">
          <select bind:value={needItemId} class="flex-1 min-w-0 rounded-lg border-gray-200 text-xs focus:border-brand-500 focus:ring-brand-500">
            {#each items as item}<option value={item.id}>{item.name}</option>{/each}
          </select>
          <input type="number" bind:value={needQty} min="1" class="w-14 rounded-lg border-gray-200 text-xs focus:border-brand-500 focus:ring-brand-500" />
          <select bind:value={needUrgency} class="rounded-lg border-gray-200 text-xs focus:border-brand-500 focus:ring-brand-500">
            {#each ['LOW','MEDIUM','HIGH','CRITICAL'] as u}<option>{u}</option>{/each}
          </select>
          <button onclick={submitNeed} disabled={needSubmitting} class="bg-brand-500 text-white text-xs px-3 py-1.5 rounded-lg font-medium hover:bg-brand-600 transition-colors disabled:opacity-60">
            {needSubmitting ? '…' : 'Post'}
          </button>
        </div>
      </div>
    </div>
  </div>

  <!-- TRANSFERS + ANNOUNCEMENTS -->
  <div class="grid lg:grid-cols-2 gap-6">
    <!-- INCOMING TRANSFERS -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100">
      <div class="p-4 border-b border-gray-50 flex items-center justify-between">
        <h2 class="font-semibold text-gray-900">🚚 Incoming Transfers</h2>
        <span class="text-xs text-gray-400">{transfers.length} total</span>
      </div>
      {#if loading}
        <div class="p-4 space-y-2">{#each [1,2] as _}<div class="h-12 bg-gray-50 animate-pulse rounded"></div>{/each}</div>
      {:else if transfers.length === 0}
        <EmptyState message="No incoming transfers" icon="📭" />
      {:else}
        <div class="divide-y divide-gray-50">
          {#each transfers as t}
            <div class="px-4 py-3 text-sm">
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-2">
                  <span class="font-medium text-gray-800">{t.donation?.item?.name}</span>
                  <span class="text-xs text-gray-400">{t.quantityAssigned} {t.donation?.item?.unit}</span>
                  <!-- Pickup badge -->
                  {#if t.donation?.donationType === 'PICKUP_REQUEST'}
                    <span class="inline-flex items-center gap-1 px-2 py-0.5 bg-blue-50 text-blue-700 rounded-full text-xs font-medium">
                      🚐 Pickup
                    </span>
                  {:else}
                    <span class="inline-flex items-center gap-1 px-2 py-0.5 bg-gray-100 text-gray-500 rounded-full text-xs font-medium">
                      🏠 Drop-off
                    </span>
                  {/if}
                </div>
                <StatusBadge status={t.status} />
              </div>
              <!-- Pickup address details -->
              {#if t.donation?.donationType === 'PICKUP_REQUEST' && t.donation?.pickupAddress}
                <div class="mt-2 flex items-start gap-1.5 bg-blue-50 rounded-lg px-3 py-2">
                  <span class="text-blue-400 text-xs mt-0.5">📍</span>
                  <div>
                    <div class="text-xs font-medium text-blue-800">{t.donation.pickupAddress}, {t.donation.pickupCity}</div>
                    <div class="text-xs text-blue-500">
                      {t.donation.donorName}
                      {#if t.donation.donorPhone}
                        · <a href="tel:{t.donation.donorPhone}" class="hover:underline font-medium">📞 {t.donation.donorPhone}</a>
                      {/if}
                      · <a href="mailto:{t.donation.donorEmail}" class="hover:underline">{t.donation.donorEmail}</a>
                    </div>
                  </div>
                </div>
              {/if}
            </div>
          {/each}
        </div>
      {/if}
    </div>

    <!-- ANNOUNCEMENTS -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100">
      <div class="p-4 border-b border-gray-50">
        <h2 class="font-semibold text-gray-900">📢 Announcements</h2>
      </div>
      <div class="divide-y divide-gray-50 max-h-40 overflow-y-auto">
        {#if announcements.length === 0}
          <EmptyState message="No announcements yet" icon="📢" />
        {:else}
          {#each announcements as ann}
            <div class="flex items-center justify-between px-4 py-3 text-sm">
              <span class="text-gray-700 text-xs">{ann.message || ann.item?.name}</span>
              <StatusBadge status={ann.type} />
            </div>
          {/each}
        {/if}
      </div>
      <!-- Post Announcement -->
      <div class="p-4 border-t border-gray-50 bg-gray-50/50 space-y-2">
        <div class="text-xs font-medium text-gray-500">Post announcement</div>
        <div class="flex gap-2 flex-wrap">
          <select bind:value={annType} class="rounded-lg border-gray-200 text-xs focus:border-brand-500 focus:ring-brand-500">
            {#each ['SURPLUS','EXPIRY','URGENT'] as t}<option>{t}</option>{/each}
          </select>
          <select bind:value={annItemId} class="flex-1 min-w-0 rounded-lg border-gray-200 text-xs focus:border-brand-500 focus:ring-brand-500">
            {#each items as item}<option value={item.id}>{item.name}</option>{/each}
          </select>
          <input type="number" bind:value={annQty} min="1" class="w-14 rounded-lg border-gray-200 text-xs" />
        </div>
        <div class="flex gap-2">
          <input type="text" bind:value={annMessage} class="flex-1 rounded-lg border-gray-200 text-xs focus:border-brand-500 focus:ring-brand-500" placeholder="Optional message…" />
          <button onclick={submitAnnouncement} disabled={annSubmitting} class="bg-brand-500 text-white text-xs px-3 py-1.5 rounded-lg font-medium hover:bg-brand-600 transition-colors disabled:opacity-60">
            {annSubmitting ? '…' : 'Post'}
          </button>
        </div>
      </div>
    </div>
  </div>
</div>
