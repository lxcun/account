export interface Transaction {
  id?: number
  type: 'INCOME' | 'EXPENSE'
  amount: number
  category: string
  description?: string
  transactionDate: string
  assetId?: number
  liabilityId?: number
  accountChangeType?: 'ASSET_INCREASE' | 'ASSET_DECREASE' | 'LIABILITY_INCREASE' | 'LIABILITY_ONLY_INCREASE' | 'LIABILITY_DECREASE'
  createTime?: string
  updateTime?: string
}

export interface Summary {
  totalIncome: number
  totalExpense: number
  balance: number
}

export interface Asset {
  id?: number
  name: string
  type: 'CASH' | 'BANK' | 'ALIPAY' | 'WECHAT' | 'STOCK' | 'FUND' | 'OTHER'
  balance: number
  description?: string
  createTime?: string
  updateTime?: string
}

export interface Liability {
  id?: number
  name: string
  type: 'CREDIT_CARD' | 'MORTGAGE' | 'CAR_LOAN' | 'PERSONAL_LOAN' | 'OTHER'
  balance: number
  description?: string
  createTime?: string
  updateTime?: string
}

export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  token: string
  username: string
  realName: string
  role: string
}

export interface UserInfo {
  id?: number
  username: string
  realName: string
  role: string
}

export interface OperationLog {
  id?: number
  userId: number
  username: string
  realName?: string
  operationType: 'CREATE' | 'UPDATE' | 'DELETE' | 'OTHER'
  module: 'ASSET' | 'LIABILITY' | 'TRANSACTION' | 'OTHER'
  operationName: string
  description?: string
  targetId?: number
  targetName?: string
  requestData?: string
  resultData?: string
  ip?: string
  status: 'SUCCESS' | 'FAILED'
  errorMsg?: string
  operationTime: string
}

export interface OperationLogQuery {
  userId?: number
  module?: string
  operationType?: string
  startTime?: string
  endTime?: string
  limit?: number
}
