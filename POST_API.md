# 发帖模块后端接口文档

## 数据库表结构

### 1. posts 表（帖子表）
```sql
CREATE TABLE posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '发帖用户ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '内容',
    
    -- 关联的打卡数据（可选）
    plan_detail_id BIGINT NULL COMMENT '关联的训练详情ID',
    day_number INT NULL COMMENT '打卡第几天',
    plan_name VARCHAR(200) NULL COMMENT '训练计划名称',
    training_date DATE NULL COMMENT '训练日期',
    intensity VARCHAR(50) NULL COMMENT '强度等级',
    duration INT NULL COMMENT '时长（分钟）',
    distance INT NULL COMMENT '距离（米）',
    
    -- 显示控制
    show_training_info BOOLEAN DEFAULT FALSE COMMENT '是否显示训练信息',
    
    -- 统计信息
    like_count INT DEFAULT 0 COMMENT '点赞数',
    comment_count INT DEFAULT 0 COMMENT '评论数',
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (plan_detail_id) REFERENCES plan_details(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子表';
```

### 2. post_comments 表（评论表）
```sql
CREATE TABLE post_comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    user_id BIGINT NOT NULL COMMENT '评论用户ID',
    content TEXT NOT NULL COMMENT '评论内容',
    parent_id BIGINT NULL COMMENT '父评论ID（回复）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_post_id (post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子评论表';
```

---

## 接口列表

### 1. 获取打卡数据列表（发帖时可选关联）

**接口地址：** `GET /posts/checkin-data`

**功能说明：** 获取当前学员的所有打卡记录，用于发帖时选择关联

**请求参数：** 无

**响应示例：**
```json
{
  "code": 200,
  "message": "成功",
  "data": [
    {
      "id": 1,
      "planName": "游泳基础训练",
      "dayNumber": 1,
      "trainingDate": "2024-01-15",
      "intensity": "中等",
      "duration": 60,
      "distance": 2000,
      "isChecked": true
    },
    {
      "id": 2,
      "planName": "游泳基础训练",
      "dayNumber": 2,
      "trainingDate": "2024-01-16",
      "intensity": "高",
      "duration": 90,
      "distance": 3000,
      "isChecked": false
    }
  ]
}
```

---

### 2. 创建帖子

**接口地址：** `POST /posts`

**功能说明：** 发布新帖子，可选择关联打卡数据

**请求头：** `Content-Type: application/json`

**请求体：**
```json
{
  "title": "今日训练心得",
  "content": "今天训练感觉不错，完成了所有计划内容，蛙泳的蹬腿动作越来越标准了！",
  "planDetailId": 1,
  "showTrainingInfo": true
}
```

**参数说明：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| title | String | 是 | 帖子标题，最长200字符 |
| content | String | 是 | 帖子内容/心得 |
| planDetailId | Number | 否 | 关联的打卡记录ID，提供后会自动填充训练数据 |
| showTrainingInfo | Boolean | 否 | 是否显示训练信息，默认false |

**响应示例：**
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "id": 1,
    "userId": 10,
    "title": "今日训练心得",
    "content": "今天训练感觉不错，完成了所有计划内容，蛙泳的蹬腿动作越来越标准了！",
    "planDetailId": 1,
    "dayNumber": 1,
    "planName": "游泳基础训练",
    "trainingDate": "2024-01-15",
    "intensity": "中等",
    "duration": 60,
    "distance": 2000,
    "showTrainingInfo": true,
    "likeCount": 0,
    "commentCount": 0,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00",
    "username": "zhangsan",
    "realName": "张三",
    "avatar": "/uploads/avatar.jpg"
  }
}
```

---

### 3. 更新帖子

**接口地址：** `PUT /posts/{id}`

**功能说明：** 修改已发布的帖子

**请求头：** `Content-Type: application/json`

**请求体：**
```json
{
  "title": "修改后的标题",
  "content": "修改后的内容",
  "planDetailId": 1,
  "showTrainingInfo": true
}
```

**参数说明：** 同创建帖子，`planDetailId` 传 `null` 可取消关联

**响应示例：** 同创建帖子

---

### 4. 删除帖子

**接口地址：** `DELETE /posts/{id}`

**功能说明：** 删除指定帖子（仅作者可操作）

**请求参数：**
| 参数 | 位置 | 类型 | 必填 | 说明 |
|------|------|------|------|------|
| id | path | Number | 是 | 帖子ID |

**响应示例：**
```json
{
  "code": 200,
  "message": "成功",
  "data": null
}
```

---

### 5. 获取帖子列表（分页）

**接口地址：** `GET /posts`

**功能说明：** 获取所有帖子列表，按创建时间倒序

**请求参数：**
| 参数 | 位置 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|------|--------|------|
| page | query | Number | 否 | 1 | 页码 |
| pageSize | query | Number | 否 | 10 | 每页数量 |

**请求示例：** `GET /posts?page=1&pageSize=10`

**响应示例：**
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "list": [
      {
        "id": 1,
        "userId": 10,
        "title": "今日训练心得",
        "content": "今天训练感觉不错...",
        "planDetailId": 1,
        "dayNumber": 1,
        "planName": "游泳基础训练",
        "trainingDate": "2024-01-15",
        "intensity": "中等",
        "duration": 60,
        "distance": 2000,
        "showTrainingInfo": true,
        "likeCount": 5,
        "commentCount": 2,
        "createdAt": "2024-01-15T10:30:00",
        "updatedAt": "2024-01-15T10:30:00",
        "username": "zhangsan",
        "realName": "张三",
        "avatar": "/uploads/avatar.jpg"
      }
    ],
    "total": 25,
    "page": 1,
    "pageSize": 10,
    "totalPages": 3
  }
}
```

**字段说明：**
- `list`: 帖子数组
- `total`: 总记录数
- `page`: 当前页码
- `pageSize`: 每页数量
- `totalPages`: 总页数

---

### 6. 获取帖子详情

**接口地址：** `GET /posts/{id}`

**功能说明：** 获取指定帖子的详细信息

**请求参数：**
| 参数 | 位置 | 类型 | 必填 | 说明 |
|------|------|------|------|------|
| id | path | Number | 是 | 帖子ID |

**响应示例：**
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "id": 1,
    "userId": 10,
    "title": "今日训练心得",
    "content": "今天训练感觉不错，完成了所有计划内容，蛙泳的蹬腿动作越来越标准了！",
    "planDetailId": 1,
    "dayNumber": 1,
    "planName": "游泳基础训练",
    "trainingDate": "2024-01-15",
    "intensity": "中等",
    "duration": 60,
    "distance": 2000,
    "showTrainingInfo": true,
    "likeCount": 5,
    "commentCount": 2,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00",
    "username": "zhangsan",
    "realName": "张三",
    "avatar": "/uploads/avatar.jpg"
  }
}
```

---

### 7. 点赞帖子

**接口地址：** `POST /posts/{id}/like`

**功能说明：** 对指定帖子点赞

**请求参数：**
| 参数 | 位置 | 类型 | 必填 | 说明 |
|------|------|------|------|------|
| id | path | Number | 是 | 帖子ID |

**响应示例：**
```json
{
  "code": 200,
  "message": "成功",
  "data": null
}
```

---

### 8. 取消点赞

**接口地址：** `DELETE /posts/{id}/like`

**功能说明：** 取消对指定帖子的点赞

**请求参数：**
| 参数 | 位置 | 类型 | 必填 | 说明 |
|------|------|------|------|------|
| id | path | Number | 是 | 帖子ID |

**响应示例：**
```json
{
  "code": 200,
  "message": "成功",
  "data": null
}
```

---

## 错误响应格式

所有接口在发生错误时统一返回以下格式：

```json
{
  "code": 400,
  "message": "错误信息描述",
  "data": null
}
```

**常见错误码：**
- `400`: 请求参数错误
- `401`: 未登录或登录已过期
- `403`: 无权操作
- `404`: 资源不存在
- `500`: 服务器内部错误

---

## 前端实现要点

### 1. 发帖表单

**必填字段：**
- 标题（输入框，最长200字符）
- 内容/心得（多行文本框）

**可选字段：**
- 关联打卡数据（下拉选择，数据源：`GET /posts/checkin-data`）
- 是否显示训练信息（开关/复选框）

### 2. 帖子列表展示

**显示内容：**
- 标题
- 内容（可截取前N个字符）
- 作者信息（头像、用户名、真实姓名）
- 发布时间
- 点赞数、评论数

**条件显示（当 `showTrainingInfo === true` 且有关联数据时）：**
- 第几天（dayNumber）
- 训练计划名称（planName）
- 训练日期（trainingDate）
- 强度等级（intensity）
- 时长（duration，分钟）
- 距离（distance，米）

### 3. 交互功能

- ✅ 点赞/取消点赞
- 📄 分页加载
- ✏️ 编辑帖子（仅作者）
- 🗑️ 删除帖子（仅作者）
- 💬 评论功能（后续扩展）

### 4. 权限控制

- 所有接口需要登录（session中校验currentUser）
- 更新、删除操作仅限帖子作者
- 帖子列表对所有登录用户可见
