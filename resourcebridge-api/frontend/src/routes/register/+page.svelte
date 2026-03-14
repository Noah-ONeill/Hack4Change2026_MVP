<script lang="ts">
  import { goto } from '$app/navigation';
  import { onMount } from 'svelte';
  import { register } from '$lib/api/auth';
  import { getAll as getOrgs } from '$lib/api/organizations';
  import { auth } from '$lib/stores/auth';
  import { showToast } from '$lib/stores/toast';
  import type { Organization } from '$lib/types';

  let name = $state('');
  let email = $state('');
  let password = $state('');
  let role = $state<'COORDINATOR' | 'STAFF'>('STAFF');
  let organizationId = $state<number | null>(null);
  let organizations = $state<Organization[]>([]);
  let loading = $state(false);
  let error = $state('');

  onMount(async () => {
    try {
      const orgs = await getOrgs();
      organizations = orgs;
      if (orgs.length > 0) organizationId = orgs[0].id;
    } catch {}
  });

  async function handleSubmit() {
    if (!organizationId) { error = 'Please select an organization'; return; }
    loading = true;
    error = '';
    try {
      const res = await register({ name, email, password, role, organizationId });
      auth.setAuth(res);
      showToast(`Welcome, ${res.name}!`);
      goto('/dashboard');
    } catch (e: any) {
      error = e.message || 'Registration failed';
    } finally {
      loading = false;
    }
  }
</script>

<svelte:head><title>Register — ResourceBridge</title></svelte:head>

<div class="min-h-screen bg-gray-50 flex items-center justify-center px-4 py-10">
  <div class="w-full max-w-sm">
    <div class="text-center mb-8">
      <div class="w-14 h-14 bg-brand-500 rounded-2xl flex items-center justify-center mx-auto mb-4 shadow-lg">
        <span class="text-white font-bold text-xl">RB</span>
      </div>
      <h1 class="text-2xl font-bold text-gray-900">Create account</h1>
      <p class="text-sm text-gray-500 mt-1">For shelter staff and coordinators</p>
    </div>

    <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
      {#if error}
        <div class="bg-red-50 border border-red-100 text-red-700 text-sm rounded-lg px-4 py-3 mb-4">{error}</div>
      {/if}

      <form onsubmit={(e) => { e.preventDefault(); handleSubmit(); }} class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1.5">Full name</label>
          <input type="text" bind:value={name} required class="w-full rounded-lg border-gray-200 focus:border-brand-500 focus:ring-brand-500 text-sm" placeholder="Jane Smith" />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1.5">Email</label>
          <input type="email" bind:value={email} required class="w-full rounded-lg border-gray-200 focus:border-brand-500 focus:ring-brand-500 text-sm" placeholder="you@organization.ca" />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1.5">Password</label>
          <input type="password" bind:value={password} required class="w-full rounded-lg border-gray-200 focus:border-brand-500 focus:ring-brand-500 text-sm" placeholder="••••••••" />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1.5">Role</label>
          <select bind:value={role} class="w-full rounded-lg border-gray-200 focus:border-brand-500 focus:ring-brand-500 text-sm">
            <option value="STAFF">Shelter Staff</option>
            <option value="COORDINATOR">Program Coordinator</option>
          </select>
          <p class="text-xs text-gray-400 mt-1">
            {role === 'COORDINATOR' ? 'Coordinators manage multiple shelters and assign donations.' : 'Staff manage inventory and needs for their shelter.'}
          </p>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1.5">Organization</label>
          <select bind:value={organizationId} class="w-full rounded-lg border-gray-200 focus:border-brand-500 focus:ring-brand-500 text-sm">
            {#each organizations as org}
              <option value={org.id}>{org.name}</option>
            {/each}
          </select>
        </div>
        <button type="submit" disabled={loading} class="w-full bg-brand-500 hover:bg-brand-600 disabled:opacity-60 text-white font-semibold py-2.5 rounded-lg transition-colors text-sm">
          {loading ? 'Creating account…' : 'Create account'}
        </button>
      </form>
    </div>

    <p class="text-center text-sm text-gray-500 mt-4">
      Already have an account? <a href="/login" class="text-brand-600 font-medium hover:underline">Sign in</a>
    </p>
  </div>
</div>
