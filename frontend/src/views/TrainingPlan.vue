<template>
  <div class="page">
    <nav class="glass-nav">
      <div class="nav-actions" style="gap:12px;">
        <button class="btn-back" @click="$router.push('/dashboard')">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>
        </button>
        <div class="glass-nav-brand" style="gap:0;">
          <span class="brand-icon" style="margin-right:8px;">
            <svg width="16" height="16" viewBox="0 0 40 40" fill="none"><rect x="3" y="4" width="18" height="16" rx="2" stroke="currentColor" stroke-width="2.5" fill="none"/><path d="M8 10h8M8 14h6" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/></svg>
          </span>
          训练计划
        </div>
      </div>
    </nav>

    <div class="page-container">
      <div class="plan-layout">
        <!-- 学员列表 -->
        <div class="plan-sidebar glass">
          <div class="sidebar-header">
            <h3>我的学员</h3>
            <span class="badge badge-neutral">{{ students.length }}</span>
          </div>
          <div class="student-list">
            <div v-if="students.length===0" class="empty-state" style="padding:24px;">
              <div class="empty-icon">
                <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><circle cx="12" cy="8" r="4"/><path d="M4 20c0-4 3.6-8 8-8s8 4 8 8"/></svg>
              </div>
              <p>暂无学员</p>
            </div>
            <div
              v-for="s in students"
              :key="s.studentId"
              :class="['student-chip', { active: selectedId===s.studentId }]"
              @click="selectStudent(s)"
            >
              <div class="chip-avatar">{{ (s.studentRealName||s.studentUsername||'?')[0] }}</div>
              <div class="chip-info">
                <div class="chip-name">{{ s.studentRealName || s.studentUsername || '学员'+s.studentId }}</div>
                <div class="chip-sub">已关联学员</div>
              </div>
            </div>
          </div>
          <button class="btn btn-primary btn-sm btn-block" style="margin-top:12px;" @click="showAddModal=true">+ 添加学员</button>
        </div>

        <!-- 计划列表 -->
        <div class="plan-main">
          <div class="plan-header">
            <div>
              <h2>{{ selectedName ? selectedName + ' 的训练计划' : '请选择学员' }}</h2>
              <p class="plan-sub" v-if="selectedName">{{ plans.length }} 个计划</p>
            </div>
            <button class="btn btn-primary" :disabled="!selectedId" @click="openCreate">+ 创建计划</button>
          </div>

          <div class="lane-divider"></div>

          <div v-if="plans.length===0 && selectedId" class="empty-state" style="background:var(--c-glass);border-radius:var(--radius-md);padding:60px;">
            <div class="empty-icon">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><path d="M14 2v6h6"/><path d="M12 18v-6M9 15h6"/></svg>
            </div>
            <p>暂无训练计划</p>
          </div>

          <div v-for="p in plans" :key="p.id" class="plan-card glass">
            <div class="plan-card-top">
              <div>
                <h3>{{ p.planName }}</h3>
                <p class="plan-card-meta">
                  {{ p.startDate }} → {{ p.endDate }}
                  <span v-if="p.description"> · {{ p.description }}</span>
                </p>
              </div>
              <span :class="['badge', statusBadge(p.status||'DRAFT')]">{{ statusMap[p.status||'DRAFT'] }}</span>
            </div>
            <div class="plan-card-actions">
              <button class="btn btn-ghost btn-sm" @click="viewDetail(p.id)">查看详情</button>
              <button class="btn btn-ghost btn-sm" @click="editPlan(p.id)">编辑</button>
              <button class="btn btn-danger btn-sm" @click="deletePlan(p.id)">删除</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 详情弹窗 -->
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
          <p class="plan-meta" v-if="detailPlan.description">描述：{{ detailPlan.description }}</p>

          <div class="lane-divider"></div>

          <div v-for="d in detailDetails" :key="d.id" class="day-card">
            <div class="day-header">
              <span class="day-num">第 {{ d.dayNumber }} 天</span>
              <span v-if="d.trainingDate" class="day-date">{{ d.trainingDate }}</span>
              <button class="btn-edit-day" @click="editDayDetail(d.id)">编辑</button>
            </div>
            <div class="day-info">
              <span class="day-intensity">{{ d.intensity || '未设置强度' }}</span>
              <span :class="['badge', d.isChecked ? 'badge-success' : 'badge-neutral']" @click="toggleCheck(d.id, d.isChecked)" style="cursor: pointer;">
                {{ d.isChecked ? '✓ 已打卡' : '○ 未打卡' }}
              </span>
            </div>
            <p v-if="d.notes" class="day-notes">{{ d.notes }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加学员弹窗 -->
    <div v-if="showAddModal" class="modal-overlay" @click.self="showAddModal=false">
      <div class="modal-content">
        <div class="modal-header">
          <h2>添加学员</h2>
          <button class="btn-back" @click="showAddModal=false">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><path d="M18 6L6 18M6 6l12 12"/></svg>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">选择学员</label>
            <select v-model="newStudentId" class="form-input">
              <option value="">-- 请选择 --</option>
              <option v-for="s in allStudents" :key="s.id" :value="s.id">{{ s.username }}</option>
            </select>
          </div>
          <button class="btn btn-primary btn-block" @click="addStudent">确认添加</button>
        </div>
      </div>
    </div>

    <!-- 创建/编辑计划弹窗 -->
    <div v-if="showCreateModal" class="modal-overlay" @click.self="showCreateModal=false">
      <div class="modal-content" style="max-width: 800px;">
        <div class="modal-header">
          <h2>{{ editMode ? '编辑训练计划' : '创建训练计划' }}</h2>
          <button class="btn-back" @click="showCreateModal=false">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><path d="M18 6L6 18M6 6l12 12"/></svg>
          </button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="submitCreatePlan">
            <div class="form-group">
              <label class="form-label">计划名称</label>
              <input v-model="createForm.planName" type="text" class="form-input" placeholder="例如：游泳基础训练" required />
            </div>
            <div class="form-group">
              <label class="form-label">计划描述</label>
              <textarea v-model="createForm.description" class="form-input" rows="2" placeholder="可选"></textarea>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label class="form-label">开始日期</label>
                <input v-model="createForm.startDate" type="date" class="form-input" required />
              </div>
              <div class="form-group">
                <label class="form-label">计划天数（5-14天）</label>
                <input v-model.number="createForm.days" type="number" class="form-input" min="5" max="14" required @input="generateDays" />
              </div>
            </div>
            <div class="form-group">
              <label class="form-label">计划状态</label>
              <select v-model="createForm.status" class="form-input">
                <option value="DRAFT">草稿</option>
                <option value="ACTIVE">进行中</option>
                <option value="COMPLETED">已完成</option>
              </select>
            </div>

            <div class="lane-divider"></div>

            <div class="day-details">
              <h4 style="margin-bottom: 16px; font-size: 16px;">每天训练内容</h4>
              <div v-for="day in formDays" :key="day.dayNumber" class="day-form-card">
                <div class="day-form-header">
                  <span class="day-form-title">第 {{ day.dayNumber }} 天 - {{ day.date }}</span>
                </div>
                <div class="day-form-body">
                  <div class="form-row">
                    <div class="form-group">
                      <label class="form-label">训练项目</label>
                      <input v-model="day.trainingItem" type="text" class="form-input" placeholder="自由泳、蛙泳等" />
                    </div>
                    <div class="form-group">
                      <label class="form-label">强度等级</label>
                      <select v-model="day.intensity" class="form-input">
                        <option value="">请选择</option>
                        <option value="低">低</option>
                        <option value="中等">中等</option>
                        <option value="高">高</option>
                      </select>
                    </div>
                  </div>
                  <div class="form-row">
                    <div class="form-group">
                      <label class="form-label">时长（分钟）</label>
                      <input v-model.number="day.duration" type="number" class="form-input" min="0" placeholder="60" />
                    </div>
                    <div class="form-group">
                      <label class="form-label">距离（米）</label>
                      <input v-model.number="day.distance" type="number" class="form-input" min="0" placeholder="1000" />
                    </div>
                  </div>
                  <div class="form-group">
                    <label class="form-label">计划内容</label>
                    <textarea v-model="day.notes" class="form-input" rows="2" placeholder="今天的训练内容..."></textarea>
                  </div>
                </div>
              </div>
            </div>

            <button type="submit" class="btn btn-primary btn-block" style="margin-top: 20px;">{{ editMode ? '保存修改' : '创建计划' }}</button>
          </form>
        </div>
      </div>
    </div>

    <!-- 编辑单天详情弹窗 -->
    <div v-if="editingDay" class="modal-overlay" @click.self="editingDay=null">
      <div class="modal-content">
        <div class="modal-header">
          <h2>编辑训练内容</h2>
          <button class="btn-back" @click="editingDay=null">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><path d="M18 6L6 18M6 6l12 12"/></svg>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">强度等级</label>
            <select v-model="editingDay.intensity" class="form-input">
              <option value="低">低</option>
              <option value="中等">中等</option>
              <option value="高">高</option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-label">计划内容</label>
            <textarea v-model="editingDay.notes" class="form-input" rows="4"></textarea>
          </div>
          <div class="form-group">
            <label class="form-label">打卡状态</label>
            <select v-model="editingDay.isChecked" class="form-input">
              <option :value="false">未打卡</option>
              <option :value="true">已打卡</option>
            </select>
          </div>
          <button class="btn btn-primary btn-block" @click="saveDayDetail">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getCoachStudents, getAllStudents, addStudentRelation, getStudentPlans, getPlanDetail, deletePlan as delPlan, createPlan, updatePlan, getDayDetail, updateDayDetail, toggleCheckStatus } from '../api/index'

const students = ref<any[]>([])
const allStudents = ref<any[]>([])
const plans = ref<any[]>([])
const selectedId = ref<number|null>(null)
const selectedName = ref('')
const detailPlan = ref<any>(null)
const detailDetails = ref<any[]>([])
const showAddModal = ref(false)
const showCreateModal = ref(false)
const newStudentId = ref('')
const editMode = ref(false)
const editingDay = ref<any>(null)
const statusMap: Record<string,string> = { DRAFT: '草稿', ACTIVE: '进行中', COMPLETED: '已完成' }

// 创建计划表单
const createForm = ref<{
  id?: number
  planName: string
  description: string
  startDate: string
  days: number
  status: string
}>({
  planName: '',
  description: '',
  startDate: new Date().toISOString().split('T')[0],
  days: 5,
  status: 'DRAFT'
})
const formDays = ref<any[]>([])

onMounted(() => { loadStudents(); loadAllStudents() })

async function loadStudents() { const r=await getCoachStudents(); if(r.code===200) students.value=r.data }
async function loadAllStudents() { const r=await getAllStudents(); if(r.code===200) allStudents.value=r.data }
async function selectStudent(s: any) { selectedId.value=s.studentId; selectedName.value=s.studentRealName||s.studentUsername; await loadPlans() }
async function loadPlans() { if(!selectedId.value) return; const r=await getStudentPlans(selectedId.value); if(r.code===200) plans.value=r.data }
async function viewDetail(id: number) { const r=await getPlanDetail(id); if(r.code===200) { detailPlan.value=r.data.plan; detailDetails.value=r.data.details } }
async function editPlan(id: number) {
  const r = await getPlanDetail(id)
  if (r.code === 200) {
    const plan = r.data.plan
    editMode.value = true
    createForm.value = {
      id: plan.id,
      planName: plan.planName || '',
      description: plan.description || '',
      startDate: plan.startDate || '',
      days: Math.ceil((new Date(plan.endDate).getTime() - new Date(plan.startDate).getTime()) / (1000 * 60 * 60 * 24)) + 1,
      status: plan.status || 'DRAFT'
    }
    generateDays()
    showCreateModal.value = true
  }
}
async function deletePlan(id: number) { if(!confirm('确定删除？')) return; await delPlan(id); await loadPlans() }
async function editDayDetail(detailId: number) {
  const r = await getDayDetail(detailId)
  if (r.code === 200) {
    editingDay.value = { ...r.data }
  }
}
async function saveDayDetail() {
  if (!editingDay.value) return
  const r = await updateDayDetail({
    id: editingDay.value.id,
    intensity: editingDay.value.intensity,
    notes: editingDay.value.notes,
    isChecked: editingDay.value.isChecked
  })
  if (r.code === 200) {
    editingDay.value = null
    // 重新加载详情
    if (detailPlan.value) {
      await viewDetail(detailPlan.value.id)
    }
  }
}
async function toggleCheck(detailId: number, currentStatus: boolean) {
  const r = await toggleCheckStatus(detailId, !currentStatus)
  if (r.code === 200) {
    // 重新加载详情
    if (detailPlan.value) {
      await viewDetail(detailPlan.value.id)
    }
  }
}
function openCreate() { 
  if(!selectedId.value) return
  editMode.value = false
  createForm.value = {
    planName: '',
    description: '',
    startDate: new Date().toISOString().split('T')[0],
    days: 5,
    status: 'DRAFT'
  }
  generateDays()
  showCreateModal.value = true
}
async function addStudent() { if(!newStudentId.value) return; await addStudentRelation(Number(newStudentId.value)); showAddModal.value=false; await loadStudents() }
function statusBadge(s: string) { return s==='ACTIVE' ? 'badge-success' : s==='COMPLETED' ? 'badge-info' : 'badge-neutral' }

function generateDays() {
  const days = createForm.value.days
  const startDate = new Date(createForm.value.startDate)
  formDays.value = []
  for(let i = 1; i <= days; i++) {
    const date = new Date(startDate)
    date.setDate(date.getDate() + (i - 1))
    formDays.value.push({
      dayNumber: i,
      date: date.toISOString().split('T')[0],
      trainingItem: '',
      intensity: '',
      duration: 60,
      distance: 1000,
      notes: ''
    })
  }
}

async function submitCreatePlan() {
  if(!selectedId.value) return
  if(createForm.value.days < 5 || createForm.value.days > 14) {
    alert('计划周期必须在5-14天之间')
    return
  }
  
  const requestData: any = {
    studentId: selectedId.value,
    planName: createForm.value.planName,
    description: createForm.value.description,
    startDate: createForm.value.startDate,
    days: createForm.value.days,
    status: createForm.value.status,
    dayDetails: formDays.value.map(day => ({
      dayNumber: day.dayNumber,
      trainingDate: day.date,
      trainingItem: day.trainingItem,
      duration: day.duration,
      distance: day.distance,
      intensity: day.intensity,
      notes: day.notes
    }))
  }
  
  let r
  if (editMode.value && createForm.value.id) {
    // 编辑模式：调用更新接口（原HTML的逻辑）
    r = await updatePlan({
      id: createForm.value.id,
      planName: createForm.value.planName,
      description: createForm.value.description,
      startDate: createForm.value.startDate,
      days: createForm.value.days,
      status: createForm.value.status
    })
  } else {
    // 创建模式
    r = await createPlan(requestData)
  }
  
  if(r.code === 200) {
    showCreateModal.value = false
    await loadPlans()
    alert(editMode.value ? '保存成功' : '创建成功')
    editMode.value = false
  } else {
    alert(r.message || '操作失败')
  }
}
</script>

<style scoped>
.plan-layout {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 24px;
  align-items: start;
}

@media (max-width: 768px) {
  .plan-layout { grid-template-columns: 1fr; }
}

.plan-sidebar {
  padding: 20px;
  position: sticky;
  top: 88px;
}

.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.sidebar-header h3 {
  font-family: var(--font-display);
  font-size: 15px;
  font-weight: 600;
}

.student-list { display: flex; flex-direction: column; gap: 6px; }

.student-chip {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: all 0.2s;
}

.student-chip:hover { background: var(--c-surface); }
.student-chip.active { background: var(--c-accent-dim); border: 1px solid rgba(34, 211, 238, 0.2); }

.chip-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--c-accent), #0EA5E9);
  color: var(--c-deep-1);
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: var(--font-display);
  font-size: 14px;
  font-weight: 700;
  flex-shrink: 0;
}

.chip-name { font-size: 14px; font-weight: 500; }
.chip-sub { font-size: 12px; color: var(--c-text-muted); }

.plan-main { min-height: 200px; }

.plan-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}

.plan-header h2 {
  font-family: var(--font-display);
  font-size: 22px;
  font-weight: 700;
}

.plan-sub { font-size: 13px; color: var(--c-text-secondary); margin-top: 2px; }

.plan-card {
  padding: 20px;
  margin-bottom: 12px;
  cursor: pointer;
}

.plan-card:hover { border-color: rgba(34, 211, 238, 0.3); }

.plan-card-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.plan-card-top h3 {
  font-family: var(--font-display);
  font-size: 17px;
  font-weight: 600;
  margin-bottom: 4px;
}

.plan-card-meta {
  font-size: 13px;
  color: var(--c-text-secondary);
}

.plan-card-actions {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}

.plan-meta { font-size: 14px; color: var(--c-text-secondary); margin-bottom: 8px; }

.day-card {
  padding: 14px 16px;
  margin-bottom: 10px;
  background: var(--c-surface);
  border-radius: var(--radius-sm);
  border-left: 3px solid var(--c-accent);
}

.day-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.day-num {
  font-family: var(--font-display);
  font-size: 15px;
  font-weight: 600;
}

.day-date { font-size: 12px; color: var(--c-text-muted); }

.day-info {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
}

.day-intensity { font-size: 13px; color: var(--c-text-secondary); }
.day-notes { font-size: 13px; color: var(--c-text); }

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

@media (max-width: 768px) {
  .form-row { grid-template-columns: 1fr; }
}

.day-details {
  margin-top: 24px;
}

.day-form-card {
  background: var(--c-surface);
  border-radius: var(--radius-sm);
  margin-bottom: 16px;
  overflow: hidden;
}

.day-form-header {
  background: linear-gradient(135deg, var(--c-accent), #0EA5E9);
  padding: 12px 16px;
  color: var(--c-deep-1);
}

.day-form-title {
  font-family: var(--font-display);
  font-size: 14px;
  font-weight: 600;
}

.day-form-body {
  padding: 16px;
}

.btn-edit-day {
  margin-left: auto;
  padding: 4px 12px;
  background: var(--c-accent);
  color: var(--c-deep-1);
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-edit-day:hover {
  opacity: 0.9;
}
</style>
