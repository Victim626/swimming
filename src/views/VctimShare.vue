<template>
  <div class="share-page">
    <div class="share-bg">
      <span v-for="b in bubbles" :key="b.id" class="share-bubble" :style="b.style"></span>
    </div>

    <main class="share-card glass">
      <div class="hero-badge">Vctim个人作品分享</div>
      <h1 class="hero-title">我的第一个作品</h1>
      <p class="hero-desc">
        这是一个可直接通过链接访问的个人作品展示页，用来介绍我的项目、能力和作品方向。
      </p>

      <section class="intro-grid">
        <article class="info-panel">
          <h2>项目亮点</h2>
          <ul>
            <li>公网可直接打开</li>
            <li>适合作为第一个作品展示</li>
            <li>可跳转到主项目继续使用</li>
          </ul>
        </article>

        <article class="info-panel">
          <h2>适合展示什么</h2>
          <ul>
            <li>个人简介</li>
            <li>项目截图与功能点</li>
            <li>联系方式或社交链接</li>
          </ul>
        </article>
      </section>

      <section class="project-showcase">
        <div class="showcase-card">
          <span class="showcase-tag">作品 01</span>
          <h3>游泳教练辅助平台</h3>
          <p>支持登录、教练学员管理、训练计划和预约等功能，适合做项目展示入口。</p>
        </div>
        <div class="showcase-card">
          <span class="showcase-tag">作品 02</span>
          <h3>后续可扩展内容</h3>
          <p>你可以继续补充更多项目、演示视频、图片轮播和个人技能介绍。</p>
        </div>
      </section>

      <section class="share-link-panel">
        <div>
          <div class="panel-label">公开访问链接</div>
          <code class="share-link">{{ shareUrl }}</code>
        </div>
        <button class="copy-btn" type="button" @click="copyShareLink">{{ copied ? '已复制' : '复制链接' }}</button>
      </section>

      <div class="actions">
        <RouterLink class="primary-btn" to="/login">进入项目</RouterLink>
        <button class="ghost-btn" type="button" @click="scrollToTop">返回顶部</button>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { RouterLink } from 'vue-router'

const bubbles = Array.from({ length: 8 }, (_, i) => ({
  id: i,
  style: {
    left: `${6 + i * 11}%`,
    width: `${8 + (i % 4) * 6}px`,
    height: `${8 + (i % 4) * 6}px`,
    animationDelay: `${i * 1.4}s`,
    animationDuration: `${12 + i * 2}s`
  }
}))

const copied = ref(false)
const shareUrl = computed(() => `${window.location.origin}/#/vctim-share`)

async function copyShareLink() {
  await navigator.clipboard.writeText(shareUrl.value)
  copied.value = true
  window.setTimeout(() => { copied.value = false }, 1800)
}

function scrollToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}
</script>

<style scoped>
.share-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 28px 18px;
  position: relative;
  overflow: hidden;
}

.share-bg {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(circle at 20% 20%, rgba(34, 211, 238, 0.12), transparent 28%),
    radial-gradient(circle at 80% 30%, rgba(59, 130, 246, 0.10), transparent 26%),
    radial-gradient(circle at 50% 80%, rgba(251, 191, 36, 0.08), transparent 24%);
  pointer-events: none;
}

.share-bubble {
  position: absolute;
  bottom: -40px;
  border-radius: 999px;
  border: 1px solid rgba(34, 211, 238, 0.14);
  background: rgba(34, 211, 238, 0.04);
  animation: floatUp linear infinite;
}

@keyframes floatUp {
  0% { transform: translateY(0) scale(0.8); opacity: 0; }
  20% { opacity: 0.6; }
  100% { transform: translateY(-120vh) scale(1.2); opacity: 0; }
}

.share-card {
  position: relative;
  width: min(960px, 100%);
  padding: 42px;
  z-index: 1;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  padding: 8px 14px;
  border-radius: 999px;
  background: rgba(34, 211, 238, 0.10);
  border: 1px solid rgba(34, 211, 238, 0.18);
  color: var(--c-accent);
  font-size: 13px;
  letter-spacing: 0.5px;
  margin-bottom: 18px;
}

.hero-title {
  font-family: var(--font-display);
  font-size: clamp(34px, 5vw, 58px);
  font-weight: 800;
  line-height: 1.05;
  margin-bottom: 14px;
}

.hero-desc {
  max-width: 680px;
  color: var(--c-text-secondary);
  font-size: 16px;
  line-height: 1.8;
  margin-bottom: 30px;
}

.intro-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
  margin-bottom: 18px;
}

.info-panel,
.showcase-card {
  border-radius: 22px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(8, 18, 30, 0.55);
  backdrop-filter: blur(18px);
  padding: 22px;
}

.info-panel h2,
.showcase-card h3 {
  font-size: 18px;
  margin-bottom: 12px;
}

.info-panel ul {
  padding-left: 18px;
  color: var(--c-text-secondary);
  line-height: 1.9;
}

.project-showcase {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
  margin-top: 8px;
}

.showcase-tag {
  display: inline-flex;
  margin-bottom: 12px;
  color: var(--c-accent);
  font-size: 13px;
}

.showcase-card p {
  color: var(--c-text-secondary);
  line-height: 1.7;
}

.share-link-panel {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-top: 28px;
  padding: 18px 20px;
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.10);
  background: rgba(255, 255, 255, 0.04);
  flex-wrap: wrap;
}

.panel-label {
  font-size: 13px;
  color: var(--c-text-secondary);
  margin-bottom: 8px;
}

.share-link {
  display: block;
  max-width: 100%;
  padding: 10px 12px;
  border-radius: 12px;
  background: rgba(0, 0, 0, 0.22);
  color: var(--c-text);
  overflow-wrap: anywhere;
}

.copy-btn,
.primary-btn,
.ghost-btn {
  min-width: 140px;
  padding: 14px 20px;
  border-radius: 16px;
  text-align: center;
  text-decoration: none;
  transition: transform .2s ease, border-color .2s ease, background .2s ease;
}

.copy-btn {
  border: 0;
  color: var(--c-deep-1);
  background: linear-gradient(135deg, var(--c-accent), #38bdf8);
  font-weight: 700;
}

.primary-btn {
  color: var(--c-deep-1);
  background: linear-gradient(135deg, var(--c-accent), #38bdf8);
  font-weight: 700;
}

.ghost-btn {
  border: 1px solid rgba(255, 255, 255, 0.14);
  color: var(--c-text);
  background: rgba(255, 255, 255, 0.04);
}

.copy-btn:hover,
.primary-btn:hover,
.ghost-btn:hover {
  transform: translateY(-2px);
}

@media (max-width: 760px) {
  .share-card {
    padding: 28px 20px;
  }

  .intro-grid,
  .project-showcase {
    grid-template-columns: 1fr;
  }
}
</style>
