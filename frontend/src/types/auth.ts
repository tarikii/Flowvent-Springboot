export type Role = 'ADMIN' | 'CLIENT'

export interface LoginRequest {
  email: string
  password: string
}

export interface AuthResponse {
  token: string
}

export interface AuthUser {
  id: number
  username: string
  email: string
  role: Role
}