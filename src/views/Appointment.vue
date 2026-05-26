<template>
  <div class="page">
    <nav class="glass-nav">
      <div class="nav-actions" style="gap:12px;">
        <button class="btn-back" @click="$router.push('/dashboard')">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>
        </button>
        <div class="glass-nav-brand">
          <span class="brand-icon">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><rect x="3" y="4" width="18" height="18" rx="2"/><path d="M16 2v4M8 2v4M3 10h18"/></svg>
          </span>
          课程预约
        </div>
      </div>
    </nav>

    <div class="page-container">
      <!-- 日期选择 -->
      <div class="date-picker glass">
        <label class="form-label" style="margin-bottom:6px;">选择日期</label>
        <input v-model="selectedDate" type="date" :min="minDate" class="form-input" @change="loadAppointments" />
      </div>

      <div v-if="!myCoachId" class="empty-state">
        <div class="empty-icon">
          <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="var(--c-text-muted)" stroke-width="1.5"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 00-3-3.87M16 3.13a4 4 0 010 7.75"/></svg>
        </div>
        <p>您还没有关联教练</p>
      </div>

      <!-- 时间段列表 -->
      <div v-for="slot in timeSlots" :key="slot.id" :class="['slot-card', { full: isFull(slot) }]">
        <div class="slot-top">
          <div class="slot-info">
            <h3>{{ slot.slotName }}</h3>
            <span class="slot-time">
              {{ slot.startTime?.substring(0,5) }} — {{ slot.endTime?.substring(0,5) }}
            </span>
          </div>
          <span :class="['badge', slotBadge(slot)]">{{ slotLabel(slot) }}</span>
        </div>
        <button
          class="btn btn-primary btn-block"
          :disabled="isFull(slot)"
          @click="openBook(slot)"
        >
          {{ isFull(slot) ? '已满' : '预约此时间段' }}
        </button>
      </div>

      <div v-if="timeSlots.length===0 && myCoachId" class="empty-state">
        <p>暂无可预约的时间段</p>
      </div>
    </div>

    <!-- 预约弹窗 -->
    <div v-if="bookSlot" class="modal-overlay" @click.self="bookSlot=null">
      <div class="modal-content">
        <div class="modal-header">
          <h2>确认预约</h2>
          <button class="btn-back" @click="bookSlot=null">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><path d="M18 6L6 18M6 6l12 12"/></svg>
          </button>
        </div>
        <div class="modal-body">
          <div class="book-summary">
            <div class="book-row">
              <span class="book-label">日期</span>
              <span>{{ selectedDate }}</span>
            </div>
            <div class="book-row">
              <span class="book-label">时间</span>
              <span>{{ bookSlot.slotName }} ({{ bookSlot.startTime?.substring(0,5) }}-{{ bookSlot.endTime?.substring(0,5) }})</span>
            </div>
          </div>
          <form @submit.prevent="handleBook">
            <div class="form-group">
              <label class="form-label">地点</label>
              <input v-model="location" class="form-input" placeholder="XX游泳馆" />
            </div>
            <div class="form-group">
              <label class="form-label">备注</label>
              <textarea v-model="notes" class="form-input" rows="3" placeholder="想学习什么？"></textarea>
            </div>
            <button type="submit" class="btn btn-primary btn-block btn-lg">确认预约</button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { getMyCoach, getTimeSlots, getAppointmentsForStudent, bookAppointment } from '../api'

const myCoachId = ref<number|null>(null)
const timeSlots = ref<any[]>([])
const bookings = ref<any[]>([])
const bookSlot = ref<any>(null)
const location = ref('')
const notes = ref('')
const selectedDate = ref('')
const minDate = computed(() => {
  const d = new Date(); d.setDate(d.getDate()+1)
  return d.toISOString().split('T')[0]
})

onMounted(() => {
  const d = new Date(); d.setDate(d.getDate()+1)
  selectedDate.value = d.toISOString().split('T')[0]
  loadMyCoach()
})

async function loadMyCoach() { const r=await getMyCoach(); if(r.code===200) { myCoachId.value=r.data.id; loadTimeSlots() } }
async function loadTimeSlots() { const r=await getTimeSlots(); if(r.code===200) timeSlots.value=r.data; await loadAppointments() }
async function loadAppointments() { if(!myCoachId.value||!selectedDate.value) return; const r=await getAppointmentsForStudent(myCoachId.value, selectedDate.value); if(r.code===200) bookings.value=r.data }
function isUnavailable(slot: any) { return bookings.value.some((b:any) => b.studentId===myCoachId.value && b.startTime===slot.startTime && b.notes==='教练设置不可用') }
function getBookedCount(slot: any) { return bookings.value.filter((b:any) => b.startTime===slot.startTime && !(b.studentId===myCoachId.value && b.notes==='教练设置不可用')).length }
function isFull(slot: any) { return isUnavailable(slot) || getBookedCount(slot) >= slot.maxCapacity }
function slotBadge(slot: any) { return isUnavailable(slot) ? 'badge-danger' : isFull(slot) ? 'badge-neutral' : 'badge-success' }
function slotLabel(slot: any) { return isUnavailable(slot) ? '已关闭' : isFull(slot) ? '已满' : `可预约 ${slot.maxCapacity - getBookedCount(slot)}/${slot.maxCapacity}` }
function openBook(slot: any) { bookSlot.value=slot; location.value=''; notes.value='' }
async function handleBook() {
  if(!myCoachId.value||!bookSlot.value) return
  const r = await bookAppointment({
    coachId: myCoachId.value, appointmentDate: selectedDate.value,
    startTime: bookSlot.value.startTime, endTime: bookSlot.value.endTime,
    location: location.value, notes: notes.value
  })
  if(r.code===200) { bookSlot.value=null; await loadAppointments() }
  else alert(r.message)
}
</script>

<style scoped>
.page { min-height: 100vh; background: transparent; }

.date-picker {
  padding: 18px 20px;
  margin-bottom: 16px;
}

.slot-card {
  background: var(--c-glass);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid var(--c-glass-border);
  border-radius: var(--radius-md);
  padding: 20px;
  margin-bottom: 12px;
  transition: all 0.3s ease;
}

.slot-card:hover {
  border-color: var(--c-glass-border-hover);
  box-shadow: var(--shadow-glass-lg);
}

.slot-card.full { background: var(--c-surface); opacity: 0.7; }

.slot-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 14px;
}

.slot-info h3 { font-family: var(--font-display); font-size: 16px; font-weight: 600; margin-bottom: 2px; color: var(--c-text); }
.slot-time { font-size: 13px; color: var(--c-text-secondary); }

.book-summary {
  background: var(--c-surface);
  border: 1px solid var(--c-glass-border);
  border-radius: var(--radius-sm);
  padding: 16px;
  margin-bottom: 20px;
}

.book-row {
  display: flex;
  justify-content: space-between;
  padding: 6px 0;
  font-size: 14px;
}

.book-label { color: var(--c-text-secondary); font-weight: 500; }
</style>
