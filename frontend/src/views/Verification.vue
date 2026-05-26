<template>
  <div class="page">
    <nav class="glass-nav">
      <div class="nav-actions" style="gap:12px;">
        <button class="btn-back" @click="$router.push('/dashboard')">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>
        </button>
        <div class="glass-nav-brand">
          <span class="brand-icon">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><path d="M9 12l2 2 4-4"/><path d="M12 2a10 10 0 1010 10"/></svg>
          </span>
          教练认证
        </div>
      </div>
    </nav>

    <div class="page-container" style="max-width:560px;">
      <div class="verify-card glass">
        <div class="verify-header">
          <div class="verify-icon">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M9 12l2 2 4-4"/><path d="M12 2a10 10 0 1010 10"/></svg>
          </div>
          <h1>教练资质认证</h1>
          <p>提交资质信息等待人工审核</p>
        </div>

        <!-- 状态 -->
        <div v-if="status!==null" :class="['status-banner', statusClass]">
          <div class="status-icon-large">{{ statusIcon }}</div>
          <div class="status-info">
            <div class="status-title">{{ statusText }}</div>
            <div class="status-desc">{{ statusDesc }}</div>
          </div>
        </div>

        <div class="notice">
          提交后将由管理员进行人工审核，审核通过后才能使用平台全部功能。
        </div>

        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label class="form-label">真实姓名 <span class="req">*</span></label>
            <input v-model="realName" class="form-input" required placeholder="请输入真实姓名" :disabled="formDisabled" />
          </div>
          <div class="form-group">
            <label class="form-label">身份证号 <span class="req">*</span></label>
            <input v-model="idCard" class="form-input" required placeholder="18位身份证号" maxlength="18" :disabled="formDisabled" />
          </div>
          <div class="form-group">
            <label class="form-label">职业资格证书号 <span class="req">*</span></label>
            <input v-model="certNo" class="form-input" required placeholder="教练资格证件号" :disabled="formDisabled" />
          </div>

          <button type="submit" class="btn btn-primary btn-block btn-lg" :disabled="formDisabled || loading">
            {{ loading ? '提交中...' : (formDisabled ? '已提交' : '提交认证') }}
          </button>

          <div v-if="error" class="msg msg-error">{{ error }}</div>
          <div v-if="successMsg" class="msg msg-success">{{ successMsg }}</div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getVerificationStatus, submitVerification } from '../api'

const router = useRouter()
const store = useUserStore()
const realName = ref('')
const idCard = ref('')
const certNo = ref('')
const error = ref('')
const successMsg = ref('')
const loading = ref(false)
const status = ref<number|null>(null)
const formDisabled = ref(false)

const statusClass = computed(() => status.value===0 ? 'pending' : status.value===1 ? 'approved' : 'rejected')
const statusIcon = computed(() => status.value===0 ? '⏳' : status.value===1 ? '✅' : '❌')
const statusText = computed(() => status.value===0 ? '待审核' : status.value===1 ? '认证通过' : '认证未通过')
const statusDesc = computed(() => status.value===0 ? '等待管理员审核' : status.value===1 ? '恭喜通过认证' : '请修改后重新提交')

onMounted(async () => {
  if(store.user?.role !== 'COACH') { router.push('/dashboard'); return }
  const r = await getVerificationStatus()
  if(r.code===200 && r.data) {
    const p = r.data as any
    realName.value = p.realName||''; idCard.value = p.idCardNumber||''; certNo.value = p.certificateNumber||''
    status.value = p.qualificationVerified
    if(status.value===0 || status.value===1) formDisabled.value = true
  }
})

async function handleSubmit() {
  if(!realName.value || !idCard.value || !certNo.value) { error.value='请填写必填项'; return }
  if(!/^[0-9Xx]{18}$/.test(idCard.value)) { error.value='身份证号格式不正确'; return }
  loading.value = true; error.value = ''; successMsg.value = ''
  const r = await submitVerification({ realName: realName.value, idCardNumber: idCard.value, certificateNumber: certNo.value })
  if(r.code===200) { successMsg.value='提交成功！等待审核'; formDisabled.value=true }
  else error.value = r.message
  loading.value = false
}
</script>

<style scoped>
.page { min-height: 100vh; background: transparent; }

.verify-card {
  padding: 36px;
}

.verify-header {
  text-align: center;
  margin-bottom: 28px;
}

.verify-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, var(--c-accent), #0EA5E9);
  color: var(--c-deep-1);
  border-radius: 16px;
  margin-bottom: 14px;
  box-shadow: 0 0 20px var(--c-accent-glow);
}

.verify-header h1 {
  font-family: var(--font-display);
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 4px;
}

.verify-header p {
  font-size: 14px;
  color: var(--c-text-secondary);
}

.status-banner {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px;
  border-radius: var(--radius-sm);
  margin-bottom: 20px;
}

.status-banner.pending { background: var(--c-warning-bg); color: var(--c-warning); }
.status-banner.approved { background: var(--c-success-bg); color: var(--c-success); }
.status-banner.rejected { background: var(--c-danger-bg); color: var(--c-danger); }

.status-icon-large { font-size: 32px; }

.status-title { font-size: 15px; font-weight: 600; }
.status-desc { font-size: 13px; margin-top: 2px; opacity: 0.8; }

.notice {
  background: var(--c-accent-dim);
  border-left: 3px solid var(--c-accent);
  padding: 12px 16px;
  margin-bottom: 24px;
  font-size: 13px;
  color: var(--c-accent);
  border-radius: 0 var(--radius-sm) var(--radius-sm) 0;
  line-height: 1.5;
}

.req { color: var(--c-danger); }

.msg {
  margin-top: 12px;
  padding: 10px 14px;
  font-size: 13px;
  font-weight: 500;
  border-radius: var(--radius-sm);
  text-align: center;
}

.msg-error { background: var(--c-danger-bg); color: var(--c-danger); }
.msg-success { background: var(--c-success-bg); color: var(--c-success); }
</style>
