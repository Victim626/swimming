<template>
  <div class="page">
    <nav class="glass-nav">
      <div class="nav-actions" style="gap:12px;">
        <button class="btn-back" @click="$router.push('/dashboard')">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>
        </button>
        <div class="glass-nav-brand">
          <span class="brand-icon">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2"/><rect x="9" y="3" width="6" height="4" rx="1"/></svg>
          </span>
          我的预约
        </div>
      </div>
    </nav>

    <div class="page-container">
      <!-- Tabs -->
      <div class="tabs">
        <button v-for="t in tabs" :key="t.key" :class="['tab',{active:currentTab===t.key}]" @click="currentTab=t.key">{{ t.label }}</button>
      </div>

      <!-- 列表 -->
      <div v-if="filtered.length===0" class="empty-state">
        <div class="empty-icon">
          <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="var(--c-text-muted)" stroke-width="1.5"><rect x="3" y="4" width="18" height="18" rx="2"/><path d="M16 2v4M8 2v4M3 10h18"/></svg>
        </div>
        <p>暂无预约记录</p>
        <router-link to="/appointment" class="btn btn-primary btn-sm" style="margin-top:16px;">去预约</router-link>
      </div>

      <div v-for="a in filtered" :key="a.id" :class="['appt-card', a.status.toLowerCase()]">
        <div class="appt-card-top">
          <div class="appt-card-info">
            <h3>课程预约</h3>
            <p class="appt-time">{{ a.appointmentDate }} · {{ a.startTime?.substring(0,5) }}-{{ a.endTime?.substring(0,5) }}</p>
            <p v-if="a.location" class="appt-detail">{{ a.location }}</p>
            <p v-if="a.notes" class="appt-detail">{{ a.notes }}</p>
          </div>
          <span :class="['badge', statusBadge(a.status)]">{{ statusLabel(a.status) }}</span>
        </div>
        <div v-if="a.status==='PENDING'||a.status==='CONFIRMED'" class="appt-card-action">
          <button class="btn btn-danger btn-sm" @click="handleCancel(a.id)">取消预约</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getMyAppointments, cancelAppointment } from '../api'

const allAppts = ref<any[]>([])
const currentTab = ref('all')
const tabs = [
  { key:'all',label:'全部' }, { key:'PENDING',label:'待确认' },
  { key:'CONFIRMED',label:'已确认' }, { key:'COMPLETED',label:'已完成' }, { key:'CANCELLED',label:'已取消' }
]

onMounted(async () => { const r=await getMyAppointments(); if(r.code===200) allAppts.value=r.data })
const filtered = computed(() => currentTab.value==='all' ? allAppts.value : allAppts.value.filter(a => a.status===currentTab.value))
function statusBadge(s: string) { return s==='PENDING'?'badge-warning':s==='CONFIRMED'?'badge-success':s==='COMPLETED'?'badge-info':'badge-danger' }
function statusLabel(s: string) { return { PENDING:'待确认',CONFIRMED:'已确认',COMPLETED:'已完成',CANCELLED:'已取消' }[s]||s }
async function handleCancel(id: number) { if(!confirm('取消预约？')) return; await cancelAppointment(id); const r=await getMyAppointments(); if(r.code===200) allAppts.value=r.data }
</script>

<style scoped>
.page { min-height: 100vh; background: transparent; }

.appt-card {
  background: var(--c-glass);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid var(--c-glass-border);
  border-radius: var(--radius-md);
  padding: 20px;
  margin-bottom: 10px;
}

.appt-card.pending { border-left: 3px solid var(--c-warning); }
.appt-card.confirmed { border-left: 3px solid var(--c-success); }
.appt-card.completed { border-left: 3px solid var(--c-info); }
.appt-card.cancelled { border-left: 3px solid var(--c-danger); opacity: 0.7; }

.appt-card-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.appt-card-info h3 { font-family: var(--font-display); font-size: 15px; font-weight: 600; margin-bottom: 4px; }
.appt-time { font-size: 13px; color: var(--c-text-secondary); }
.appt-detail { font-size: 13px; color: var(--c-text-secondary); margin-top: 2px; }

.appt-card-action {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid var(--c-glass-border);
}
</style>
