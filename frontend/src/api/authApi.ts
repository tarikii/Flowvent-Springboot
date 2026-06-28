import { apiRequest } from './apiClients'
import type { AuthResponse, AuthUser, LoginRequest, RegisterRequest } from '../types/auth'

export function login(request: LoginRequest) {
  return apiRequest<AuthResponse>('/auth/login', {
    method: 'POST',
    body: JSON.stringify(request),
  })
}

export function register(request: RegisterRequest) {
  return apiRequest<AuthResponse>('/auth/register', {
    method: 'POST',
    body: JSON.stringify(request),
  })
}

export function getAuthenticatedUser() {
  return apiRequest<AuthUser>('/auth/me')
}