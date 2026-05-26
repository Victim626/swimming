<template>
  <div class="chat-page">
    <nav class="glass-nav" style="position:relative;">
      <div class="nav-actions" style="gap:12px;">
        <button class="btn-back" @click="$router.push('/dashboard')">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>
        </button>
        <div class="glass-nav-brand" style="gap:0;">
          <span class="brand-icon" style="margin-right:8px;">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z"/></svg>
          </span>
          即时沟通
        </div>
      </div>
    </nav>

    <div class="chat-container glass">
      <!-- 联系人列表 -->
      <div class="chat-sidebar">
        <div class="chat-sidebar-header">
          <h3>联系人</h3>
          <span class="badge badge-neutral">{{ contacts.length }}</span>
        </div>
        <div class="contacts-list">
          <div v-if="contacts.length===0" class="empty-state" style="padding:32px;">
            <p style="font-size:13px;">暂无联系人</p>
          </div>
          <div
            v-for="c in contacts"
            :key="c.id"
            :class="['contact-item', { active: receiverId===c.id }]"
            @click="selectContact(c.id, c.realName||c.username)"
          >
            <div class="contact-avatar">{{ (c.realName||c.username||'?')[0] }}</div>
            <div class="contact-body">
              <div class="contact-name">{{ c.realName || c.username }}</div>
              <div class="contact-role">{{ c.role==='COACH' ? '教练' : '学员' }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 聊天区域 -->
      <div class="chat-main">
        <div class="chat-main-header">
          <span v-if="receiverName">
            <span class="chat-with">与</span>
            <strong>{{ receiverName }}</strong>
            <span class="chat-with">聊天中</span>
          </span>
          <span v-else class="chat-placeholder">选择一个联系人开始聊天</span>
        </div>

        <div class="message-area" ref="msgArea">
          <div v-if="messages.length===0" class="empty-state" style="padding:60px;">
            <p style="font-size:13px;color:var(--c-text-muted);">{{ receiverId ? '暂无消息' : '选择联系人开始聊天' }}</p>
          </div>
          <div v-for="m in messages" :key="m.id" :class="['msg-row', m.senderId===store.user?.id ? 'sent' : 'received']">
            <div class="msg-bubble">{{ m.content }}</div>
            <div class="msg-time">{{ formatTime(m.sendTime) }}</div>
          </div>
        </div>

        <div class="chat-input-area">
          <input
            v-model="inputText"
            class="chat-input"
            placeholder="输入消息..."
            :disabled="!receiverId"
            @keypress.enter="send"
          />
          <button class="btn btn-primary" :disabled="!receiverId" @click="send">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><path d="M22 2L11 13M22 2l-7 20-4-9-9-4 20-7z"/></svg>
            发送
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useUserStore } from '../stores/user'
import { getChatUsers, getChatHistory, sendMessage } from '../api'

const store = useUserStore()
const contacts = ref<any[]>([])
const messages = ref<any[]>([])
const receiverId = ref<number|null>(null)
const receiverName = ref('')
const inputText = ref('')
const msgArea = ref<HTMLElement|null>(null)
let pollTimer: any = null
let lastId = 0

onMounted(async () => { const r=await getChatUsers(); if(r.code===200) contacts.value=r.data; startPoll() })
onUnmounted(() => { if(pollTimer) clearInterval(pollTimer) })

function startPoll() { pollTimer = setInterval(pollMessages, 2000) }
async function pollMessages() { if(!receiverId.value) return; const r=await getChatHistory(receiverId.value); if(r.code===200) { const newMsgs = r.data.filter(m => m.id && m.id > lastId); if(newMsgs.length>0) { messages.value = r.data; lastId = r.data[r.data.length-1]?.id||0; scrollDown() } } }
async function selectContact(id: number, name: string) {
  receiverId.value = id; receiverName.value = name; lastId = 0
  const r = await getChatHistory(id)
  if(r.code===200) { messages.value = r.data; if(r.data.length>0) lastId = r.data[r.data.length-1]?.id||0; scrollDown() }
}
async function send() {
  if(!inputText.value.trim() || !receiverId.value) return
  await sendMessage({ senderId: store.user!.id, receiverId: receiverId.value, content: inputText.value, messageType: 'TEXT' })
  inputText.value = ''
  const r = await getChatHistory(receiverId.value)
  if(r.code===200) { messages.value = r.data; lastId = r.data[r.data.length-1]?.id||0; scrollDown() }
}
function formatTime(t?: string) { return t ? new Date(t).toLocaleString('zh-CN') : '' }
function scrollDown() { nextTick(() => { if(msgArea.value) msgArea.value.scrollTop = msgArea.value.scrollHeight }) }
</script>

<style scoped>
.chat-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.chat-container {
  flex: 1;
  margin: 16px 20px;
  display: flex;
  overflow: hidden;
  border-radius: var(--radius-lg);
}

.chat-sidebar {
  width: 240px;
  border-right: 1px solid var(--c-glass-border);
  display: flex;
  flex-direction: column;
}

.chat-sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border-bottom: 1px solid var(--c-glass-border);
}

.chat-sidebar-header h3 {
  font-family: var(--font-display);
  font-size: 15px;
  font-weight: 600;
}

.contacts-list {
  flex: 1;
  overflow-y: auto;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  cursor: pointer;
  transition: background 0.2s;
}

.contact-item:hover { background: var(--c-surface); }
.contact-item.active { background: var(--c-accent-dim); }

.contact-avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--c-accent), #0EA5E9);
  color: var(--c-deep-1);
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: var(--font-display);
  font-size: 15px;
  font-weight: 700;
  flex-shrink: 0;
}

.contact-body { flex: 1; min-width: 0; }
.contact-name { font-size: 14px; font-weight: 500; }
.contact-role { font-size: 12px; color: var(--c-text-muted); }

.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: rgba(255,255,255,0.72);
  min-width: 0;
}

.chat-main-header {
  padding: 14px 20px;
  border-bottom: 1px solid var(--c-glass-border);
  font-size: 14px;
  color: var(--c-text-secondary);
}

.chat-with { color: var(--c-text-muted); }

.chat-placeholder { color: var(--c-text-dim); }

.message-area {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.msg-row {
  display: flex;
  flex-direction: column;
  margin-bottom: 16px;
}

.msg-row.sent { align-items: flex-end; }
.msg-row.received { align-items: flex-start; }

.msg-bubble {
  max-width: 65%;
  padding: 12px 18px;
  font-size: 14px;
  line-height: 1.5;
  border-radius: 18px;
  word-break: break-word;
}

.msg-row.sent .msg-bubble {
  background: linear-gradient(135deg, var(--c-accent), #0EA5E9);
  color: var(--c-deep-1);
  border-bottom-right-radius: 4px;
  box-shadow: 0 2px 8px var(--c-accent-glow);
}

.msg-row.received .msg-bubble {
  background: var(--c-surface);
  color: var(--c-text);
  border: 1px solid var(--c-glass-border);
  border-bottom-left-radius: 4px;
}

.msg-time {
  font-size: 11px;
  color: var(--c-text-muted);
  margin-top: 4px;
  padding: 0 4px;
}

.chat-input-area {
  display: flex;
  gap: 10px;
  padding: 14px 20px;
  border-top: 1px solid var(--c-glass-border);
}

.chat-input {
  flex: 1;
  padding: 12px 16px;
  font-family: var(--font-body);
  font-size: 14px;
  background: var(--c-surface);
  border: 1.5px solid var(--c-glass-border);
  border-radius: var(--radius-sm);
  outline: none;
  transition: border-color 0.2s;
  color: var(--c-text);
}

.chat-input:focus { border-color: var(--c-accent); box-shadow: 0 0 0 3px var(--c-accent-dim); }
.chat-input:disabled { opacity: 0.4; }
.chat-input::placeholder { color: var(--c-text-dim); }

@media (max-width: 900px) {
  .chat-page {
    height: auto;
    min-height: 100vh;
  }

  .chat-container {
    margin: 12px 16px 16px;
    flex-direction: column;
    min-height: calc(100vh - 72px);
  }

  .chat-sidebar {
    width: 100%;
    max-height: 260px;
    border-right: 0;
    border-bottom: 1px solid var(--c-glass-border);
  }

  .contacts-list {
    max-height: 200px;
  }

  .chat-main {
    min-height: 0;
  }

  .message-area {
    padding: 16px;
  }

  .msg-bubble {
    max-width: 82%;
  }

  .chat-input-area {
    padding: 12px 16px;
  }
}

@media (max-width: 640px) {
  .chat-container {
    margin: 10px 12px 12px;
    border-radius: 18px;
  }

  .chat-sidebar-header {
    padding: 12px 14px;
  }

  .contact-item {
    padding: 11px 14px;
  }

  .chat-main-header {
    padding: 12px 14px;
    font-size: 13px;
  }

  .message-area {
    padding: 14px 12px;
  }

  .msg-bubble {
    max-width: 88%;
    padding: 11px 14px;
    font-size: 13px;
  }

  .chat-input-area {
    gap: 8px;
    padding: 12px;
    flex-wrap: wrap;
  }

  .chat-input {
    min-width: 0;
    padding: 11px 14px;
    flex-basis: 100%;
  }

  .chat-input-area .btn {
    width: 100%;
    justify-content: center;
  }
}
</style>
