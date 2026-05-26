<template>
  <div class="page posts-page">
    <nav class="glass-nav">
      <div class="nav-actions" style="gap:12px;">
        <button class="btn-back" @click="$router.push('/dashboard')">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>
        </button>
        <div class="glass-nav-brand" style="gap:0;">
          <span class="brand-icon" style="margin-right:8px;">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><path d="M4 4h16v12H7l-3 3z"/><path d="M8 8h8M8 12h5"/></svg>
          </span>
          训练分享
        </div>
      </div>
      <button class="btn btn-primary btn-sm" @click="openCreateModal">+ 发帖</button>
    </nav>

    <div class="page-container">
      <section class="posts-hero glass">
        <div class="posts-hero-copy">
          <span class="hero-kicker">TRAINING FEED</span>
          <h1>把训练里的进步，发成一条有温度的记录。</h1>
          <p>关联打卡数据，展示当天训练信息，点赞、评论、编辑和删除都在这里完成。</p>
        </div>
        <div class="posts-hero-stats">
          <div class="hero-stat">
            <div class="hero-stat-num">{{ totalPosts }}</div>
            <div class="hero-stat-label">帖子总数</div>
          </div>
          <div class="hero-stat">
            <div class="hero-stat-num">{{ currentPage }}</div>
            <div class="hero-stat-label">当前页</div>
          </div>
          <div class="hero-stat">
            <div class="hero-stat-num">{{ totalPages }}</div>
            <div class="hero-stat-label">总页数</div>
          </div>
        </div>
      </section>

      <section class="feed-shell">
        <div class="feed-list">
          <div v-if="loading" class="empty-state glass" style="padding:48px;">
            <p>正在加载帖子…</p>
          </div>

          <div v-else-if="posts.length===0" class="empty-state glass" style="padding:56px;">
            <div class="empty-icon">
              <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M4 4h16v12H7l-3 3z"/><path d="M8 8h8M8 12h5"/></svg>
            </div>
            <p>还没有帖子，先发第一条训练分享吧。</p>
            <button class="btn btn-primary btn-sm" style="margin-top:16px;" @click="openCreateModal">发一条</button>
          </div>

          <article v-for="post in posts" :key="post.id" class="post-card glass">
            <header class="post-card-top">
              <div class="post-author">
                <div class="post-avatar">{{ authorInitial(post) }}</div>
                <div>
                  <h3>{{ post.realName || post.username || '匿名用户' }}</h3>
                  <p>@{{ post.username || 'unknown' }} · {{ formatTime(post.createdAt) }}</p>
                </div>
              </div>
              <div class="post-badge-row">
                <span v-if="post.showTrainingInfo && post.planName" class="badge badge-info">训练关联</span>
                <span v-if="isPostOwner(post)" class="badge badge-primary">我发布的</span>
              </div>
            </header>

            <div class="post-body">
              <h2>{{ post.title }}</h2>
              <p class="post-content">{{ post.content }}</p>

              <div v-if="post.showTrainingInfo && post.planName" class="training-chip-grid">
                <span class="training-chip">{{ post.planName }}</span>
                <span class="training-chip">第 {{ post.dayNumber }} 天</span>
                <span class="training-chip">{{ post.trainingDate }}</span>
                <span class="training-chip">{{ post.intensity || '未设置强度' }}</span>
                <span class="training-chip">{{ post.duration || '-' }} 分钟</span>
                <span class="training-chip">{{ post.distance || '-' }} 米</span>
              </div>
            </div>

            <footer class="post-actions">
              <div class="post-meta">
                <span>❤ {{ post.likeCount || 0 }}</span>
                <span>💬 {{ post.commentCount || 0 }}</span>
              </div>
              <div class="post-action-buttons">
                <button class="btn btn-ghost btn-sm" @click="openDetail(post.id)">详情/评论</button>
                <button class="btn btn-primary btn-sm" @click="togglePostLike(post)">
                  {{ post.likedByCurrentUser ? '取消点赞' : '点赞' }}
                </button>
                <button v-if="isPostOwner(post)" class="btn btn-ghost btn-sm" @click="openEditModal(post)">编辑</button>
                <button v-if="isPostOwner(post)" class="btn btn-danger btn-sm" @click="handleDelete(post.id)">删除</button>
              </div>
            </footer>
          </article>

          <div v-if="posts.length>0" class="pagination-bar glass">
            <button class="btn btn-ghost btn-sm" :disabled="currentPage===1" @click="changePage(currentPage-1)">上一页</button>
            <span>第 {{ currentPage }} / {{ totalPages }} 页</span>
            <button class="btn btn-ghost btn-sm" :disabled="currentPage>=totalPages" @click="changePage(currentPage+1)">下一页</button>
          </div>
        </div>

        <aside class="feed-side">
          <div class="side-panel glass">
            <div class="side-panel-title">发布提示</div>
            <ul>
              <li>标题控制在 200 字以内</li>
              <li>可关联一条训练打卡记录</li>
              <li>训练信息可单独开关展示</li>
            </ul>
          </div>
          <div class="side-panel glass">
            <div class="side-panel-title">评论说明</div>
            <p>支持发评论、回复、编辑、删除与点赞；评论状态会跟随当前登录用户独立显示。</p>
          </div>
        </aside>
      </section>
    </div>

    <div v-if="showFormModal" class="modal-overlay" @click.self="closeFormModal">
      <div class="modal-content post-modal">
        <div class="modal-header">
          <h2>{{ editingPost ? '编辑帖子' : '发帖' }}</h2>
          <button class="btn-back" @click="closeFormModal">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><path d="M18 6L6 18M6 6l12 12"/></svg>
          </button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="handleSubmit">
            <div class="form-group">
              <label class="form-label">标题</label>
              <input v-model="form.title" maxlength="200" class="form-input" placeholder="今天训练有什么收获？" />
            </div>
            <div class="form-group">
              <label class="form-label">内容</label>
              <textarea v-model="form.content" class="form-input" rows="6" placeholder="分享训练过程、感受和改进点。"></textarea>
            </div>
            <div class="form-group">
              <label class="form-label">关联打卡</label>
              <select v-model="form.planDetailId" class="form-input">
                <option :value="null">不关联</option>
                <option v-for="item in checkinData" :key="item.id" :value="item.id">
                  第 {{ item.dayNumber }} 天 · {{ item.planName }} · {{ item.trainingDate || '未填日期' }}
                </option>
              </select>
            </div>
            <div v-if="selectedCheckin" class="checkin-preview glass">
              <div class="checkin-preview-title">{{ selectedCheckin.planName }}</div>
              <div class="checkin-preview-grid">
                <span>第 {{ selectedCheckin.dayNumber }} 天</span>
                <span>{{ selectedCheckin.trainingDate || '未填日期' }}</span>
                <span>{{ selectedCheckin.intensity || '未设置强度' }}</span>
                <span>{{ selectedCheckin.duration || '-' }} 分钟</span>
                <span>{{ selectedCheckin.distance || '-' }} 米</span>
              </div>
            </div>
            <label class="toggle-row">
              <input v-model="form.showTrainingInfo" type="checkbox" />
              <span>在帖子中展示训练信息</span>
            </label>
            <div class="modal-footer" style="padding:16px 0 0;">
              <button type="button" class="btn btn-ghost" @click="closeFormModal">取消</button>
              <button type="submit" class="btn btn-primary">{{ editingPost ? '保存修改' : '发布帖子' }}</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <div v-if="detailPost" class="modal-overlay" @click.self="closeDetail">
      <div class="modal-content post-modal detail-modal">
        <div class="modal-header">
          <h2>帖子详情</h2>
          <button class="btn-back" @click="closeDetail">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><path d="M18 6L6 18M6 6l12 12"/></svg>
          </button>
        </div>
        <div class="modal-body">
          <div class="detail-author">
            <div class="post-avatar">{{ authorInitial(detailPost) }}</div>
            <div>
              <h3>{{ detailPost.realName || detailPost.username || '匿名用户' }}</h3>
              <p>@{{ detailPost.username || 'unknown' }} · {{ formatTime(detailPost.createdAt) }}</p>
            </div>
          </div>

          <h1 class="detail-title">{{ detailPost.title }}</h1>
          <p class="detail-content">{{ detailPost.content }}</p>

          <div v-if="detailPost.showTrainingInfo && detailPost.planName" class="detail-training glass">
            <div class="side-panel-title">训练信息</div>
            <div class="detail-training-grid">
              <span>计划：{{ detailPost.planName }}</span>
              <span>第 {{ detailPost.dayNumber }} 天</span>
              <span>日期：{{ detailPost.trainingDate }}</span>
              <span>强度：{{ detailPost.intensity || '未设置' }}</span>
              <span>时长：{{ detailPost.duration || '-' }} 分钟</span>
              <span>距离：{{ detailPost.distance || '-' }} 米</span>
            </div>
          </div>

          <div class="detail-stats">
            <span>❤ {{ detailPost.likeCount || 0 }}</span>
            <span>💬 {{ detailPost.commentCount || 0 }}</span>
          </div>

          <div class="comments-shell glass">
            <div class="comments-header">
              <div class="side-panel-title">评论</div>
              <span class="badge badge-neutral">{{ comments.length }} 条</span>
            </div>

            <form class="comment-form" @submit.prevent="submitComment">
              <textarea
                v-model="commentForm.content"
                class="form-input"
                rows="3"
                :placeholder="commentPlaceholder"
              ></textarea>
              <div class="comment-form-actions">
                <button v-if="commentMode !== 'create'" type="button" class="btn btn-ghost btn-sm" @click="resetCommentForm">取消</button>
                <button type="submit" class="btn btn-primary btn-sm" :disabled="commentBusy">
                  {{ commentButtonText }}
                </button>
              </div>
            </form>

            <div v-if="comments.length===0" class="empty-state" style="padding:30px 0;">
              <p>还没有评论，来抢个沙发吧。</p>
            </div>

            <div v-for="comment in comments" :key="comment.id" class="comment-thread">
              <article class="comment-item">
                <div class="comment-avatar">{{ authorInitial(comment) }}</div>
                <div class="comment-body">
                  <div class="comment-topline">
                    <div>
                      <strong>{{ comment.realName || comment.username || '匿名用户' }}</strong>
                      <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
                    </div>
                    <span v-if="comment.deleted" class="badge badge-neutral">已删除</span>
                  </div>
                  <p class="comment-text">{{ comment.content }}</p>
                  <div class="comment-actions">
                    <button class="btn btn-ghost btn-sm" @click="toggleCommentLike(comment)">
                      {{ comment.likedByCurrentUser ? '取消点赞' : '点赞' }} · {{ comment.likeCount || 0 }}
                    </button>
                    <button v-if="!comment.deleted" class="btn btn-ghost btn-sm" @click="startReply(comment)">回复</button>
                    <button v-if="isCommentOwner(comment) && !comment.deleted" class="btn btn-ghost btn-sm" @click="startEdit(comment)">编辑</button>
                    <button v-if="isCommentOwner(comment) && !comment.deleted" class="btn btn-danger btn-sm" @click="deleteComment(comment)">删除</button>
                  </div>
                </div>
              </article>

              <div v-if="comment.replies?.length" class="comment-replies">
                <article v-for="reply in comment.replies" :key="reply.id" class="comment-item comment-reply">
                  <div class="comment-avatar small">{{ authorInitial(reply) }}</div>
                  <div class="comment-body">
                    <div class="comment-topline">
                      <div>
                        <strong>{{ reply.realName || reply.username || '匿名用户' }}</strong>
                        <span class="comment-time">{{ formatTime(reply.createdAt) }}</span>
                      </div>
                      <span v-if="reply.deleted" class="badge badge-neutral">已删除</span>
                    </div>
                    <p class="comment-text">{{ reply.content }}</p>
                    <div class="comment-actions">
                      <button class="btn btn-ghost btn-sm" @click="toggleCommentLike(reply)">
                        {{ reply.likedByCurrentUser ? '取消点赞' : '点赞' }} · {{ reply.likeCount || 0 }}
                      </button>
                      <button v-if="isCommentOwner(reply) && !reply.deleted" class="btn btn-ghost btn-sm" @click="startEdit(reply)">编辑</button>
                      <button v-if="isCommentOwner(reply) && !reply.deleted" class="btn btn-danger btn-sm" @click="deleteComment(reply)">删除</button>
                    </div>
                  </div>
                </article>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="message" :class="['toast', messageType==='error' ? 'toast-error' : 'toast-success']">{{ message }}</div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useUserStore } from '../stores/user'
import {
  createPost,
  deletePost,
  getPostCheckinData,
  getPostComments,
  getPostDetail,
  getPostList,
  likeComment,
  likePost,
  createPostComment,
  updatePostComment,
  deletePostComment,
  unlikeComment,
  unlikePost,
  updatePost,
} from '../api'
import type { CommentItem, PostCheckinData, PostCommentFormData, PostFormData, PostItem } from '../types'

const store = useUserStore()
const posts = ref<PostItem[]>([])
const checkinData = ref<PostCheckinData[]>([])
const comments = ref<CommentItem[]>([])
const totalPosts = ref(0)
const currentPage = ref(1)
const totalPages = ref(1)
const loading = ref(false)
const detailPost = ref<PostItem | null>(null)
const showFormModal = ref(false)
const editingPost = ref<PostItem | null>(null)
const message = ref('')
const messageType = ref<'success' | 'error'>('success')
const commentBusy = ref(false)
const commentMode = ref<'create' | 'reply' | 'edit'>('create')
const commentTargetId = ref<number | null>(null)
const form = ref<PostFormData>({ title: '', content: '', planDetailId: null, showTrainingInfo: false })
const commentForm = ref<PostCommentFormData>({ content: '', parentId: null })

const selectedCheckin = computed(() => checkinData.value.find(item => item.id === form.value.planDetailId) || null)
const commentPlaceholder = computed(() => {
  if (commentMode.value === 'edit') return '编辑你的评论...'
  if (commentMode.value === 'reply') return '回复这条评论...'
  return '写下你的评论...'
})
const commentButtonText = computed(() => {
  if (commentMode.value === 'edit') return '保存修改'
  if (commentMode.value === 'reply') return '回复评论'
  return '发表评论'
})

onMounted(async () => {
  await Promise.all([loadPosts(), loadCheckinData()])
})

watch(() => form.value.planDetailId, () => {
  if (form.value.planDetailId == null) {
    form.value.showTrainingInfo = false
  }
})

function showToast(text: string, type: 'success' | 'error' = 'success') {
  message.value = text
  messageType.value = type
  setTimeout(() => { message.value = '' }, 2500)
}

async function loadPosts(page = currentPage.value) {
  loading.value = true
  const res = await getPostList(page, 8)
  if (res.code === 200) {
    posts.value = res.data.list || []
    totalPosts.value = res.data.total || 0
    currentPage.value = res.data.page || page
    totalPages.value = res.data.totalPages || 1
  } else {
    showToast(res.message || '加载帖子失败', 'error')
  }
  loading.value = false
}

async function loadCheckinData() {
  const res = await getPostCheckinData()
  if (res.code === 200) checkinData.value = res.data || []
}

async function loadComments(postId: number) {
  const res = await getPostComments(postId)
  if (res.code === 200) {
    comments.value = res.data || []
  } else {
    comments.value = []
    showToast(res.message || '加载评论失败', 'error')
  }
}

async function refreshDetail(postId: number) {
  const [detailRes, commentRes] = await Promise.all([getPostDetail(postId), getPostComments(postId)])
  if (detailRes.code === 200) {
    detailPost.value = detailRes.data
  }
  if (commentRes.code === 200) {
    comments.value = commentRes.data || []
  }
}

function openCreateModal() {
  editingPost.value = null
  form.value = { title: '', content: '', planDetailId: null, showTrainingInfo: false }
  showFormModal.value = true
}

function openEditModal(post: PostItem) {
  editingPost.value = post
  form.value = {
    title: post.title,
    content: post.content,
    planDetailId: post.planDetailId ?? null,
    showTrainingInfo: !!post.showTrainingInfo,
  }
  showFormModal.value = true
}

function closeFormModal() {
  showFormModal.value = false
  editingPost.value = null
}

function closeDetail() {
  detailPost.value = null
  comments.value = []
  resetCommentForm()
}

async function handleSubmit() {
  if (!form.value.title?.trim() || !form.value.content?.trim()) {
    showToast('请填写标题和内容', 'error')
    return
  }

  const payload: PostFormData = {
    title: form.value.title.trim(),
    content: form.value.content.trim(),
    planDetailId: form.value.planDetailId ?? null,
    showTrainingInfo: !!form.value.showTrainingInfo,
  }

  const res = editingPost.value
    ? await updatePost(editingPost.value.id, payload)
    : await createPost(payload)

  if (res.code === 200) {
    showToast(editingPost.value ? '帖子已更新' : '帖子已发布')
    closeFormModal()
    await loadPosts()
  } else {
    showToast(res.message || '操作失败', 'error')
  }
}

async function openDetail(id: number) {
  const [detailRes, commentRes] = await Promise.all([getPostDetail(id), getPostComments(id)])
  if (detailRes.code === 200) {
    detailPost.value = detailRes.data
  } else {
    showToast(detailRes.message || '加载详情失败', 'error')
    return
  }
  comments.value = commentRes.code === 200 ? (commentRes.data || []) : []
  if (commentRes.code !== 200) {
    showToast(commentRes.message || '加载评论失败', 'error')
  }
  resetCommentForm()
}

async function togglePostLike(post: PostItem) {
  const liked = !!post.likedByCurrentUser
  const res = liked ? await unlikePost(post.id) : await likePost(post.id)
  if (res.code === 200) {
    post.likedByCurrentUser = !liked
    post.likeCount = Math.max((post.likeCount || 0) + (liked ? -1 : 1), 0)
    if (detailPost.value?.id === post.id) {
      await refreshDetail(post.id)
    }
  } else {
    showToast(res.message || '点赞操作失败', 'error')
  }
}

async function handleDelete(id: number) {
  if (!confirm('确定删除这条帖子吗？')) return
  const res = await deletePost(id)
  if (res.code === 200) {
    showToast('帖子已删除')
    await loadPosts()
    if (detailPost.value?.id === id) closeDetail()
  } else {
    showToast(res.message || '删除失败', 'error')
  }
}

function changePage(page: number) {
  if (page < 1 || page > totalPages.value) return
  loadPosts(page)
}

async function submitComment() {
  if (!detailPost.value) return
  const content = commentForm.value.content.trim()
  if (!content) {
    showToast('请填写评论内容', 'error')
    return
  }

  commentBusy.value = true
  let res
  if (commentMode.value === 'edit' && commentTargetId.value != null) {
    res = await updatePostComment(commentTargetId.value, { content })
  } else {
    const payload: PostCommentFormData = {
      content,
      parentId: commentMode.value === 'reply' ? commentTargetId.value : null,
    }
    res = await createPostComment(detailPost.value.id, payload)
  }

  if (res.code === 200) {
    showToast(commentMode.value === 'edit' ? '评论已更新' : '评论已发布')
    await loadPosts(currentPage.value)
    await refreshDetail(detailPost.value.id)
    resetCommentForm()
  } else {
    showToast(res.message || '操作失败', 'error')
  }
  commentBusy.value = false
}

function startReply(comment: CommentItem) {
  commentMode.value = 'reply'
  commentTargetId.value = comment.id
  commentForm.value = { content: '', parentId: comment.id }
}

function startEdit(comment: CommentItem) {
  commentMode.value = 'edit'
  commentTargetId.value = comment.id
  commentForm.value = { content: comment.content, parentId: comment.parentId ?? null }
}

function resetCommentForm() {
  commentMode.value = 'create'
  commentTargetId.value = null
  commentForm.value = { content: '', parentId: null }
}

async function deleteComment(comment: CommentItem) {
  if (!confirm('确定删除这条评论吗？')) return
  const res = await deletePostComment(comment.id)
  if (res.code === 200) {
    showToast('评论已删除')
    if (detailPost.value) {
      await loadPosts(currentPage.value)
      await refreshDetail(detailPost.value.id)
    }
    if (commentTargetId.value === comment.id) resetCommentForm()
  } else {
    showToast(res.message || '删除失败', 'error')
  }
}

async function toggleCommentLike(comment: CommentItem) {
  const liked = !!comment.likedByCurrentUser
  const res = liked ? await unlikeComment(comment.id) : await likeComment(comment.id)
  if (res.code === 200) {
    if (detailPost.value) {
      await loadComments(detailPost.value.id)
      await refreshDetail(detailPost.value.id)
    }
  } else {
    showToast(res.message || '点赞操作失败', 'error')
  }
}

function authorInitial(item: Pick<PostItem, 'realName' | 'username'> | Pick<CommentItem, 'realName' | 'username'> | null) {
  const name = item?.realName || item?.username || '?'
  return name.charAt(0).toUpperCase()
}

function formatTime(value?: string) {
  if (!value) return '刚刚'
  return new Date(value).toLocaleString('zh-CN', { hour12: false })
}

function isPostOwner(post: PostItem) {
  return !!store.user && post.userId === store.user.id
}

function isCommentOwner(comment: CommentItem) {
  return !!store.user && comment.userId === store.user.id
}
</script>

<style scoped>
.posts-page {
  min-height: 100vh;
}

.posts-hero {
  display: grid;
  grid-template-columns: 1.6fr 1fr;
  gap: 24px;
  padding: 28px;
  margin-bottom: 20px;
}

.hero-kicker {
  display: inline-flex;
  align-items: center;
  width: fit-content;
  margin-bottom: 12px;
  padding: 4px 12px;
  border-radius: 999px;
  background: var(--c-accent-dim);
  border: 1px solid var(--c-glass-border);
  color: var(--c-accent);
  font-family: var(--font-display);
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 2px;
}

.posts-hero-copy h1 {
  font-family: var(--font-display);
  font-size: 34px;
  line-height: 1.04;
  margin-bottom: 12px;
}

.posts-hero-copy p {
  max-width: 56ch;
  color: var(--c-text-secondary);
  font-size: 15px;
}

.posts-hero-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.hero-stat {
  padding: 18px;
  border-radius: var(--radius-md);
  background: rgba(255,255,255,0.6);
  border: 1px solid var(--c-glass-border);
}

.hero-stat-num {
  font-family: var(--font-display);
  font-size: 32px;
  font-weight: 700;
  color: var(--c-text);
}

.hero-stat-label {
  margin-top: 4px;
  color: var(--c-text-secondary);
  font-size: 13px;
}

.feed-shell {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 280px;
  gap: 20px;
  align-items: start;
}

.feed-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.post-card {
  padding: 20px;
}

.post-card-top {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 16px;
}

.post-author,
.detail-author {
  display: flex;
  align-items: center;
  gap: 12px;
}

.post-avatar {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--c-accent), #7dd3fc);
  color: var(--c-ink);
  font-family: var(--font-display);
  font-weight: 700;
  box-shadow: 0 12px 24px rgba(14, 165, 233, 0.18);
}

.post-avatar.small {
  width: 36px;
  height: 36px;
  border-radius: 12px;
}

.post-author h3,
.detail-author h3 {
  font-size: 15px;
  font-weight: 700;
}

.post-author p,
.detail-author p {
  font-size: 12px;
  color: var(--c-text-muted);
  margin-top: 2px;
}

.post-body h2 {
  font-family: var(--font-display);
  font-size: 24px;
  line-height: 1.1;
  margin-bottom: 12px;
}

.post-content,
.detail-content,
.side-panel p,
.comments-shell p {
  color: var(--c-text-secondary);
  line-height: 1.75;
}

.training-chip-grid,
.detail-training-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 16px;
}

.training-chip {
  display: inline-flex;
  align-items: center;
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(14, 165, 233, 0.10);
  border: 1px solid rgba(14, 165, 233, 0.16);
  color: var(--c-text);
  font-size: 12px;
}

.post-actions {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
  padding-top: 16px;
  margin-top: 16px;
  border-top: 1px solid var(--c-glass-border);
}

.post-meta,
.detail-stats {
  display: flex;
  gap: 14px;
  color: var(--c-text-secondary);
  font-size: 13px;
}

.post-action-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.pagination-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
}

.feed-side {
  display: flex;
  flex-direction: column;
  gap: 14px;
  position: sticky;
  top: 88px;
}

.side-panel,
.checkin-preview,
.comments-shell,
.detail-training {
  padding: 16px;
}

.side-panel-title {
  font-family: var(--font-display);
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 1px;
  text-transform: uppercase;
  margin-bottom: 10px;
  color: var(--c-text);
}

.side-panel ul {
  padding-left: 18px;
  color: var(--c-text-secondary);
  line-height: 1.8;
}

.post-modal {
  max-width: 720px;
}

.detail-modal {
  max-width: 760px;
}

.detail-title {
  font-family: var(--font-display);
  font-size: 32px;
  line-height: 1.05;
  margin: 18px 0 12px;
}

.detail-training {
  margin: 18px 0;
}

.toggle-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 14px;
  color: var(--c-text-secondary);
}

.checkin-preview-title {
  font-family: var(--font-display);
  font-weight: 700;
  margin-bottom: 10px;
}

.checkin-preview-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  color: var(--c-text-secondary);
  font-size: 12px;
}

.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}

.comment-form {
  margin-bottom: 18px;
}

.comment-form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 10px;
}

.comment-thread {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 14px;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 14px;
  border-radius: var(--radius-md);
  background: rgba(255,255,255,0.45);
  border: 1px solid var(--c-glass-border);
}

.comment-reply {
  margin-left: 30px;
  background: rgba(255,255,255,0.25);
}

.comment-body {
  flex: 1;
  min-width: 0;
}

.comment-topline {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
  margin-bottom: 6px;
}

.comment-topline strong {
  font-size: 14px;
}

.comment-time {
  margin-left: 8px;
  color: var(--c-text-muted);
  font-size: 12px;
}

.comment-text {
  color: var(--c-text-secondary);
  line-height: 1.6;
  word-break: break-word;
}

.comment-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 10px;
}

@media (max-width: 960px) {
  .posts-hero,
  .feed-shell {
    grid-template-columns: 1fr;
  }

  .feed-side {
    position: static;
  }
}

@media (max-width: 640px) {
  .posts-hero {
    padding: 20px;
  }

  .posts-hero-copy h1,
  .detail-title {
    font-size: 28px;
  }

  .post-card-top,
  .post-actions,
  .pagination-bar {
    flex-direction: column;
    align-items: flex-start;
  }

  .comment-reply {
    margin-left: 12px;
  }
}
</style>
