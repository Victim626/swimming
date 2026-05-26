import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User } from '../types'

export const useUserStore = defineStore('user', () => {
  const user = ref<User | null>(JSON.parse(sessionStorage.getItem('currentUser') || 'null'))

  function setUser(u: User | null) {
    user.value = u
    if (u) sessionStorage.setItem('currentUser', JSON.stringify(u))
    else sessionStorage.removeItem('currentUser')
  }

  function clearUser() {
    setUser(null)
  }

  const isLoggedIn = computed(() => user.value !== null)
  const isCoach = computed(() => user.value?.role === 'COACH')
  const isStudent = computed(() => user.value?.role === 'STUDENT')

  return { user, setUser, clearUser, isLoggedIn, isCoach, isStudent }
})
