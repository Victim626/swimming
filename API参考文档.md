# 游泳教练辅助平台 - API 参考文档

**基础路径：** `http://localhost:8081/mySpringBoot`

**统一响应格式：**

```json
{
  "code": 200,        // 200=成功, 500=错误
  "message": "成功",
  "data": null        // 具体数据
}
```

所有接口需要 session 登录，未登录返回 500 + "未登录"。

---

## 目录

1. [用户认证](#1-用户认证)
2. [用户资料](#2-用户资料)
3. [教练资质审核](#3-教练资质审核)
4. [训练计划](#4-训练计划)
5. [学员训练](#5-学员训练)
6. [预约管理](#6-预约管理)
7. [即时通讯](#7-即时通讯)
8. [帖子社区](#8-帖子社区)
9. [WebSocket 聊天](#9-websocket-聊天)

---

## 1. 用户认证

### 登录

```
POST /login
Content-Type: application/x-www-form-urlencoded

参数: username, password
响应: Result<User>
```

### 注册

```
POST /register
Content-Type: application/x-www-form-urlencoded

参数:
  username    - 用户名
  password    - 密码（至少6位）
  role        - COACH 或 STUDENT
  coachRealName      - 教练真实姓名（role=COACH时必填）
  coachPhone         - 教练手机号
  coachEmail         - 教练邮箱
  studentRealName    - 学员真实姓名（role=STUDENT时必填）
  studentPhone       - 学员手机号
  studentEmail       - 学员邮箱
  gender             - MALE / FEMALE / OTHER
  age                - 年龄

响应: Result<String>
```

### 退出登录

```
POST /logout
响应: 重定向到 /
```

### 页面路由（返回 Vue SPA）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/` | 登录页 / 首页 |
| GET | `/register` | 注册页 |
| GET | `/dashboard` | 仪表板（需登录） |
| GET | `/my-appointments` | 我的预约（学员） |
| GET | `/chat` | 聊天页 |

---

## 2. 用户资料

### 获取当前用户完整资料

```
GET /api/profile/me
响应: Result<Map> 包含 user + profile（CoachProfile 或 StudentProfile）
```

### 更新基本信息

```
POST /api/profile/update-user
Content-Type: application/json

{
  "username": "新用户名",
  "password": "新密码"
}
响应: Result<String>
```

### 更新教练资料

```
POST /api/profile/update-coach
Content-Type: application/json

{
  "realName": "姓名",
  "phone": "手机号",
  "email": "邮箱",
  "avatar": "头像URL",
  "specialization": "专长",
  "introduction": "简介"
}
响应: Result<String>
```

### 更新学员资料

```
POST /api/profile/update-student
Content-Type: application/json

{
  "realName": "姓名",
  "phone": "手机号",
  "email": "邮箱",
  "avatar": "头像URL",
  "gender": "MALE/FEMALE/OTHER",
  "age": 18
}
响应: Result<String>
```

---

## 3. 教练资质审核

### 获取审核状态

```
GET /api/coach/verification/status
响应: Result<Map> 包含 qualificationVerified 等
```

### 提交教练资质审核

```
POST /api/coach/verification/submit
Content-Type: application/x-www-form-urlencoded

参数: realName, idCardNumber, certificateNumber
响应: Result<String>
```

### 获取所有审核记录（管理员）

```
GET /api/admin/verification/list
响应: Result<List<Map>> 所有教练的审核记录
```

### 审核通过

```
POST /api/admin/verification/approve
Content-Type: application/json

{"id": 审核记录ID}
响应: Result<String>
```

### 审核驳回

```
POST /api/admin/verification/reject
Content-Type: application/json

{"id": 审核记录ID}
响应: Result<String>
```

---

## 4. 训练计划

### 获取关联学员列表（教练）

```
GET /api/training-plan/students
响应: Result<List<CoachStudentRelation>>
```

### 获取所有学员（添加关联用）

```
GET /api/training-plan/all-students
响应: Result<List<Map>>
```

### 添加教练-学员关联

```
POST /api/training-plan/relation?studentId=1
响应: Result<String>
```

### 获取学员的训练计划列表

```
GET /api/training-plan/list?studentId=1
响应: Result<List<TrainingPlan>>
```

### 获取计划详情（含每日明细）

```
GET /api/training-plan/detail?id=1
响应: Result<Map> 包含 plan + details[]
```

### 获取单日训练详情

```
GET /api/training-plan/detail-detail?id=1
响应: Result<PlanDetail>
```

### 创建训练计划

```
POST /api/training-plan/create
Content-Type: application/json

{
  "studentId": 1,
  "planName": "游泳基础训练",
  "description": "两周基础训练",
  "startDate": "2024-01-15",
  "days": 5,
  "status": "ACTIVE",
  "dayDetails": [
    {
      "dayNumber": 1,
      "trainingDate": "2024-01-15",
      "trainingItem": "自由泳",
      "duration": 60,
      "distance": 2000,
      "intensity": "低",
      "notes": "热身为主"
    }
  ]
}
响应: Result<String>
```

天数范围：5-14 天。

### 更新训练计划

```
POST /api/training-plan/update
Content-Type: application/x-www-form-urlencoded

参数: id, planName, description, startDate, days, status
响应: Result<String>
```

### 删除训练计划

```
POST /api/training-plan/delete?id=1
响应: Result<String>
```

### 更新单日训练明细

```
POST /api/training-plan/update-detail
Content-Type: application/json

{
  "id": 1,
  "intensity": "高",
  "notes": "加大强度",
  "isChecked": true
}
响应: Result<Long> (planId)
```

### 打卡（切换某天的完成状态）

```
POST /api/training-plan/check/{detailId}?isChecked=true
响应: Result<String>
```

### 获取教练所有计划（仪表板用）

```
GET /api/training-plan/my-plans
响应: Result<List<TrainingPlan>>
```

### 获取教练学员列表（仪表板用）

```
GET /api/coach/students
响应: Result<List<CoachStudentRelation>>
```

---

## 5. 学员训练

### 获取今日训练计划

```
GET /api/student/today-plans
响应: Result<List<Map>> 当前学员今天的训练计划
```

### 获取所有训练计划

```
GET /api/student/all-plans
响应: Result<List<TrainingPlan>>
```

### 获取计划详情

```
GET /api/student/plan-detail?id=1
响应: Result<Map> 包含 plan + details[]
```

### 打卡

```
POST /api/student/check/{detailId}?isChecked=true
响应: Result<String>
```

### 获取关联教练

```
GET /api/student/my-coach
响应: Result<User>
```

### 获取我的计划（仪表板用）

```
GET /api/student/my-plans
响应: Result<List<TrainingPlan>>
```

### 获取指定日期的任务（仪表板用）

```
GET /api/student/today-tasks?date=2024-01-15
响应: Result<List<PlanDetail>>
```

---

## 6. 预约管理

### 获取所有时间段

```
GET /api/time-slots
响应: Result<List<TimeSlot>>
```

时间段预置：早上(08:30-10:00)、下午(15:30-17:00)、晚上(19:30-21:00)

### 学员预约

```
POST /api/appointment/book
Content-Type: application/json

{
  "coachId": 1,
  "appointmentDate": "2024-01-20",
  "startTime": "08:30",
  "endTime": "10:00",
  "location": "游泳馆A",
  "notes": "请多指导蛙泳"
}
响应: Result<String>
```

### 教练确认预约

```
POST /api/appointment/confirm
Content-Type: application/json

{"id": 预约ID}
响应: Result<String>
```

### 取消预约

```
POST /api/appointment/cancel
Content-Type: application/json

{"id": 预约ID}
响应: Result<String>
```

### 关闭时间段（教练设置不可约）

```
POST /api/appointment/slot/close
Content-Type: application/json

{
  "appointmentDate": "2024-01-20",
  "startTime": "08:30",
  "endTime": "10:00"
}
响应: Result<String>
```

### 删除预约记录

```
POST /api/appointment/delete?id=1
响应: Result<String>
```

### 查询预约（按教练+日期）

```
GET /api/appointments/coach/date?date=2024-01-20
响应: Result<List<Appointment>>
```

### 查询预约（学员按教练+日期）

```
GET /api/appointments/student?coachId=1&date=2024-01-20
响应: Result<List<Appointment>>
```

### 学员所有预约

```
GET /api/student/appointments
响应: Result<List<Appointment>>
```

### 教练所有预约

```
GET /api/appointments/coach
响应: Result<List<Appointment>>
```

### 今日预约（教练仪表板）

```
GET /api/appointment/today?date=2024-01-20
响应: Result<List<Appointment>>
```

### 清理过期预约

```
POST /api/appointment/clean-expired
响应: Result<String>
```

---

## 7. 即时通讯

### 获取可聊天用户列表

```
GET /chat/users
响应: Result<List<User>>
学员看到关联教练，教练看到关联学员
```

### 发送消息

```
POST /chat/send
Content-Type: application/json

{
  "senderId": 1,
  "receiverId": 2,
  "content": "你好！",
  "messageType": "TEXT"
}
响应: Result<Void>
```

### 获取聊天历史

```
GET /chat/history/{receiverId}
响应: Result<List<ChatMessage>>
```

### 获取联系人列表

```
GET /chat/contacts
响应: Result<List<Long>> (用户ID列表)
```

### 获取未读消息数

```
GET /chat/unread
响应: Result<Map> { "count": 3 }
```

---

## 8. 帖子社区

### 获取打卡数据列表（发帖时关联用）

```
GET /posts/checkin-data
响应: Result<List<Map>>
```

### 创建帖子

```
POST /posts
Content-Type: application/json

{
  "title": "今日训练心得",
  "content": "今天感觉不错",
  "planDetailId": 1,
  "showTrainingInfo": true
}
响应: Result<Post>
```

### 更新帖子

```
PUT /posts/{id}
Content-Type: application/json

{
  "title": "新标题",
  "content": "新内容",
  "planDetailId": null,
  "showTrainingInfo": false
}
响应: Result<Post>
```

### 删除帖子

```
DELETE /posts/{id}
响应: Result<Void>
```

### 获取帖子列表（分页）

```
GET /posts?page=1&pageSize=10
响应: Result<Map> 包含 list, total, page, pageSize, totalPages
```

### 获取帖子详情

```
GET /posts/{id}
响应: Result<Post>
```

### 点赞帖子

```
POST /posts/{id}/like
响应: Result<Void>
```

### 取消点赞

```
DELETE /posts/{id}/like
响应: Result<Void>
```

### 获取评论列表（树形结构）

```
GET /posts/{postId}/comments
响应: Result<List<Comment>> (子评论嵌套在 replies 字段中)
```

### 发表评论

```
POST /posts/{postId}/comments
Content-Type: application/json

{
  "content": "评论内容",
  "parentId": null
}
响应: Result<Comment>
```

parentId 为 null 时是根评论，有值时是回复指定评论。

### 更新评论

```
PUT /comments/{commentId}
Content-Type: application/json

{"content": "修改后的内容"}
响应: Result<Comment>
```

### 删除评论（软删除）

```
DELETE /comments/{commentId}
响应: Result<Void>
```

### 评论点赞

```
POST /comments/{commentId}/like
响应: Result<Void>
```

### 取消评论点赞

```
DELETE /comments/{commentId}/like
响应: Result<Void>
```

---

## 9. WebSocket 聊天

**端点：** `/ws`（STOMP 协议）

### 连接配置

```
WebSocket: ws://localhost:8081/mySpringBoot/ws
SockJS:   http://localhost:8081/mySpringBoot/ws
```

STOMP 端点前缀：`/app`

### 发送消息

```
STOMP 目标: /app/chat.sendMessage
Payload:
{
  "senderId": 1,
  "receiverId": 2,
  "content": "你好",
  "messageType": "TEXT"
}
消息会自动推送到接收者的订阅队列
```

### 用户上线

```
STOMP 目标: /app/chat.addUser
Payload:
{
  "senderId": 1,
  "content": "上线了"
}
```

### 订阅接收消息

```
STOMP 目标: /user/queue/messages
```

接收到的消息会自动存入数据库。

### 订阅主题前缀

```
STOMP: /topic
用户队列: /user/queue/*
```

### 前端连接示例（STOMP.js）

```javascript
const socket = new SockJS('/mySpringBoot/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, () => {
  stompClient.subscribe('/user/queue/messages', (msg) => {
    console.log(JSON.parse(msg.body));
  });
  stompClient.send('/app/chat.sendMessage', {}, JSON.stringify({
    senderId: 1, receiverId: 2, content: '你好'
  }));
});
```

---

## 数据库实体参考

| 实体 | 对应表 | 关键字段 |
|------|--------|----------|
| User | users | id, username, password, realName, phone, email, avatar, role(COACH/STUDENT), status |
| CoachProfile | coach_profiles | id, userId, realName, phone, email, certificateNumber, qualificationVerified |
| StudentProfile | student_profiles | id, userId, realName, phone, email, gender, age |
| TrainingPlan | training_plans | id, coachId, studentId, planName, description, startDate, endDate, status(DRAFT/ACTIVE/COMPLETED/CANCELLED) |
| PlanDetail | plan_details | id, planId, dayNumber, trainingDate, trainingItem, duration, distance, intensity, isChecked |
| Appointment | appointments | id, coachId, studentId, appointmentDate, startTime, endTime, status(PENDING/CONFIRMED/COMPLETED/CANCELLED) |
| TimeSlot | time_slots | id, slotName, startTime, endTime, maxCapacity |
| ChatMessage | chat_messages | id, senderId, receiverId, content, messageType, isRead, sendTime |
| Post | posts | id, userId, title, content, likeCount, commentCount, planDetailId, showTrainingInfo |
| Comment | post_comments | id, postId, userId, content, parentId, likeCount |
| CoachStudentRelation | coach_student_relations | id, coachId, studentId, status |
| CoachShare | coach_shares | id, coachId, title, content, images, isPublic |
| StudentShare | student_shares | id, studentId, title, content, images, planId, isPublic |
