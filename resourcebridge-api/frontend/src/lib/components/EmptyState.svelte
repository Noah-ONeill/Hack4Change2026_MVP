<script lang="ts">
  let { message = 'No data yet', icon = '📭' }: { message?: string; icon?: string } = $props();
</script>
<div class="flex flex-col items-center justify-center py-12 text-gray-400">
  <span class="text-4xl mb-3">{icon}</span>
  <p class="text-sm">{message}</p>
</div>
