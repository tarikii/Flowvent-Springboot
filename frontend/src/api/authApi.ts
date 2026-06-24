import { apiRequest } from './apiClient'
import type { AuthResponse, AuthUser, LoginRequest } from '../types/auth'

export function login(request: LoginRequest) {
  return apiRequest<AuthResponse>('/auth/login', {
    method: 'POST',
    body: JSON.stringify(request),
  })
}

export function getAuthenticatedUser() {
  return apiRequest<AuthUser>('/auth/me')
}