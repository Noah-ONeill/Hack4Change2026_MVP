<script lang="ts">
  let { urgency }: { urgency: string } = $props();
  const map: Record<string, string> = {
    CRITICAL: 'bg-red-100 text-red-700 border border-red-200',
    HIGH:     'bg-orange-100 text-orange-700 border border-orange-200',
    MEDIUM:   'bg-yellow-100 text-yellow-700 border border-yellow-200',
    LOW:      'bg-green-100 text-green-700 border border-green-200',
  };
</script>
<span class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-semibold {map[urgency] ?? 'bg-gray-100 text-gray-600'}">
  {urgency}
</span>
