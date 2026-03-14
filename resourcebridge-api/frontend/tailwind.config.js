import forms from '@tailwindcss/forms';

/** @type {import('tailwindcss').Config} */
export default {
  content: ['./src/**/*.{html,js,svelte,ts}'],
  theme: {
    extend: {
      fontFamily: { sans: ['Inter', 'system-ui', 'sans-serif'] },
      colors: {
        brand: { 50:'#fff7ed', 100:'#ffedd5', 200:'#fed7aa', 400:'#fb923c', 500:'#f97316', 600:'#ea580c', 700:'#c2410c' }
      }
    },
  },
  plugins: [forms],
}
