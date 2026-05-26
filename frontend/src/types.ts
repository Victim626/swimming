export interface User {
  id: number; username: string; password?: string; realName: string
  phone?: string; email?: string; avatar?: string; role: string
  status?: number; createdAt?: string; updatedAt?: string
  coachProfile?: CoachProfile
}
export interface CoachProfile {
  id?: number; userId?: number; realName?: string; phone?: string; email?: string
  certificateNumber?: string; idCardNumber?: string; qualificationVerified?: number
  verificationStatus?: string // PENDING, APPROVED, REJECTED
}
export interface StudentProfile {
  id?: number; userId?: number; realName?: string; phone?: string; email?: string
  gender?: string; age?: number
}
export interface TrainingPlan {
  id: number; coachId: number; studentId: number; planName: string
  description?: string; startDate?: string; endDate?: string; status?: string
  aiSuggestions?: string; createdAt?: string; updatedAt?: string
}
export interface PlanDetail {
  id: number; planId: number; dayNumber: number; trainingDate?: string
  trainingItem?: string; duration?: number; distance?: number; intensity?: string
  notes?: string; isChecked?: boolean; sortOrder?: number
}
export interface Appointment {
  id: number; coachId: number; studentId: number; appointmentDate: string
  startTime: string; endTime: string; location?: string; status: string
  notes?: string; coachName?: string; studentName?: string; timeSlotName?: string
}
export interface TimeSlot {
  id: number; slotName: string; startTime: string; endTime: string
  maxCapacity: number; sortOrder?: number; isActive?: boolean
}
export interface ChatMessage {
  id?: number; senderId: number; receiverId: number; content: string
  messageType?: string; isRead?: boolean; sendTime?: string
  senderName?: string; receiverName?: string
}
export interface CoachStudentRelation {
  id?: number; coachId?: number; studentId: number; relationshipType?: string
  startDate?: string; status?: number; studentUsername?: string
  studentRealName?: string; studentPhone?: string; studentGender?: string; studentAge?: number
}

export interface PostCheckinData {
  id: number
  planName: string
  dayNumber: number
  trainingDate?: string
  intensity?: string
  duration?: number
  distance?: number
  isChecked?: boolean
}

export interface PostItem {
  id: number
  userId: number
  title: string
  content: string
  planDetailId?: number | null
  dayNumber?: number | null
  planName?: string | null
  trainingDate?: string | null
  intensity?: string | null
  duration?: number | null
  distance?: number | null
  showTrainingInfo?: boolean
  likeCount?: number
  commentCount?: number
  likedByCurrentUser?: boolean
  createdAt?: string
  updatedAt?: string
  username?: string
  realName?: string
  avatar?: string
}

export interface CommentItem {
  id: number
  postId: number
  userId: number
  parentId?: number | null
  content: string
  likeCount?: number
  likedByCurrentUser?: boolean
  deleted?: boolean
  createdAt?: string
  updatedAt?: string
  username?: string
  realName?: string
  avatar?: string
  replies?: CommentItem[]
}

export interface PostDetail extends PostItem {}

export interface PostFormData {
  title: string
  content: string
  planDetailId?: number | null
  showTrainingInfo?: boolean
}

export interface PostCommentFormData {
  content: string
  parentId?: number | null
}

export interface PaginationResult<T> {
  list: T[]
  total: number
  page: number
  pageSize: number
  totalPages: number
}
