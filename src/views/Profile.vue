<template>
  <div class="page">
    <nav class="glass-nav">
      <div class="nav-actions" style="gap:12px;">
        <button class="btn-back" @click="$router.push('/dashboard')">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>
        </button>
        <div class="glass-nav-brand" style="gap:0;">个人资料</div>
      </div>
      <div></div>
    </nav>

    <div class="page-container">
      <div v-if="loading" class="empty-state"><p>加载中...</p></div>

      <template v-else>
        <!-- 头像和基本信息 -->
        <div class="profile-header">
          <div class="profile-header-glow"></div>
          <div class="avatar-section">
            <div class="avatar">{{ avatarText }}</div>
            <div class="profile-info">
              <h2 class="profile-name">{{ profileData.realName || store.user?.username }}</h2>
              <span :class="['badge', store.isCoach ? 'badge-primary' : 'badge-success']" style="margin-top:4px;">
                {{ store.isCoach ? '教练' : '学员' }}
              </span>
            </div>
          </div>
          <div class="profile-decoration">
            <span class="deco-ring"></span>
            <span class="deco-ring" style="width:160px;height:160px;animation-delay:1s;"></span>
          </div>
        </div>

        <!-- 账号信息 -->
        <div class="glass profile-section">
          <div class="section-header">
            <h3>账号信息</h3>
            <button v-if="!editingAccount" class="btn btn-ghost btn-sm" @click="editingAccount = true">编辑</button>
            <div v-else class="btn-group">
              <button class="btn btn-primary btn-sm" @click="saveAccount" :disabled="saving">保存</button>
              <button class="btn btn-ghost btn-sm" @click="cancelAccountEdit">取消</button>
            </div>
          </div>
          <div class="form-grid">
            <div class="form-field">
              <label>用户名</label>
              <input v-if="editingAccount" v-model="accountForm.username" class="form-input" />
              <span v-else class="field-value">{{ store.user?.username }}</span>
            </div>
            <div class="form-field">
              <label>密码</label>
              <input v-if="editingAccount" v-model="accountForm.password" type="password" class="form-input" placeholder="留空则不修改" />
              <span v-else class="field-value">••••••</span>
            </div>
          </div>
        </div>

        <!-- 详细资料 -->
        <div class="glass profile-section">
          <div class="section-header">
            <h3>{{ store.isCoach ? '教练资料' : '学员资料' }}</h3>
            <button v-if="!editingProfile" class="btn btn-ghost btn-sm" @click="editingProfile = true">编辑</button>
            <div v-else class="btn-group">
              <button class="btn btn-primary btn-sm" @click="saveProfile" :disabled="saving">保存</button>
              <button class="btn btn-ghost btn-sm" @click="cancelProfileEdit">取消</button>
            </div>
          </div>
          <div class="form-grid">
            <div class="form-field">
              <label>真实姓名</label>
              <input v-if="editingProfile" v-model="profileForm.realName" class="form-input" />
              <span v-else class="field-value">{{ profileData.realName || '-' }}</span>
            </div>
            <div class="form-field">
              <label>手机号</label>
              <input v-if="editingProfile" v-model="profileForm.phone" class="form-input" />
              <span v-else class="field-value">{{ profileData.phone || '-' }}</span>
            </div>
            <div class="form-field">
              <label>邮箱</label>
              <input v-if="editingProfile" v-model="profileForm.email" class="form-input" />
              <span v-else class="field-value">{{ profileData.email || '-' }}</span>
            </div>
            <template v-if="store.isCoach">
              <div class="form-field">
                <label>专长</label>
                <input v-if="editingProfile" v-model="profileForm.specialty" class="form-input" placeholder="如：自由泳、蝶泳" />
                <span v-else class="field-value">{{ profileData.specialty || '-' }}</span>
              </div>
              <div class="form-field full-width">
                <label>个人简介</label>
                <textarea v-if="editingProfile" v-model="profileForm.bio" class="form-input" rows="3"></textarea>
                <span v-else class="field-value">{{ profileData.bio || '-' }}</span>
              </div>
            </template>
            <template v-if="store.isStudent">
              <div class="form-field">
                <label>性别</label>
                <select v-if="editingProfile" v-model="profileForm.gender" class="form-input">
                  <option value="">请选择</option>
                  <option value="MALE">男</option>
                  <option value="FEMALE">女</option>
                </select>
                <span v-else class="field-value">{{ genderDisplay(profileData.gender) }}</span>
              </div>
              <div class="form-field">
                <label>年龄</label>
                <input v-if="editingProfile" v-model.number="profileForm.age" type="number" class="form-input" min="1" max="120" />
                <span v-else class="field-value">{{ profileData.age || '-' }}</span>
              </div>
            </template>
          </div>
        </div>
      </template>

      <div v-if="message" :class="['toast', messageType === 'error' ? 'toast-error' : 'toast-success']">
        {{ message }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '../stores/user'
import { getProfile, updateUser, updateCoachProfile, updateStudentProfile } from '../api'

const store = useUserStore()
const loading = ref(true)
const saving = ref(false)
const editingAccount = ref(false)
const editingProfile = ref(false)
const message = ref('')
const messageType = ref<'success' | 'error'>('success')

const profileData = ref<any>({})
const accountForm = ref({ username: '', password: '' })
const profileForm = ref<any>({})

const avatarText = computed(() => {
  const name = profileData.value.realName || store.user?.username || ''
  return name.charAt(0).toUpperCase()
})

onMounted(async () => { await loadProfile() })

async function loadProfile() {
  loading.value = true
  const res = await getProfile()
  if (res.code === 200) {
    profileData.value = res.data
    accountForm.value = { username: store.user?.username || '', password: '' }
    profileForm.value = { ...res.data }
  }
  loading.value = false
}

async function saveAccount() {
  saving.value = true
  const data: any = {}
  if (accountForm.value.username && accountForm.value.username !== store.user?.username) {
    data.username = accountForm.value.username
  }
  if (accountForm.value.password) {
    data.password = accountForm.value.password
  }
  if (!data.username && !data.password) {
    showMessage('没有修改内容', 'error')
    saving.value = false
    return
  }
  const res = await updateUser(data)
  if (res.code === 200) {
    if (data.username) {
      const u = { ...store.user!, username: data.username }
      store.setUser(u)
    }
    showMessage('账号信息已更新')
    editingAccount.value = false
  } else {
    showMessage(res.message || '更新失败', 'error')
  }
  saving.value = false
}

async function saveProfile() {
  saving.value = true
  const fn = store.isCoach ? updateCoachProfile : updateStudentProfile
  const res = await fn(profileForm.value)
  if (res.code === 200) {
    showMessage('资料已更新')
    editingProfile.value = false
    await loadProfile()
  } else {
    showMessage(res.message || '更新失败', 'error')
  }
  saving.value = false
}

function cancelAccountEdit() {
  editingAccount.value = false
  accountForm.value = { username: store.user?.username || '', password: '' }
}

function cancelProfileEdit() {
  editingProfile.value = false
  profileForm.value = { ...profileData.value }
}

function showMessage(msg: string, type: 'success' | 'error' = 'success') {
  message.value = msg
  messageType.value = type
  setTimeout(() => { message.value = '' }, 3000)
}

function genderDisplay(gender: string) {
  if (!gender) return '-'
  const map: Record<string, string> = { 'MALE': '男', 'FEMALE': '女', 'OTHER': '其他' }
  return map[gender] || gender
}
</script>

<style scoped>
.profile-header {
  position: relative;
  background: linear-gradient(135deg, rgba(255,255,255,0.9) 0%, rgba(229,246,255,0.92) 52%, rgba(210,236,250,0.9) 100%);
  border: 1px solid var(--c-glass-border);
  border-radius: var(--radius-lg);
  padding: 36px 32px;
  margin-bottom: 24px;
  overflow: hidden;
}

.profile-header-glow {
  position: absolute;
  top: -30%;
  right: -10%;
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, rgba(34, 211, 238, 0.05), transparent);
  pointer-events: none;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 20px;
  position: relative;
  z-index: 1;
}

.avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--c-accent), #0EA5E9);
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: var(--font-display);
  font-size: 28px;
  font-weight: 700;
  color: var(--c-deep-1);
  box-shadow: 0 0 20px var(--c-accent-glow);
  flex-shrink: 0;
}

.profile-name {
  font-family: var(--font-display);
  font-size: 22px;
  font-weight: 700;
  color: var(--c-text);
}

.profile-decoration {
  position: absolute;
  right: 20px;
  bottom: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.deco-ring {
  position: absolute;
  width: 120px;
  height: 120px;
  border: 1px solid rgba(34, 211, 238, 0.1);
  border-radius: 50%;
  animation: ringPulse 4s ease-in-out infinite;
}

@keyframes ringPulse {
  0%, 100% { transform: scale(0.8); opacity: 0.3; }
  50% { transform: scale(1.1); opacity: 0.6; }
}

.profile-section {
  margin-bottom: 20px;
  padding: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--c-glass-border);
}

.section-header h3 {
  font-family: var(--font-display);
  font-size: 17px;
  font-weight: 600;
  color: var(--c-text);
}

.btn-group { display: flex; gap: 8px; }

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.form-field label {
  display: block;
  font-family: var(--font-display);
  font-size: 12px;
  color: var(--c-text-muted);
  margin-bottom: 6px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.form-field .field-value {
  font-size: 15px;
  color: var(--c-text);
  padding: 8px 0;
  display: block;
}

.form-field.full-width { grid-column: 1 / -1; }

@media (max-width: 768px) {
  .form-grid { grid-template-columns: 1fr; }
}
</style>
