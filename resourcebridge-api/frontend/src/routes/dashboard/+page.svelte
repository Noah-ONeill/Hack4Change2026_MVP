<script lang="ts">
  import { onMount } from 'svelte';
  import { goto } from '$app/navigation';
  import { auth } from '$lib/stores/auth';

  onMount(() => {
    auth.init();
    if (!$auth.token) { goto('/login'); return; }
    goto('/dashboard/staff');
  });
</script>
