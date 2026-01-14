import axios from 'axios'
import { message } from 'antd'
import type { ApiResponse, Transaction, Summary, Asset, Liability, LoginRequest, LoginResponse, OperationLog, OperationLogQuery } from '../types'
import { auth } from '../utils/auth'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

api.interceptors.request.use(
  (config) => {
    const token = auth.getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

api.interceptors.response.use(
  (response) => {
    if (response.data.code !== 200) {
      message.error(response.data.message || '请求失败')
      return Promise.reject(response.data)
    }
    return response.data
  },
  (error) => {
    if (error.response?.status === 401) {
      auth.logout()
      window.location.href = '/login'
    }
    message.error(error.response?.data?.message || error.message || '网络错误')
    return Promise.reject(error)
  }
)

export const authApi = {
  login: (data: LoginRequest) =>
    api.post<any, ApiResponse<LoginResponse>>('/auth/login', data)
}

export const transactionApi = {
  create: (data: Transaction) => 
    api.post<any, ApiResponse<Transaction>>('/transactions', data),
    
  update: (id: number, data: Transaction) => 
    api.put<any, ApiResponse<Transaction>>(`/transactions/${id}`, data),
    
  delete: (id: number) => 
    api.delete<any, ApiResponse<void>>(`/transactions/${id}`),
    
  getAll: () => 
    api.get<any, ApiResponse<Transaction[]>>('/transactions'),
    
  getByDateRange: (startDate: string, endDate: string) => 
    api.get<any, ApiResponse<Transaction[]>>('/transactions/range', {
      params: { startDate, endDate }
    }),
    
  getSummary: (startDate: string, endDate: string) => 
    api.get<any, ApiResponse<Summary>>('/transactions/summary', {
      params: { startDate, endDate }
    })
}

export const assetApi = {
  create: (data: Asset) => 
    api.post<any, ApiResponse<Asset>>('/assets', data),
    
  update: (id: number, data: Asset) => 
    api.put<any, ApiResponse<Asset>>(`/assets/${id}`, data),
    
  delete: (id: number) => 
    api.delete<any, ApiResponse<void>>(`/assets/${id}`),
    
  getAll: () => 
    api.get<any, ApiResponse<Asset[]>>('/assets'),
    
  getSummary: () => 
    api.get<any, ApiResponse<{ totalAssets: number }>>('/assets/summary')
}

export const liabilityApi = {
  create: (data: Liability) =>
    api.post<any, ApiResponse<Liability>>('/liabilities', data),

  update: (id: number, data: Liability) =>
    api.put<any, ApiResponse<Liability>>(`/liabilities/${id}`, data),

  delete: (id: number) =>
    api.delete<any, ApiResponse<void>>(`/liabilities/${id}`),

  getAll: () =>
    api.get<any, ApiResponse<Liability[]>>('/liabilities'),

  getSummary: () =>
    api.get<any, ApiResponse<{ totalLiabilities: number }>>('/liabilities/summary')
}

export const operationLogApi = {
  getById: (id: number) =>
    api.get<any, ApiResponse<OperationLog>>(`/operation-logs/${id}`),

  getRecent: (limit: number = 100) =>
    api.get<any, ApiResponse<OperationLog[]>>('/operation-logs/recent', {
      params: { limit }
    }),

  getByUserId: (userId: number) =>
    api.get<any, ApiResponse<OperationLog[]>>(`/operation-logs/user/${userId}`),

  getByModule: (module: string) =>
    api.get<any, ApiResponse<OperationLog[]>>(`/operation-logs/module/${module}`),

  getByOperationType: (operationType: string) =>
    api.get<any, ApiResponse<OperationLog[]>>(`/operation-logs/operation-type/${operationType}`),

  getByTimeRange: (startTime: string, endTime: string) =>
    api.get<any, ApiResponse<OperationLog[]>>('/operation-logs/time-range', {
      params: { startTime, endTime }
    }),

  getByUserIdAndModule: (userId: number, module: string) =>
    api.get<any, ApiResponse<OperationLog[]>>(`/operation-logs/user/${userId}/module/${module}`),

  getByUserIdAndOperationType: (userId: number, operationType: string) =>
    api.get<any, ApiResponse<OperationLog[]>>(`/operation-logs/user/${userId}/operation-type/${operationType}`),

  getTotalCount: () =>
    api.get<any, ApiResponse<number>>('/operation-logs/total-count'),

  getCountByUserId: (userId: number) =>
    api.get<any, ApiResponse<number>>(`/operation-logs/user/${userId}/count`),

  query: (query: OperationLogQuery) =>
    api.post<any, ApiResponse<OperationLog[]>>('/operation-logs/query', query)
}

export default api
