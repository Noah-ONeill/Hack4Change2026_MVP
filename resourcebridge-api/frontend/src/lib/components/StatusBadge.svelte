<script lang="ts">
  let { status }: { status: string } = $props();
  const map: Record<string, string> = {
    OFFERED:    'bg-blue-100 text-blue-700',
    ASSIGNED:   'bg-orange-100 text-orange-700',
    DELIVERED:  'bg-purple-100 text-purple-700',
    RECEIVED:   'bg-green-100 text-green-700',
    PENDING:    'bg-gray-100 text-gray-600',
    IN_TRANSIT: 'bg-orange-100 text-orange-700',
    COMPLETED:  'bg-green-100 text-green-700',
    EXPIRY:     'bg-red-100 text-red-700',
    SURPLUS:    'bg-green-100 text-green-700',
    URGENT:     'bg-orange-100 text-orange-700',
  };
</script>
<span class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium {map[status] ?? 'bg-gray-100 text-gray-600'}">
  {status.replace('_', ' ')}
</span>
