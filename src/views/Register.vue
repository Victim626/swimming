<template>
  <div class="register-page">
    <div class="register-card glass">
      <div class="register-brand">
        <div class="register-icon">
          <svg width="28" height="28" viewBox="0 0 40 40" fill="none">
            <path d="M10 30 Q20 20 30 30 Q35 25 40 30" stroke="currentColor" stroke-width="2" fill="none"/>
            <path d="M5 35 Q15 25 25 35 Q30 30 35 35" stroke="currentColor" stroke-width="1.5" fill="none" opacity="0.5"/>
            <circle cx="20" cy="12" r="6" stroke="currentColor" stroke-width="2" fill="none"/>
          </svg>
        </div>
        <div>
          <h1>创建账号</h1>
          <p class="brand-sub">加入泳道训练平台</p>
        </div>
      </div>

      <div class="role-selector">
        <button type="button" :class="['role-btn', { active: role === 'COACH' }]" @click="role='COACH'">
          <span class="role-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"><circle cx="12" cy="5" r="3"/><path d="M5 20 Q12 12 19 20"/><path d="M3 22 Q12 16 21 22" opacity="0.4"/></svg>
          </span>
          <span class="role-label">教练注册</span>
        </button>
        <button type="button" :class="['role-btn', { active: role === 'STUDENT' }]" @click="role='STUDENT'">
          <span class="role-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"><circle cx="12" cy="8" r="4"/><path d="M4 20c0-4 3.6-8 8-8s8 4 8 8"/></svg>
          </span>
          <span class="role-label">学员注册</span>
        </button>
      </div>

      <form @submit.prevent="handleRegister">
        <div class="form-row">
          <div class="form-group">
            <label class="form-label">用户名 <span class="req">*</span></label>
            <input v-model="form.username" class="form-input" required placeholder="请输入用户名" />
          </div>
          <div class="form-group">
            <label class="form-label">真实姓名 <span class="req">*</span></label>
            <input
              :value="role==='COACH' ? form.coachRealName : form.studentRealName"
              @input="role==='COACH' ? (form.coachRealName=($event.target as HTMLInputElement).value) : (form.studentRealName=($event.target as HTMLInputElement).value)"
              class="form-input"
              required
              placeholder="请输入真实姓名"
            />
          </div>
        </div>
        <div class="form-row">
          <div class="form-group">
            <label class="form-label">密码 <span class="req">*</span></label>
            <input v-model="form.password" type="password" class="form-input" required placeholder="至少6位" minlength="6" />
          </div>
          <div class="form-group">
            <label class="form-label">确认密码 <span class="req">*</span></label>
            <input v-model="confirmPwd" type="password" class="form-input" required placeholder="再次输入" />
          </div>
        </div>
        <div class="form-row">
          <div class="form-group">
            <label class="form-label">手机号</label>
            <input :value="role==='COACH' ? form.coachPhone : form.studentPhone" @input="role==='COACH' ? (form.coachPhone=($event.target as HTMLInputElement).value) : (form.studentPhone=($event.target as HTMLInputElement).value)" class="form-input" placeholder="手机号" />
          </div>
          <div class="form-group">
            <label class="form-label">邮箱</label>
            <input :value="role==='COACH' ? form.coachEmail : form.studentEmail" @input="role==='COACH' ? (form.coachEmail=($event.target as HTMLInputElement).value) : (form.studentEmail=($event.target as HTMLInputElement).value)" type="email" class="form-input" placeholder="邮箱" />
          </div>
        </div>
        <div v-if="role==='STUDENT'" class="form-row">
          <div class="form-group">
            <label class="form-label">性别</label>
            <select v-model="form.gender" class="form-input">
              <option value="">请选择</option>
              <option value="MALE">男</option>
              <option value="FEMALE">女</option>
              <option value="OTHER">其他</option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-label">年龄</label>
            <input v-model.number="form.age" type="number" class="form-input" placeholder="岁" />
          </div>
        </div>

        <button type="submit" class="btn btn-primary btn-block btn-lg" :disabled="loading">
          {{ loading ? '注册中...' : '创建账号' }}
        </button>

        <div v-if="error" class="msg msg-error">{{ error }}</div>
        <div v-if="success" class="msg msg-success">{{ success }}</div>
      </form>

      <div class="register-footer">
        已有账号？<router-link to="/login" class="login-link">返回登录</router-link>
      </div>

      <div class="register-lane"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { registerApi } from '../api'

const router = useRouter()
const role = ref('COACH')
const confirmPwd = ref('')
const error = ref('')
const success = ref('')
const loading = ref(false)
const form = reactive({
  username: '', password: '', role: 'COACH',
  coachRealName: '', coachPhone: '', coachEmail: '',
  studentRealName: '', studentPhone: '', studentEmail: '', gender: '', age: ''
})

async function handleRegister() {
  error.value = ''; success.value = ''
  if (!form.username || !form.password) { error.value = '请填写必填字段'; return }
  const name = role.value === 'COACH' ? form.coachRealName : form.studentRealName
  if (!name) { error.value = '请填写真实姓名'; return }
  if (form.password.length < 6) { error.value = '密码至少6位'; return }
  if (form.password !== confirmPwd.value) { error.value = '两次密码不一致'; return }
  loading.value = true
  form.role = role.value
  const res = await registerApi({ ...form })
  if (res.code === 200) {
    success.value = '注册成功！正在跳转...'
    setTimeout(() => router.push('/login'), 1500)
  } else {
    error.value = res.message
  }
  loading.value = false
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  padding: 20px;
  background: transparent;
}

.register-page::before {
  content: '';
  position: fixed;
  inset: 0;
  background:
    radial-gradient(ellipse at 20% 30%, rgba(34, 211, 238, 0.06) 0%, transparent 50%),
    radial-gradient(ellipse at 80% 70%, rgba(251, 191, 36, 0.04) 0%, transparent 50%);
  pointer-events: none;
}

.register-card {
  position: relative;
  width: 100%;
  max-width: 520px;
  background: var(--c-glass);
  backdrop-filter: blur(16px);
  border: 1px solid var(--c-glass-border);
  border-radius: var(--radius-lg);
  padding: 40px 36px 32px;
  box-shadow: var(--shadow-glass-lg);
  animation: cardEnter 0.5s ease;
  overflow: hidden;
}

.register-card::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(34, 211, 238, 0.3), transparent);
}

@keyframes cardEnter {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.register-brand {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 28px;
}

.register-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, var(--c-accent), #0EA5E9);
  color: var(--c-deep-1);
  border-radius: 14px;
  flex-shrink: 0;
  box-shadow: 0 0 16px var(--c-accent-glow);
}

.register-brand h1 {
  font-family: var(--font-display);
  font-size: 22px;
  font-weight: 700;
  color: var(--c-text);
}

.brand-sub {
  font-family: var(--font-body);
  font-size: 13px;
  color: var(--c-text-secondary);
  margin-top: 2px;
}

.role-selector {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  margin-bottom: 24px;
}

.role-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 14px;
  border: 1.5px solid var(--c-glass-border);
  background: var(--c-surface);
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: all 0.2s;
  font-family: var(--font-body);
}

.role-btn:hover { border-color: var(--c-glass-border-hover); background: var(--c-surface-hover); }

.role-btn.active {
  border-color: var(--c-accent);
  background: var(--c-accent-dim);
  box-shadow: 0 0 12px var(--c-accent-glow);
}

.role-icon { font-size: 20px; }
.role-label { font-size: 14px; font-weight: 600; color: var(--c-text); }

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.form-row .form-group { margin-bottom: 16px; }

.req { color: var(--c-danger); }

.msg {
  margin-top: 12px;
  padding: 10px 14px;
  font-size: 13px;
  font-weight: 500;
  border-radius: var(--radius-sm);
  text-align: center;
  border: 1px solid transparent;
}

.msg-error { background: var(--c-danger-bg); color: var(--c-danger); border-color: rgba(251, 113, 133, 0.2); }
.msg-success { background: var(--c-success-bg); color: var(--c-success); border-color: rgba(52, 211, 153, 0.2); }

.register-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: var(--c-text-muted);
}

.login-link {
  color: var(--c-accent);
  font-weight: 600;
}

.register-lane {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: repeating-linear-gradient(
    to right,
    rgba(255, 255, 255, 0.08) 0,
    rgba(255, 255, 255, 0.08) 8px,
    transparent 8px,
    transparent 16px
  );
}

@media (max-width: 500px) {
  .form-row { grid-template-columns: 1fr; }
  .register-card { padding: 28px 20px 24px; }
}
</style>
