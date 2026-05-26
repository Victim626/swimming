import { createRouter, createWebHashHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('../views/Register.vue') },
  { path: '/', redirect: '/login' },
  { path: '/dashboard', name: 'Dashboard', component: () => import('../views/Dashboard.vue'), meta: { requiresAuth: true } },
  { path: '/training-plan', name: 'TrainingPlan', component: () => import('../views/TrainingPlan.vue'), meta: { requiresAuth: true, role: 'COACH' } },
  { path: '/student-training', name: 'StudentTraining', component: () => import('../views/StudentTraining.vue'), meta: { requiresAuth: true, role: 'STUDENT' } },
  { path: '/appointment', name: 'Appointment', component: () => import('../views/Appointment.vue'), meta: { requiresAuth: true, role: 'STUDENT' } },
  { path: '/appointment-manage', name: 'AppointmentManage', component: () => import('../views/AppointmentManage.vue'), meta: { requiresAuth: true, role: 'COACH' } },
  { path: '/my-appointments', name: 'StudentAppointments', component: () => import('../views/StudentAppointments.vue'), meta: { requiresAuth: true, role: 'STUDENT' } },
  { path: '/chat', name: 'Chat', component: () => import('../views/Chat.vue'), meta: { requiresAuth: true } },
  { path: '/verification', name: 'Verification', component: () => import('../views/Verification.vue'), meta: { requiresAuth: true, role: 'COACH' } },
  { path: '/admin-verification', name: 'AdminVerification', component: () => import('../views/AdminVerification.vue'), meta: { requiresAuth: true } },
  { path: '/profile', name: 'Profile', component: () => import('../views/Profile.vue'), meta: { requiresAuth: true } },
  { path: '/posts', name: 'Posts', component: () => import('../views/Posts.vue'), meta: { requiresAuth: true } },
]

const router = createRouter({ history: createWebHashHistory(), routes })

router.beforeEach((to, _from, next) => {
  const store = useUserStore()
  if (to.meta.requiresAuth && !store.isLoggedIn) {
    next('/login'); return
  }
  if (to.meta.role && store.user?.role !== to.meta.role) {
    next('/dashboard'); return
  }
  // 教练强制认证检查（仅对需要认证的教练功能页面生效）
  if (to.meta.role === 'COACH' && store.user?.role === 'COACH') {
    const coachProfile = store.user?.coachProfile
    // 如果没有coachProfile数据，先放行（可能是旧数据），由页面API调用时再验证
    if (coachProfile && coachProfile.verificationStatus !== 'APPROVED') {
      // 如果不是去认证页面本身，就跳转过去
      if (to.path !== '/verification') {
        next('/verification'); return
      }
    }
  }
  next()
})

export default router
