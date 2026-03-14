<script lang="ts">
  import { onMount } from 'svelte';
  import { getUnfulfilled } from '$lib/api/needs';
  import { getAll as getItems } from '$lib/api/items';
  import { createDonation } from '$lib/api/donations';
  import { showToast } from '$lib/stores/toast';
  import UrgencyBadge from '$lib/components/UrgencyBadge.svelte';
  import EmptyState from '$lib/components/EmptyState.svelte';
  import type { Need, Item, Donation } from '$lib/types';

  let needs = $state<Need[]>([]);
  let items = $state<Item[]>([]);
  let loading = $state(true);
  let activeTab = $state<'browse' | 'donate'>('browse');
  let submittedDonation = $state<Donation | null>(null);

  // Form fields
  let donorName = $state('');
  let donorEmail = $state('');
  let selectedItemId = $state<number | null>(null);
  let quantity = $state(1);
  let expiryDate = $state('');
  let submitting = $state(false);
  let filterUrgency = $state('');

  onMount(async () => {
    try {
      const [n, i] = await Promise.all([getUnfulfilled(), getItems()]);
      needs = n;
      items = i;
      if (i.length > 0) selectedItemId = i[0].id;
    } catch {}
    finally { loading = false; }
  });

  function preFill(need: Need) {
    selectedItemId = need.item.id;
    activeTab = 'donate';
  }

  let selectedItem = $derived(items.find(i => i.id === Number(selectedItemId)));
  let filteredNeeds = $derived(filterUrgency ? needs.filter(n => n.urgency === filterUrgency) : needs);

  async function handleDonate() {
    if (!selectedItemId) return;
    submitting = true;
    try {
      const body: any = {
        donorName,
        donorEmail,
        item: { id: Number(selectedItemId) },
        quantity: Number(quantity),
      };
      if (selectedItem?.expiryRelevant && expiryDate) body.expiryDate = expiryDate;
      submittedDonation = await createDonation(body);
      showToast('Thank you! Your donation has been submitted.');
      donorName = ''; donorEmail = ''; quantity = 1; expiryDate = '';
      activeTab = 'browse';
    } catch (e: any) {
      showToast(e.message || 'Failed to submit donation', 'error');
    } finally {
      submitting = false;
    }
  }
</script>

<svelte:head><title>Donate — ResourceBridge</title></svelte:head>

<div class="min-h-screen bg-gray-50">
  <!-- NAV -->
  <nav class="bg-white border-b border-gray-100 px-4 py-3">
    <div class="max-w-5xl mx-auto flex items-center justify-between">
      <a href="/" class="flex items-center gap-2">
        <div class="w-8 h-8 bg-brand-500 rounded-lg flex items-center justify-center">
          <span class="text-white font-bold text-sm">RB</span>
        </div>
        <span class="font-bold text-gray-900">ResourceBridge</span>
      </a>
      <a href="/login" class="text-sm text-gray-500 hover:text-brand-600">Staff login →</a>
    </div>
  </nav>

  <div class="max-w-5xl mx-auto px-4 py-8">
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-900">Support your community</h1>
      <p class="text-gray-500 mt-1">Browse what shelters need, or offer what you have.</p>
    </div>

    <!-- TABS -->
    <div class="flex gap-1 bg-gray-100 p-1 rounded-xl w-fit mb-6">
      <button
        class="px-5 py-2 rounded-lg text-sm font-medium transition-colors {activeTab === 'browse' ? 'bg-white text-gray-900 shadow-sm' : 'text-gray-500 hover:text-gray-700'}"
        onclick={() => activeTab = 'browse'}>Browse Needs</button>
      <button
        class="px-5 py-2 rounded-lg text-sm font-medium transition-colors {activeTab === 'donate' ? 'bg-white text-gray-900 shadow-sm' : 'text-gray-500 hover:text-gray-700'}"
        onclick={() => activeTab = 'donate'}>Make a Donation</button>
    </div>

    {#if activeTab === 'browse'}
      <!-- FILTER -->
      <div class="flex items-center gap-3 mb-4">
        <span class="text-sm text-gray-500">Filter:</span>
        {#each ['', 'CRITICAL', 'HIGH', 'MEDIUM', 'LOW'] as u}
          <button
            class="px-3 py-1 rounded-full text-xs font-medium border transition-colors
              {filterUrgency === u ? 'bg-brand-500 text-white border-brand-500' : 'bg-white text-gray-600 border-gray-200 hover:border-brand-300'}"
            onclick={() => filterUrgency = u}>{u || 'All'}</button>
        {/each}
      </div>

      {#if loading}
        <div class="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {#each [1,2,3,4,5,6] as _}
            <div class="h-36 bg-gray-100 rounded-xl animate-pulse"></div>
          {/each}
        </div>
      {:else if filteredNeeds.length === 0}
        <EmptyState message="No needs matching this filter" icon="🔍" />
      {:else}
        <div class="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {#each filteredNeeds as need}
            <div class="bg-white rounded-xl p-4 shadow-sm border border-gray-100 flex flex-col justify-between">
              <div>
                <div class="flex items-start justify-between gap-2 mb-2">
                  <div class="font-semibold text-gray-900">{need.item?.name}</div>
                  <UrgencyBadge urgency={need.urgency} />
                </div>
                <div class="text-xs text-gray-500">{need.organization?.name}</div>
                <div class="text-sm text-gray-700 mt-2">Needs <span class="font-semibold text-brand-600">{need.quantityNeeded} {need.item?.unit}</span></div>
              </div>
              <button
                onclick={() => preFill(need)}
                class="mt-4 w-full bg-brand-50 text-brand-700 hover:bg-brand-100 text-sm font-medium py-2 rounded-lg transition-colors">
                Donate this →
              </button>
            </div>
          {/each}
        </div>
      {/if}

    {:else}
      <!-- DONATION FORM -->
      {#if submittedDonation}
        <div class="bg-green-50 border border-green-100 rounded-2xl p-6 max-w-md">
          <div class="text-3xl mb-3">✅</div>
          <h3 class="font-bold text-green-800 text-lg">Donation submitted!</h3>
          <p class="text-green-700 text-sm mt-1">Your donation of <strong>{submittedDonation.quantity} {submittedDonation.item?.unit}</strong> of <strong>{submittedDonation.item?.name}</strong> has been received.</p>
          <p class="text-green-600 text-xs mt-2">Donation ID: #{submittedDonation.id} — A coordinator will match this to a shelter soon.</p>
          <button onclick={() => submittedDonation = null} class="mt-4 text-sm text-brand-600 font-medium hover:underline">Make another donation</button>
        </div>
      {:else}
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 max-w-md">
          <h2 class="font-semibold text-gray-900 mb-4">Offer a donation</h2>
          <form onsubmit={(e) => { e.preventDefault(); handleDonate(); }} class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1.5">Your name</label>
              <input type="text" bind:value={donorName} required class="w-full rounded-lg border-gray-200 focus:border-brand-500 focus:ring-brand-500 text-sm" placeholder="Jane Smith" />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1.5">Your email</label>
              <input type="email" bind:value={donorEmail} required class="w-full rounded-lg border-gray-200 focus:border-brand-500 focus:ring-brand-500 text-sm" placeholder="jane@email.com" />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1.5">Item</label>
              <select bind:value={selectedItemId} class="w-full rounded-lg border-gray-200 focus:border-brand-500 focus:ring-brand-500 text-sm">
                {#each items as item}
                  <option value={item.id}>{item.name}</option>
                {/each}
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1.5">Quantity</label>
              <input type="number" bind:value={quantity} min="1" required class="w-full rounded-lg border-gray-200 focus:border-brand-500 focus:ring-brand-500 text-sm" />
            </div>
            {#if selectedItem?.expiryRelevant}
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1.5">Expiry date <span class="text-gray-400 font-normal">(optional)</span></label>
                <input type="date" bind:value={expiryDate} class="w-full rounded-lg border-gray-200 focus:border-brand-500 focus:ring-brand-500 text-sm" />
              </div>
            {/if}
            <button type="submit" disabled={submitting} class="w-full bg-brand-500 hover:bg-brand-600 disabled:opacity-60 text-white font-semibold py-2.5 rounded-lg transition-colors">
              {submitting ? 'Submitting…' : 'Submit donation'}
            </button>
          </form>
        </div>
      {/if}
    {/if}
  </div>
</div>
