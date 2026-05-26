<template>
  <div class="login-page">
    <div class="login-bg">
      <div class="login-ripple" v-for="r in ripples" :key="r.id" :style="r.style"></div>
    </div>

    <div class="login-card glass">
      <div class="login-brand">
        <div class="login-icon">
          <svg width="36" height="36" viewBox="0 0 40 40" fill="none">
            <path d="M8 30 Q18 18 28 30 Q33 24 38 30" stroke="currentColor" stroke-width="2" fill="none"/>
            <path d="M4 35 Q14 24 24 35 Q29 29 34 35" stroke="currentColor" stroke-width="1.5" fill="none" opacity="0.4"/>
            <circle cx="20" cy="10" r="6" stroke="currentColor" stroke-width="2" fill="none"/>
            <path d="M14 10 L8 16 M20 4 L20 18 M26 10 L32 16" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
          </svg>
        </div>
        <h1 class="brand-title">泳道</h1>
        <p class="brand-sub">游泳训练管理平台</p>
        <div class="brand-line"></div>
      </div>

      <form class="login-form" @submit.prevent="handleLogin">
        <div class="input-group">
          <div class="input-icon">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round">
              <circle cx="12" cy="8" r="4"/>
              <path d="M4 22c0-4 3.6-8 8-8s8 4 8 8"/>
            </svg>
          </div>
          <input
            v-model="username"
            type="text"
            class="login-input"
            placeholder="用户名"
            required
          />
          <div class="input-glow"></div>
        </div>

        <div class="input-group">
          <div class="input-icon">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round">
              <rect x="3" y="11" width="18" height="11" rx="2"/>
              <path d="M7 11V7a5 5 0 0110 0v4"/>
            </svg>
          </div>
          <input
            v-model="password"
            type="password"
            class="login-input"
            placeholder="密码"
            required
          />
          <div class="input-glow"></div>
        </div>

        <button type="submit" class="btn-submit" :disabled="loading">
          <span class="btn-text">{{ loading ? '进入泳池...' : '进入泳池' }}</span>
          <span class="btn-arrow">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round">
              <path d="M5 12h14M13 5l7 7-7 7"/>
            </svg>
          </span>
          <div class="btn-shimmer"></div>
        </button>

        <div v-if="error" class="login-error">{{ error }}</div>
      </form>

      <div class="login-footer">
        <span class="footer-text">还没有账号？</span>
        <router-link to="/register" class="login-link">创建账号</router-link>
      </div>

      <div class="login-lane">
        <span class="lane-float"></span>
        <span class="lane-float" style="animation-delay: 2.5s;"></span>
        <span class="lane-float" style="animation-delay: 5s;"></span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { loginApi } from '../api'

const router = useRouter()
const store = useUserStore()
const username = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

const ripples = Array.from({ length: 5 }, (_, i) => ({
  id: i,
  style: {
    left: `${15 + i * 18}%`,
    animationDelay: `${i * 4}s`,
    animationDuration: `${8 + i * 2}s`,
    width: `${120 + i * 40}px`,
    height: `${120 + i * 40}px`
  }
}))

async function handleLogin() {
  if (!username.value || !password.value) { error.value = '请填写完整信息'; return }
  loading.value = true; error.value = ''
  const res = await loginApi(username.value, password.value)
  if (res.code === 200) {
    store.setUser(res.data)
    router.push('/dashboard')
  } else {
    error.value = res.message
  }
  loading.value = false
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  padding: 20px;
}

.login-bg {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(ellipse at 20% 30%, rgba(34, 211, 238, 0.06) 0%, transparent 50%),
    radial-gradient(ellipse at 80% 70%, rgba(251, 191, 36, 0.04) 0%, transparent 50%),
    radial-gradient(ellipse at 50% 0%, rgba(56, 189, 248, 0.06) 0%, transparent 40%);
  pointer-events: none;
}

.login-ripple {
  position: absolute;
  bottom: -10%;
  border-radius: 50%;
  border: 1px solid rgba(34, 211, 238, 0.08);
  animation: rippleExpand 12s ease-out infinite;
  pointer-events: none;
}

@keyframes rippleExpand {
  0% { transform: scale(0.2); opacity: 0; }
  10% { opacity: 0.5; }
  60% { opacity: 0.15; }
  100% { transform: scale(2.5); opacity: 0; }
}

.login-card {
  position: relative;
  width: 100%;
  max-width: 400px;
  padding: 44px 36px 36px;
  overflow: hidden;
  z-index: 1;
}

.login-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(34, 211, 238, 0.3), transparent);
}

.login-brand {
  text-align: center;
  margin-bottom: 36px;
}

.login-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, var(--c-accent), #0EA5E9);
  color: var(--c-deep-1);
  border-radius: 18px;
  margin-bottom: 16px;
  box-shadow: 0 0 20px var(--c-accent-glow);
}

.brand-title {
  font-family: var(--font-display);
  font-size: 32px;
  font-weight: 800;
  color: var(--c-text);
  letter-spacing: 1px;
}

.brand-sub {
  font-family: var(--font-body);
  font-size: 14px;
  color: var(--c-text-secondary);
  margin-top: 4px;
}

.brand-line {
  width: 40px;
  height: 2px;
  background: linear-gradient(90deg, var(--c-accent), transparent);
  margin: 16px auto 0;
  border-radius: 1px;
}

.login-form {
  margin-bottom: 24px;
}

.input-group {
  position: relative;
  margin-bottom: 18px;
}

.input-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--c-text-muted);
  z-index: 2;
  pointer-events: none;
  transition: color 0.3s;
}

.input-group:focus-within .input-icon {
  color: var(--c-accent);
}

.login-input {
  width: 100%;
  padding: 14px 14px 14px 44px;
  font-family: var(--font-body);
  font-size: 15px;
  color: var(--c-text);
  background: rgba(255, 255, 255, 0.04);
  border: 1.5px solid var(--c-glass-border);
  border-radius: var(--radius-sm);
  transition: all 0.3s;
  outline: none;
  position: relative;
}

.login-input:focus {
  border-color: var(--c-accent);
  background: rgba(34, 211, 238, 0.04);
  box-shadow: 0 0 0 3px var(--c-accent-dim);
}

.login-input::placeholder {
  color: var(--c-text-dim);
  font-weight: 300;
}

.input-glow {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--c-accent), transparent);
  transition: width 0.4s;
  opacity: 0;
}

.login-input:focus ~ .input-glow {
  width: 60%;
  opacity: 1;
}

.btn-submit {
  position: relative;
  width: 100%;
  padding: 14px 20px;
  font-family: var(--font-body);
  font-size: 15px;
  font-weight: 600;
  color: var(--c-deep-1);
  background: linear-gradient(135deg, var(--c-accent), #0EA5E9);
  border: none;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  overflow: hidden;
  box-shadow: 0 2px 16px var(--c-accent-glow);
}

.btn-submit:hover:not(:disabled) {
  box-shadow: 0 4px 24px var(--c-accent-glow);
  transform: translateY(-1px);
}

.btn-submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-arrow {
  display: inline-flex;
  transition: transform 0.3s;
}

.btn-submit:hover:not(:disabled) .btn-arrow {
  transform: translateX(4px);
}

.btn-shimmer {
  position: absolute;
  inset: 0;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.15), transparent);
  transform: translateX(-100%);
  transition: none;
}

.btn-submit:hover:not(:disabled) .btn-shimmer {
  transform: translateX(100%);
  transition: transform 0.8s;
}

.login-error {
  margin-top: 12px;
  padding: 10px 14px;
  background: var(--c-danger-bg);
  color: var(--c-danger);
  font-size: 13px;
  font-weight: 500;
  border-radius: var(--radius-sm);
  text-align: center;
  border: 1px solid rgba(251, 113, 133, 0.2);
}

.login-footer {
  text-align: center;
  font-size: 14px;
  color: var(--c-text-muted);
}

.login-link {
  font-weight: 600;
}

.login-link:hover {
  text-decoration: underline;
}

/* 泳道漂浮装饰 */
.login-lane {
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

.lane-float {
  position: absolute;
  width: 8px;
  height: 8px;
  background: var(--c-accent);
  border-radius: 50%;
  top: -2.5px;
  animation: floatLane 6s linear infinite;
  opacity: 0.5;
  box-shadow: 0 0 8px var(--c-accent-glow);
}

@keyframes floatLane {
  0% { left: -10px; }
  100% { left: 110%; }
}
</style>
