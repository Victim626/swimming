import request from '../utils/request'
import type { User, CoachStudentRelation, TrainingPlan, PlanDetail, Appointment, TimeSlot, ChatMessage, CoachProfile, PostCheckinData, PostDetail, PostFormData, PostItem, PaginationResult, CommentItem, PostCommentFormData } from '../types'

// ============== 登录注册 ==============
export function loginApi(username: string, password: string) {
  const fd = new FormData(); fd.append('username', username); fd.append('password', password)
  return request.post('/login', fd) as Promise<{ code: number; message: string; data: User }>
}
export function registerApi(data: Record<string, any>) {
  const fd = new FormData()
  Object.entries(data).forEach(([k, v]) => fd.append(k, String(v ?? '')))
  return request.post('/register', fd) as Promise<{ code: number; message: string; data: any }>
}
export function logoutApi() {
  return request.post('/logout') as Promise<any>
}

// ============== 教练资质认证 ==============
export function getVerificationStatus() {
  return request.get('/api/coach/verification/status') as Promise<{ code: number; message: string; data: CoachProfile }>
}
export function submitVerification(data: Record<string, string>) {
  const fd = new FormData()
  Object.entries(data).forEach(([k, v]) => fd.append(k, v))
  return request.post('/api/coach/verification/submit', fd) as Promise<any>
}
export function getVerificationList() {
  return request.get('/api/admin/verification/list') as Promise<{ code: number; message: string; data: any[] }>
}
export function approveVerification(id: number) {
  return request.post('/api/admin/verification/approve', { id }) as Promise<any>
}
export function rejectVerification(id: number) {
  return request.post('/api/admin/verification/reject', { id }) as Promise<any>
}

// ============== 训练计划（教练端） ==============
export function getCoachStudents() {
  return request.get('/api/training-plan/students') as Promise<{ code: number; message: string; data: CoachStudentRelation[] }>
}
export function getAllStudents() {
  return request.get('/api/training-plan/all-students') as Promise<{ code: number; message: string; data: any[] }>
}
export function addStudentRelation(studentId: number) {
  return request.post('/api/training-plan/relation', { studentId }, { headers: { 'Content-Type': 'application/x-www-form-urlencoded' } }) as Promise<any>
}
export function getStudentPlans(studentId: number) {
  return request.get('/api/training-plan/list', { params: { studentId } }) as Promise<{ code: number; message: string; data: TrainingPlan[] }>
}
export function getPlanDetail(planId: number) {
  return request.get('/api/training-plan/detail', { params: { id: planId } }) as Promise<{ code: number; message: string; data: { plan: TrainingPlan; details: PlanDetail[] } }>
}
export function getDayDetail(detailId: number) {
  return request.get('/api/training-plan/detail-detail', { params: { id: detailId } }) as Promise<{ code: number; message: string; data: PlanDetail }>
}
export function updateDayDetail(data: { id: number; intensity: string; notes: string; isChecked: boolean }) {
  return request.post('/api/training-plan/update-detail', data, { headers: { 'Content-Type': 'application/json' } }) as Promise<any>
}
export function toggleCheckStatus(detailId: number, isChecked: boolean) {
  return request.post(`/api/training-plan/check/${detailId}`, null, { params: { isChecked } }) as Promise<any>
}
export function createPlan(data: any) {
  return request.post('/api/training-plan/create', data, { headers: { 'Content-Type': 'application/json' } }) as Promise<any>
}
export function updatePlan(data: any) {
  const fd = new FormData()
  Object.entries(data).forEach(([k, v]) => {
    if (v !== undefined && v !== null) fd.append(k, String(v))
  })
  return request.post('/api/training-plan/update', fd, { headers: { 'Content-Type': 'multipart/form-data' } }) as Promise<any>
}
export function deletePlan(id: number) {
  return request.post('/api/training-plan/delete', { id }, { headers: { 'Content-Type': 'application/x-www-form-urlencoded' } }) as Promise<any>
}
// 已删除：toggleCoachCheck - 使用 toggleCheckStatus 代替
export function getCoachMyPlans() {
  return request.get('/api/training-plan/my-plans') as Promise<{ code: number; message: string; data: TrainingPlan[] }>
}
export function getCoachStudentsCount() {
  return request.get('/api/coach/students') as Promise<{ code: number; message: string; data: CoachStudentRelation[] }>
}

// ============== 训练计划（学员端） ==============
export function getStudentTodayPlans() {
  return request.get('/api/student/today-plans') as Promise<{ code: number; message: string; data: any[] }>
}
export function getStudentAllPlans() {
  return request.get('/api/student/all-plans') as Promise<{ code: number; message: string; data: TrainingPlan[] }>
}
export function getStudentPlanDetail(id: number) {
  return request.get('/api/student/plan-detail', { params: { id } }) as Promise<{ code: number; message: string; data: { plan: TrainingPlan; details: PlanDetail[] } }>
}
export function toggleStudentCheck(detailId: number, isChecked: boolean) {
  return request.post(`/api/student/check/${detailId}`, null, { params: { isChecked } }) as Promise<any>
}
export function getMyCoach() {
  return request.get('/api/student/my-coach') as Promise<{ code: number; message: string; data: User }>
}
export function getStudentMyPlans() {
  return request.get('/api/student/my-plans') as Promise<{ code: number; message: string; data: TrainingPlan[] }>
}
export function getStudentTodayTasks(date: string) {
  return request.get('/api/student/today-tasks', { params: { date } }) as Promise<{ code: number; message: string; data: PlanDetail[] }>
}

// ============== 预约 ==============
export function getTimeSlots() {
  return request.get('/api/time-slots') as Promise<{ code: number; message: string; data: TimeSlot[] }>
}
export function bookAppointment(data: any) {
  return request.post('/api/appointment/book', data) as Promise<any>
}
export function confirmAppointment(id: number) {
  return request.post('/api/appointment/confirm', { id }) as Promise<any>
}
export function cancelAppointment(id: number) {
  return request.post('/api/appointment/cancel', { id }) as Promise<any>
}
export function getCoachAppointmentsByDate(date: string) {
  return request.get('/api/appointments/coach/date', { params: { date } }) as Promise<{ code: number; message: string; data: Appointment[] }>
}
export function getCoachAllAppointments() {
  return request.get('/api/appointments/coach') as Promise<{ code: number; message: string; data: Appointment[] }>
}
export function getAppointmentsForStudent(coachId: number, date: string) {
  return request.get('/api/appointments/student', { params: { coachId, date } }) as Promise<{ code: number; message: string; data: Appointment[] }>
}
export function getMyAppointments() {
  return request.get('/api/student/appointments') as Promise<{ code: number; message: string; data: Appointment[] }>
}
export function closeTimeSlot(data: any) {
  return request.post('/api/appointment/slot/close', data) as Promise<any>
}
export function deleteAppointment(id: number) {
  return request.post('/api/appointment/delete', { id }, { headers: { 'Content-Type': 'application/x-www-form-urlencoded' } }) as Promise<any>
}
export function getTodayAppointments(date: string) {
  return request.get('/api/appointment/today', { params: { date } }) as Promise<{ code: number; message: string; data: Appointment[] }>
}
export function cleanExpiredAppointments() {
  return request.post('/api/appointment/clean-expired') as Promise<any>
}

// ============== 个人资料 ==============
export function getProfile() {
  return request.get('/api/profile/me') as Promise<{ code: number; message: string; data: any }>
}
export function updateUser(data: { username?: string; password?: string }) {
  return request.post('/api/profile/update-user', data) as Promise<{ code: number; message: string; data: any }>
}
export function updateCoachProfile(data: Record<string, any>) {
  return request.post('/api/profile/update-coach', data) as Promise<{ code: number; message: string; data: any }>
}
export function updateStudentProfile(data: Record<string, any>) {
  return request.post('/api/profile/update-student', data) as Promise<{ code: number; message: string; data: any }>
}

// ============== 聊天 ==============
export function getChatUsers() {
  return request.get('/chat/users') as Promise<{ code: number; message: string; data: User[] }>
}
export function sendMessage(msg: Partial<ChatMessage>) {
  return request.post('/chat/send', msg) as Promise<any>
}
export function getChatHistory(receiverId: number) {
  return request.get(`/chat/history/${receiverId}`) as Promise<{ code: number; message: string; data: ChatMessage[] }>
}
export function getChatContacts() {
  return request.get('/chat/contacts') as Promise<{ code: number; message: string; data: number[] }>
}
export function getUnreadCount() {
  return request.get('/chat/unread') as Promise<{ code: number; message: string; data: { count: number } }>
}

// ============== 发帖 ==============
export function getPostCheckinData() {
  return request.get('/posts/checkin-data') as Promise<{ code: number; message: string; data: PostCheckinData[] }>
}
export function getPostList(page = 1, pageSize = 10) {
  return request.get('/posts', { params: { page, pageSize } }) as Promise<{ code: number; message: string; data: PaginationResult<PostItem> }>
}
export function getPostDetail(id: number) {
  return request.get(`/posts/${id}`) as Promise<{ code: number; message: string; data: PostDetail }>
}
export function createPost(data: PostFormData) {
  return request.post('/posts', data) as Promise<{ code: number; message: string; data: PostDetail }>
}
export function updatePost(id: number, data: PostFormData) {
  return request.put(`/posts/${id}`, data) as Promise<{ code: number; message: string; data: PostDetail }>
}
export function deletePost(id: number) {
  return request.delete(`/posts/${id}`) as Promise<{ code: number; message: string; data: null }>
}
export function likePost(id: number) {
  return request.post(`/posts/${id}/like`) as Promise<{ code: number; message: string; data: null }>
}
export function unlikePost(id: number) {
  return request.delete(`/posts/${id}/like`) as Promise<{ code: number; message: string; data: null }>
}
export function getPostComments(postId: number) {
  return request.get(`/posts/${postId}/comments`) as Promise<{ code: number; message: string; data: CommentItem[] }>
}
export function createPostComment(postId: number, data: PostCommentFormData) {
  return request.post(`/posts/${postId}/comments`, data) as Promise<{ code: number; message: string; data: CommentItem }>
}
export function updatePostComment(commentId: number, data: PostCommentFormData) {
  return request.put(`/comments/${commentId}`, data) as Promise<{ code: number; message: string; data: CommentItem }>
}
export function deletePostComment(commentId: number) {
  return request.delete(`/comments/${commentId}`) as Promise<{ code: number; message: string; data: null }>
}
export function likeComment(commentId: number) {
  return request.post(`/comments/${commentId}/like`) as Promise<{ code: number; message: string; data: null }>
}
export function unlikeComment(commentId: number) {
  return request.delete(`/comments/${commentId}/like`) as Promise<{ code: number; message: string; data: null }>
}
