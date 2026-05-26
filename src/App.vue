<template>
  <div class="app-shell">
    <div class="deep-bg"></div>
    <div class="light-rays"></div>
    <div class="bubble" style="width:6px;height:6px;left:5%;animation-duration:18s;animation-delay:0s;"></div>
    <div class="bubble" style="width:10px;height:10px;left:20%;animation-duration:24s;animation-delay:3s;"></div>
    <div class="bubble" style="width:4px;height:4px;left:40%;animation-duration:16s;animation-delay:6s;"></div>
    <div class="bubble" style="width:8px;height:8px;left:60%;animation-duration:22s;animation-delay:2s;"></div>
    <div class="bubble" style="width:5px;height:5px;left:80%;animation-duration:20s;animation-delay:8s;"></div>
    <div class="bubble" style="width:12px;height:12px;left:90%;animation-duration:28s;animation-delay:4s;"></div>
    <router-view v-slot="{ Component, route }">
      <transition name="page-fade" mode="out-in">
        <component :is="Component" :key="route.path" />
      </transition>
    </router-view>
  </div>
</template>

<style>
.page-fade-enter-active, .page-fade-leave-active {
  transition: opacity 0.25s ease, transform 0.25s ease;
}
.page-fade-enter-from {
  opacity: 0;
  transform: translateY(8px);
}
.page-fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
</style>
