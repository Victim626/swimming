import axios from 'axios'

const BASE = '/mySpringBoot'

const request = axios.create({
  baseURL: BASE,
  timeout: 15000,
  withCredentials: true,
  headers: { 'Content-Type': 'application/json' }
})

// 允许 form-data 提交
request.interceptors.request.use(config => {
  if (config.data instanceof FormData) {
    config.headers['Content-Type'] = 'multipart/form-data'
  }
  return config
})

request.interceptors.response.use(
  res => res.data,
  err => {
    const msg = err.response?.data?.message || err.message || '网络错误'
    return { code: 500, message: msg, data: null }
  }
)

export default request
export { BASE }
