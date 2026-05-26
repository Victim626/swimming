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
          资质审核
        </div>
      </div>
    </nav>

    <div class="page-container">
      <!-- 统计 -->
      <div class="stats-grid">
        <div class="stat-card stat-pending">
          <div class="stat-number">{{ stats.pending }}</div>
          <div class="stat-label">待审核</div>
        </div>
        <div class="stat-card stat-approved">
          <div class="stat-number">{{ stats.approved }}</div>
          <div class="stat-label">已通过</div>
        </div>
        <div class="stat-card stat-rejected">
          <div class="stat-number">{{ stats.rejected }}</div>
          <div class="stat-label">未通过</div>
        </div>
      </div>

      <!-- 筛选 -->
      <div class="tabs">
        <button v-for="f in filters" :key="f.key" :class="['tab',{active:currentFilter===f.key}]" @click="currentFilter=f.key">{{ f.label }}</button>
      </div>

      <!-- 表格 -->
      <div class="table-wrap glass">
        <table class="data-table">
          <thead>
            <tr>
              <th>姓名</th>
              <th>用户名</th>
              <th>身份证</th>
              <th>资格证</th>
              <th>提交时间</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in filteredData" :key="item.id">
              <td><span class="td-name">{{ item.realName||'-' }}</span></td>
              <td>{{ item.username||'-' }}</td>
              <td><code class="id-mask">{{ maskIdCard(item.idCardNumber) }}</code></td>
              <td>{{ item.certificateNumber||'-' }}</td>
              <td style="white-space:nowrap;">{{ formatTime(item.createdAt) }}</td>
              <td>
                <span :class="['badge', statusBadge(item.qualificationVerified)]">
                  {{ statusLabel(item.qualificationVerified) }}
                </span>
              </td>
              <td>
                <template v-if="item.qualificationVerified===0">
                  <div style="display:flex;gap:6px;">
                    <button class="btn btn-success btn-sm" @click="approve(item.id)">通过</button>
                    <button class="btn btn-danger btn-sm" @click="reject(item.id)">拒绝</button>
                  </div>
                </template>
                <span v-else style="font-size:12px;color:var(--c-text-muted);">已处理</span>
              </td>
            </tr>
          </tbody>
        </table>
        <div v-if="filteredData.length===0" class="empty-state" style="padding:60px;">
          <p>暂无数据</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getVerificationList, approveVerification, rejectVerification } from '../api'

const allData = ref<any[]>([])
const currentFilter = ref('all')
const filters = [{ key:'all',label:'全部' },{ key:'0',label:'待审核' },{ key:'1',label:'已通过' },{ key:'2',label:'未通过' }]

const stats = computed(() => ({
  pending: allData.value.filter(d => d.qualificationVerified===0).length,
  approved: allData.value.filter(d => d.qualificationVerified===1).length,
  rejected: allData.value.filter(d => d.qualificationVerified===2).length,
}))
const filteredData = computed(() => currentFilter.value==='all' ? allData.value : allData.value.filter(d => d.qualificationVerified===Number(currentFilter.value)))

onMounted(async () => { const r=await getVerificationList(); if(r.code===200) allData.value=r.data })
function statusBadge(s: number) { return s===0?'badge-warning':s===1?'badge-success':'badge-danger' }
function statusLabel(s: number) { return s===0?'待审核':s===1?'已通过':'未通过' }
function maskIdCard(c?: string) { return c ? c.substring(0,6)+'********'+c.substring(14) : '-' }
function formatTime(t?: string) { return t ? new Date(t).toLocaleString('zh-CN') : '-' }
async function approve(id: number) { if(!confirm('通过？')) return; await approveVerification(id); const r=await getVerificationList(); if(r.code===200) allData.value=r.data }
async function reject(id: number) { if(!confirm('拒绝？')) return; await rejectVerification(id); const r=await getVerificationList(); if(r.code===200) allData.value=r.data }
</script>

<style scoped>
.page { min-height: 100vh; background: transparent; }

.table-wrap {
  overflow-x: auto;
  padding: 0;
}

.td-name { font-weight: 500; }

.id-mask {
  font-family: var(--font-mono);
  font-size: 13px;
  background: var(--c-surface);
  padding: 2px 6px;
  border-radius: 4px;
  letter-spacing: 0.5px;
  color: var(--c-text-secondary);
}

.data-table th:last-child,
.data-table td:last-child { text-align: center; }
</style>
