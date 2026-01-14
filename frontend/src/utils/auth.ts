import type { UserInfo, LoginResponse } from '../types'

const TOKEN_KEY = 'accounting_token'
const USER_KEY = 'accounting_user'

export const auth = {
  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY)
  },

  setToken(token: string): void {
    localStorage.setItem(TOKEN_KEY, token)
  },

  removeToken(): void {
    localStorage.removeItem(TOKEN_KEY)
  },

  getUser(): UserInfo | null {
    const userStr = localStorage.getItem(USER_KEY)
    if (userStr) {
      try {
        return JSON.parse(userStr)
      } catch {
        return null
      }
    }
    return null
  },

  setUser(user: LoginResponse): void {
    localStorage.setItem(USER_KEY, JSON.stringify(user))
  },

  removeUser(): void {
    localStorage.removeItem(USER_KEY)
  },

  logout(): void {
    this.removeToken()
    this.removeUser()
  },

  isAuthenticated(): boolean {
    return !!this.getToken()
  },

  isLeader(): boolean {
    const user = this.getUser()
    return user?.role === 'LEADER' || user?.role === '领导'
  }
}
