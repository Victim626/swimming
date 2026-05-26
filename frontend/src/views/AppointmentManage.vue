<template>
  <div class="page">
    <nav class="glass-nav">
      <div class="nav-actions" style="gap:12px;">
        <button class="btn-back" @click="$router.push('/dashboard')">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>
        </button>
        <div class="glass-nav-brand">
          <span class="brand-icon">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><rect x="2" y="3" width="20" height="18" rx="2"/><path d="M2 9h20"/></svg>
          </span>
          预约管理
        </div>
      </div>
    </nav>

    <div class="page-container">
      <!-- 操作栏 -->
      <div class="action-bar">
        <div class="date-input-group">
          <label class="form-label" style="margin-bottom:4px;">筛选日期</label>
          <div style="display:flex;gap:8px;">
            <input v-model="filterDate" type="date" class="form-input" style="width:auto;" @change="loadAll" />
            <button class="btn btn-ghost btn-sm" @click="loadAll">显示所有</button>
          </div>
        </div>
        <div class="action-btns">
          <button class="btn btn-ghost btn-sm" @click="showUnavail=true">设置不可用</button>
          <button class="btn btn-ghost btn-sm" @click="handleClean">清除过期</button>
        </div>
      </div>

      <!-- Tabs -->
      <div class="tabs">
        <button v-for="t in tabs" :key="t.key" :class="['tab',{active:currentTab===t.key}]" @click="currentTab=t.key">{{ t.label }}</button>
      </div>

      <!-- 列表 -->
      <div v-if="filtered.length===0" class="empty-state">
        <p>暂无预约记录</p>
      </div>

      <div v-for="a in filtered" :key="a.id" :class="['appt-card', a.notes==='教练设置不可用'?'unavail':a.status.toLowerCase()]">
        <div class="appt-card-top">
          <div class="appt-card-info">
            <h3>{{ a.notes==='教练设置不可用' ? '不可用时间段' : a.studentName || '学员' }}</h3>
            <p class="appt-time">{{ a.appointmentDate }} · {{ a.startTime?.substring(0,5) }}-{{ a.endTime?.substring(0,5) }}</p>
            <p v-if="a.location" class="appt-location">{{ a.location }}</p>
            <p v-if="a.notes && a.notes!=='教练设置不可用'" class="appt-notes">{{ a.notes }}</p>
          </div>
          <span :class="['badge', statusBadge(a)]">{{ statusLabel(a) }}</span>
        </div>
        <div class="appt-card-actions">
          <template v-if="a.notes==='教练设置不可用'">
            <button class="btn btn-ghost btn-sm" @click="removeUnavailable(a.id)">取消不可用</button>
          </template>
          <template v-else-if="a.status==='PENDING'">
            <button class="btn btn-success btn-sm" @click="confirm(a.id)">确认</button>
            <button class="btn btn-danger btn-sm" @click="cancel(a.id)">取消</button>
          </template>
          <template v-else>
            <button
              :disabled="a.status==='CANCELLED'||a.status==='COMPLETED'"
              class="btn btn-danger btn-sm"
              @click="cancel(a.id)"
            >取消</button>
          </template>
        </div>
      </div>
    </div>

    <!-- 设置不可用 -->
    <div v-if="showUnavail" class="modal-overlay" @click.self="showUnavail=false">
      <div class="modal-content">
        <div class="modal-header">
          <h2>设置时间段不可用</h2>
          <button class="btn-back" @click="showUnavail=false">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><path d="M18 6L6 18M6 6l12 12"/></svg>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">日期</label>
            <input v-model="unavailDate" type="date" class="form-input" />
          </div>
          <div class="form-group">
            <label class="form-label">时间段</label>
            <select v-model="unavailSlot" class="form-input">
              <option value="08:30-10:00">早上 08:30-10:00</option>
              <option value="15:30-17:00">下午 15:30-17:00</option>
              <option value="19:30-21:00">晚上 19:30-21:00</option>
            </select>
          </div>
          <button class="btn btn-primary btn-block" @click="handleSetUnavail">确认设置</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getCoachAllAppointments, confirmAppointment, cancelAppointment, deleteAppointment, closeTimeSlot, cleanExpiredAppointments } from '../api'

const filterDate = ref('')
const allAppts = ref<any[]>([])
const currentTab = ref('all')
const showUnavail = ref(false)
const unavailDate = ref(new Date().toISOString().split('T')[0])
const unavailSlot = ref('08:30-10:00')
const tabs = [
  { key:'all',label:'全部' }, { key:'PENDING',label:'待确认' },
  { key:'CONFIRMED',label:'已确认' }, { key:'COMPLETED',label:'已完成' }, { key:'CANCELLED',label:'已取消' }
]

onMounted(() => loadAll())

async function loadAll() { const r=await getCoachAllAppointments(); if(r.code===200) allAppts.value=r.data }
const filtered = computed(() => {
  let items = allAppts.value
  if(currentTab.value!=='all') items = items.filter(a => a.status===currentTab.value)
  return items
})
function statusBadge(a: any) {
  if(a.notes==='教练设置不可用') return 'badge-neutral'
  return a.status==='PENDING'?'badge-warning':a.status==='CONFIRMED'?'badge-success':a.status==='COMPLETED'?'badge-info':'badge-danger'
}
function statusLabel(a: any) {
  if(a.notes==='教练设置不可用') return '不可用'
  return { PENDING:'待确认', CONFIRMED:'已确认', COMPLETED:'已完成', CANCELLED:'已取消' }[a.status]||a.status
}
async function confirm(id: number) { if(!confirm('确认？')) return; await confirmAppointment(id); await loadAll() }
async function cancel(id: number) { if(!confirm('取消？')) return; await cancelAppointment(id); await loadAll() }
async function removeUnavailable(id: number) { if(!confirm('取消不可用？')) return; await deleteAppointment(id); await loadAll() }
async function handleSetUnavail() {
  const [st, et] = unavailSlot.value.split('-')
  await closeTimeSlot({ appointmentDate: unavailDate.value, startTime: st+':00', endTime: et+':00' })
  showUnavail.value=false; await loadAll()
}
async function handleClean() { if(!confirm('清除过期预约？')) return; const r=await cleanExpiredAppointments(); alert(r.message||'已清除'); await loadAll() }
</script>

<style scoped>
.page { min-height: 100vh; background: transparent; }

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  background: var(--c-glass);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid var(--c-glass-border);
  border-radius: var(--radius-md);
  padding: 16px 20px;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 12px;
}

.action-btns { display: flex; gap: 8px; }

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
.appt-card.unavail { border-left: 3px solid var(--c-text-muted); background: var(--c-surface); }

.appt-card-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 14px;
}

.appt-card-info h3 { font-family: var(--font-display); font-size: 15px; font-weight: 600; margin-bottom: 4px; }
.appt-time { font-size: 13px; color: var(--c-text-secondary); }
.appt-location, .appt-notes { font-size: 13px; color: var(--c-text-secondary); margin-top: 2px; }

.appt-card-actions {
  display: flex;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid var(--c-glass-border);
}
</style>
