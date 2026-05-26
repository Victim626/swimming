<template>
  <div class="page">
    <nav class="glass-nav">
      <div class="nav-actions" style="gap:12px;">
        <button class="btn-back" @click="$router.push('/dashboard')">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>
        </button>
        <div class="glass-nav-brand" style="gap:0;">
          <span class="brand-icon" style="margin-right:8px;">
            <svg width="16" height="16" viewBox="0 0 40 40" fill="none"><circle cx="12" cy="5" r="3" stroke="currentColor" stroke-width="2.5" fill="none"/><path d="M5 20 Q12 12 19 20" stroke="currentColor" stroke-width="2.5" fill="none"/></svg>
          </span>
          我的训练
        </div>
      </div>
    </nav>

    <div class="page-container">
      <!-- 切换标签 -->
      <div class="tabs">
        <button :class="['tab',{active:tab==='today'}]" @click="tab='today';loadToday()">今日训练</button>
        <button :class="['tab',{active:tab==='plans'}]" @click="tab='plans';loadAllPlans()">所有计划</button>
      </div>

      <!-- 今日训练 -->
      <div v-if="tab==='today'">
        <div class="today-header">
          <div>
            <h2 style="font-family:var(--font-display);font-size:20px;font-weight:700;">{{ todayStr }}</h2>
            <p style="font-size:13px;color:var(--c-text-secondary);margin-top:2px;">今天的训练任务</p>
          </div>
          <div class="today-stats">
            <span class="badge badge-success">{{ todayPlans.filter(p=>p.isChecked).length }} 已完成</span>
            <span class="badge badge-neutral">{{ todayPlans.length }} 总计</span>
          </div>
        </div>

        <div v-if="todayPlans.length===0" class="empty-state glass" style="padding:60px;">
          <div class="empty-icon">
            <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><circle cx="12" cy="12" r="10"/><path d="M12 6v6l4 2"/></svg>
          </div>
          <p>今日无训练任务，休息一下吧</p>
        </div>

        <div v-for="p in todayPlans" :key="p.id" :class="['train-card', { done: p.isChecked }]">
          <div class="train-card-left">
            <div class="train-day">{{ p.dayNumber }}</div>
          </div>
          <div class="train-card-body">
            <div class="train-card-header">
              <h3>{{ p.planName }}</h3>
              <span v-if="p.intensity" class="intensity-tag">{{ p.intensity }}</span>
            </div>
            <p v-if="p.notes" class="train-notes">{{ p.notes }}</p>
          </div>
          <div class="train-card-right">
            <button
              :class="['btn', p.isChecked ? 'btn-success' : 'btn-primary', 'btn-sm']"
              @click="toggleCheck(p.detailId || p.id, p.isChecked)"
            >
              {{ p.isChecked ? '已完成' : '打卡' }}
            </button>
          </div>
        </div>
      </div>

      <!-- 所有计划 -->
      <div v-if="tab==='plans'">
        <div v-if="allPlans.length===0" class="empty-state glass" style="padding:60px;">
          <div class="empty-icon">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><path d="M14 2v6h6"/></svg>
          </div>
          <p>暂无训练计划</p>
        </div>
        <div v-for="p in allPlans" :key="p.id" class="plan-row glass" @click="viewDetail(p.id)">
          <div class="plan-row-body">
            <h3>{{ p.planName }}</h3>
            <p class="plan-row-meta">{{ p.startDate }} ~ {{ p.endDate }}</p>
          </div>
          <span :class="['badge', statusBadge(p.status||'DRAFT')]">{{ statusMap[p.status||'DRAFT'] }}</span>
        </div>
      </div>
    </div>

    <!-- 计划详情 -->
    <div v-if="detailPlan" class="modal-overlay" @click.self="detailPlan=null">
      <div class="modal-content">
        <div class="modal-header">
          <h2>{{ detailPlan.planName }}</h2>
          <button class="btn-back" @click="detailPlan=null">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><path d="M18 6L6 18M6 6l12 12"/></svg>
          </button>
        </div>
        <div class="modal-body">
          <p class="plan-meta">周期：{{ detailPlan.startDate }} ~ {{ detailPlan.endDate }}</p>
          <div class="lane-divider"></div>
          <div v-for="d in detailDetails" :key="d.id" class="detail-day">
            <div class="detail-day-h">
              <span class="day-num">第 {{ d.dayNumber }} 天</span>
              <span v-if="d.trainingDate" class="day-date">{{ d.trainingDate }}</span>
            </div>
            <p class="day-desc">强度：{{ d.intensity || '未设置' }} · {{ d.notes || '无内容' }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getStudentTodayPlans, getStudentAllPlans, getStudentPlanDetail, toggleStudentCheck } from '../api'

const tab = ref('today')
const todayPlans = ref<any[]>([])
const allPlans = ref<any[]>([])
const detailPlan = ref<any>(null)
const detailDetails = ref<any[]>([])
const todayStr = new Date().toISOString().split('T')[0]
const statusMap: Record<string,string> = { DRAFT:'草稿', ACTIVE:'进行中', COMPLETED:'已完成' }

onMounted(() => loadToday())

async function loadToday() { const r=await getStudentTodayPlans(); if(r.code===200) todayPlans.value=r.data }
async function loadAllPlans() { const r=await getStudentAllPlans(); if(r.code===200) allPlans.value=r.data }
async function viewDetail(id: number) { const r=await getStudentPlanDetail(id); if(r.code===200) { detailPlan.value=r.data.plan; detailDetails.value=r.data.details } }
async function toggleCheck(detailId: number, checked: boolean) { const r=await toggleStudentCheck(detailId, !checked); if(r.code===200) await loadToday() }
function statusBadge(s: string) { return s==='ACTIVE'?'badge-success':s==='COMPLETED'?'badge-info':'badge-neutral' }
</script>

<style scoped>
.today-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.today-stats {
  display: flex;
  gap: 8px;
}

.train-card {
  display: flex;
  align-items: center;
  gap: 16px;
  background: var(--c-glass);
  border: 1px solid var(--c-glass-border);
  border-radius: var(--radius-md);
  padding: 18px 20px;
  margin-bottom: 10px;
  transition: all 0.3s;
  backdrop-filter: blur(12px);
}

.train-card:hover { border-color: var(--c-accent-dim); }
.train-card.done {
  border-color: rgba(52, 211, 153, 0.3);
  background: rgba(52, 211, 153, 0.06);
}

.train-card-left { flex-shrink: 0; }

.train-day {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, var(--c-accent), #0EA5E9);
  color: var(--c-deep-1);
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: var(--font-display);
  font-size: 18px;
  font-weight: 700;
  box-shadow: 0 0 12px var(--c-accent-glow);
}

.train-card.done .train-day {
  background: linear-gradient(135deg, var(--c-success), #059669);
  box-shadow: 0 0 12px rgba(52, 211, 153, 0.3);
}

.train-card-body { flex: 1; }

.train-card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.train-card-header h3 {
  font-family: var(--font-display);
  font-size: 16px;
  font-weight: 600;
}

.intensity-tag {
  font-size: 11px;
  padding: 2px 8px;
  background: var(--c-accent-dim);
  color: var(--c-accent);
  border-radius: 100px;
  font-weight: 500;
}

.train-notes { font-size: 13px; color: var(--c-text-secondary); }

.train-card-right { flex-shrink: 0; }

.plan-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 20px;
  margin-bottom: 10px;
  cursor: pointer;
}

.plan-row:hover { border-color: rgba(34, 211, 238, 0.3); }

.plan-row-body h3 {
  font-family: var(--font-display);
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 2px;
}

.plan-row-meta { font-size: 13px; color: var(--c-text-secondary); }

.plan-meta { font-size: 14px; color: var(--c-text-secondary); margin-bottom: 8px; }

.detail-day {
  padding: 12px 16px;
  margin-bottom: 8px;
  background: var(--c-surface);
  border-radius: var(--radius-sm);
  border-left: 3px solid var(--c-accent);
}

.detail-day-h {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 4px;
}

.day-num {
  font-family: var(--font-display);
  font-size: 15px;
  font-weight: 600;
}

.day-date { font-size: 12px; color: var(--c-text-muted); }
.day-desc { font-size: 13px; color: var(--c-text-secondary); }
</style>
