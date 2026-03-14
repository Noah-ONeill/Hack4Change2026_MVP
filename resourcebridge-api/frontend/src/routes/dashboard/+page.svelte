<script lang="ts">
  import { onMount } from 'svelte';
  import { goto } from '$app/navigation';
  import { auth } from '$lib/stores/auth';

  onMount(() => {
    auth.init();
    if (!$auth.token) { goto('/login'); return; }
    if ($auth.role === 'COORDINATOR') goto('/dashboard/coordinator');
    else if ($auth.role === 'STAFF') goto('/dashboard/staff');
    else goto('/login');
  });
</script>
