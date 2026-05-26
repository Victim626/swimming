<template>
  <div class="page">
    <nav class="glass-nav">
      <div class="glass-nav-brand">
        <span class="brand-icon">
          <svg width="18" height="18" viewBox="0 0 40 40" fill="none">
            <path d="M10 30 Q20 20 30 30 Q35 25 40 30" stroke="currentColor" stroke-width="3" fill="none"/>
            <circle cx="20" cy="12" r="6" stroke="currentColor" stroke-width="3" fill="none"/>
          </svg>
        </span>
        泳道
      </div>
      <div class="nav-actions">
        <div class="nav-user">
          <span class="nav-user-name">{{ store.user?.realName || store.user?.username }}</span>
          <span :class="['badge', store.isCoach ? 'badge-primary' : 'badge-success']">
            {{ store.isCoach ? '教练' : '学员' }}
          </span>
        </div>
        <button class="btn btn-ghost btn-sm" @click="$router.push('/profile')">个人资料</button>
        <button class="btn btn-ghost btn-sm" @click="handleLogout">退出</button>
      </div>
    </nav>

    <div class="page-container">
      <!-- 欢迎区域 -->
      <div class="welcome-section">
        <div class="welcome-content">
          <div class="welcome-badge">DASHBOARD</div>
          <h2 class="welcome-title">欢迎回来，{{ store.user?.realName }}</h2>
          <p class="welcome-desc">准备好今天的训练了吗？</p>
        </div>
        <div class="welcome-visual">
          <div class="welcome-wave"></div>
          <div class="welcome-wave" style="animation-delay: 0.8s; opacity: 0.4;"></div>
          <div class="welcome-wave" style="animation-delay: 1.6s; opacity: 0.2;"></div>
        </div>
        <div class="welcome-glow"></div>
      </div>

      <!-- 统计 -->
      <div class="stats-grid">
        <div v-if="store.isStudent" class="stat-card">
          <div class="stat-number">{{ stats.totalPlans }}</div>
          <div class="stat-label">训练计划</div>
        </div>
        <div v-if="store.isStudent" class="stat-card">
          <div class="stat-number">{{ stats.todayTasks }}</div>
          <div class="stat-label">今日任务</div>
        </div>
        <div v-if="store.isStudent" class="stat-card">
          <div class="stat-number">{{ stats.completedTasks }}</div>
          <div class="stat-label">已完成</div>
        </div>
        <div v-if="store.isStudent" class="stat-card">
          <div class="stat-number">{{ stats.pendingAppts }}</div>
          <div class="stat-label">待确认预约</div>
        </div>

        <div v-if="store.isCoach" class="stat-card">
          <div class="stat-number">{{ stats.studentCount }}</div>
          <div class="stat-label">学员数量</div>
        </div>
        <div v-if="store.isCoach" class="stat-card">
          <div class="stat-number">{{ stats.planCount }}</div>
          <div class="stat-label">训练计划</div>
        </div>
        <div v-if="store.isCoach" class="stat-card">
          <div class="stat-number">{{ stats.todayAppts }}</div>
          <div class="stat-label">今日预约</div>
        </div>
        <div v-if="store.isCoach" class="stat-card">
          <div class="stat-number">{{ stats.pendingAppts }}</div>
          <div class="stat-label">待确认</div>
        </div>
      </div>

      <!-- 功能入口 -->
      <div class="section-heading">
        <span class="section-tag">QUICK ACCESS</span>
        <h3 class="section-title">快速入口</h3>
        <div class="section-line"></div>
      </div>

      <div class="feature-grid">
        <div v-if="store.isStudent" class="feature-card" @click="$router.push('/student-training')">
          <div class="feature-icon-wrap">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round">
              <circle cx="12" cy="5" r="3"/>
              <path d="M5 20 Q12 12 19 20"/>
              <path d="M3 22 Q12 16 21 22" opacity="0.4"/>
            </svg>
          </div>
          <h3>我的训练</h3>
          <p>查看今日训练任务，完成打卡</p>
        </div>
        <div v-if="store.isCoach" class="feature-card" @click="$router.push('/training-plan')">
          <div class="feature-icon-wrap">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round">
              <rect x="3" y="4" width="18" height="16" rx="2"/>
              <path d="M8 10h8M8 14h6"/>
            </svg>
          </div>
          <h3>训练计划</h3>
          <p>为学员制定个性化训练计划</p>
        </div>
        <div v-if="store.isStudent" class="feature-card" @click="$router.push('/appointment')">
          <div class="feature-icon-wrap">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round">
              <rect x="3" y="4" width="18" height="18" rx="2"/>
              <path d="M16 2v4M8 2v4M3 10h18"/>
            </svg>
          </div>
          <h3>课程预约</h3>
          <p>预约教练训练时间</p>
        </div>
        <div v-if="store.isStudent" class="feature-card" @click="$router.push('/my-appointments')">
          <div class="feature-icon-wrap">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round">
              <path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2"/>
              <rect x="9" y="3" width="6" height="4" rx="1"/>
              <path d="M9 14l2 2 4-4"/>
            </svg>
          </div>
          <h3>我的预约</h3>
          <p>查看和管理我的所有预约</p>
        </div>
        <div v-if="store.isCoach" class="feature-card" @click="$router.push('/appointment-manage')">
          <div class="feature-icon-wrap">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round">
              <rect x="2" y="3" width="20" height="18" rx="2"/>
              <path d="M2 9h20"/>
              <path d="M12 13v4M10 15h4"/>
            </svg>
          </div>
          <h3>预约管理</h3>
          <p>管理学员预约，确认训练时间</p>
        </div>
        <div class="feature-card" @click="$router.push('/posts')">
          <div class="feature-icon-wrap">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round">
              <path d="M4 4h16v12H7l-3 3z"/>
              <path d="M8 8h8M8 12h5"/>
            </svg>
          </div>
          <h3>训练分享</h3>
          <p>发帖、点赞、关联打卡记录</p>
        </div>
        <div class="feature-card" @click="$router.push('/chat')">
          <div class="feature-icon-wrap">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round">
              <path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z"/>
            </svg>
          </div>
          <h3>即时沟通</h3>
          <p>教练学员实时沟通</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { logoutApi } from '../api'
import { getStudentMyPlans, getStudentTodayTasks, getMyAppointments, getCoachStudentsCount, getCoachMyPlans, getTodayAppointments } from '../api'

const router = useRouter()
const store = useUserStore()
const stats = ref({
  totalPlans: '-', todayTasks: '-', completedTasks: '-', pendingAppts: '-',
  studentCount: '-', planCount: '-', todayAppts: '-'
})

onMounted(() => { if (store.isStudent) loadStudentStats(); else loadCoachStats() })

async function loadStudentStats() {
  const [plans, tasks, appts] = await Promise.all([
    getStudentMyPlans(), getStudentTodayTasks(new Date().toISOString().split('T')[0]), getMyAppointments()
  ])
  if (plans.code === 200) stats.value.totalPlans = plans.data.length
  if (tasks.code === 200) {
    stats.value.todayTasks = tasks.data.length
    stats.value.completedTasks = tasks.data.filter((t: any) => t.isChecked).length
  }
  if (appts.code === 200) stats.value.pendingAppts = appts.data.filter((a: any) => a.status === 'PENDING').length
}
async function loadCoachStats() {
  const [students, plans, appts] = await Promise.all([
    getCoachStudentsCount(), getCoachMyPlans(), getTodayAppointments(new Date().toISOString().split('T')[0])
  ])
  if (students.code === 200) stats.value.studentCount = students.data.length
  if (plans.code === 200) stats.value.planCount = plans.data.length
  if (appts.code === 200) {
    stats.value.todayAppts = appts.data.length
    stats.value.pendingAppts = appts.data.filter((a: any) => a.status === 'PENDING').length
  }
}
async function handleLogout() {
  await logoutApi()
  store.clearUser()
  router.push('/login')
}
</script>

<style scoped>
.welcome-section {
  position: relative;
  background: linear-gradient(135deg, rgba(255,255,255,0.9) 0%, rgba(229,246,255,0.92) 52%, rgba(210,236,250,0.9) 100%);
  border: 1px solid var(--c-glass-border);
  border-radius: var(--radius-lg);
  padding: 40px 36px;
  margin-bottom: 28px;
  overflow: hidden;
}

.welcome-content {
  position: relative;
  z-index: 1;
}

.welcome-badge {
  display: inline-block;
  font-family: var(--font-display);
  font-size: 11px;
  font-weight: 600;
  color: var(--c-accent);
  letter-spacing: 2px;
  margin-bottom: 12px;
  padding: 4px 12px;
  border: 1px solid var(--c-accent-dim);
  border-radius: 100px;
  background: var(--c-accent-dim);
}

.welcome-title {
  font-family: var(--font-display);
  font-size: 28px;
  font-weight: 700;
  color: var(--c-text);
  margin-bottom: 6px;
  letter-spacing: -0.5px;
}

.welcome-desc {
  font-size: 15px;
  color: var(--c-text-secondary);
}

.welcome-visual {
  position: absolute;
  right: 20px;
  bottom: 0;
  display: flex;
  gap: 4px;
  align-items: flex-end;
  height: 100%;
  padding-bottom: 20px;
}

.welcome-wave {
  width: 50px;
  height: 60px;
  border: 2px solid rgba(34, 211, 238, 0.2);
  border-radius: 50%;
  animation: waveMove 4s ease-in-out infinite;
}

@keyframes waveMove {
  0%, 100% { transform: translateY(0) scaleX(1); }
  50% { transform: translateY(-10px) scaleX(1.15); }
}

.welcome-glow {
  position: absolute;
  top: -50%;
  right: -20%;
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(34, 211, 238, 0.04), transparent);
  pointer-events: none;
}

.section-heading {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.section-tag {
  font-family: var(--font-display);
  font-size: 11px;
  font-weight: 600;
  color: var(--c-accent);
  letter-spacing: 2px;
  padding: 3px 10px;
  border: 1px solid var(--c-accent-dim);
  border-radius: 4px;
  background: var(--c-accent-dim);
}

.section-title {
  font-family: var(--font-display);
  font-size: 18px;
  font-weight: 600;
  color: var(--c-text);
}

.section-line {
  flex: 1;
  height: 1px;
  background: linear-gradient(90deg, var(--c-glass-border), transparent);
}

.feature-icon-wrap {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--c-accent-dim);
  border-radius: var(--radius-sm);
  margin-bottom: var(--space-sm);
  color: var(--c-accent);
  transition: all 0.3s;
}

.feature-card:hover .feature-icon-wrap {
  background: rgba(34, 211, 238, 0.2);
  box-shadow: 0 0 16px var(--c-accent-glow);
}
</style>
