import {
  createContext,
  useContext,
  useEffect,
  useState,
  type ReactNode,
} from 'react'
import { getAuthenticatedUser, login as loginRequest, register as registerRequest } from '../api/authApi'
import type { AuthUser, LoginRequest, RegisterRequest } from '../types/auth'

interface AuthContextValue {
  user: AuthUser | null
  loading: boolean
  isAuthenticated: boolean
  login: (request: LoginRequest) => Promise<void>
  register: (request: RegisterRequest) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined)

const TOKEN_KEY = 'flowvent_token'

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    async function loadAuthenticatedUser() {
      const token = localStorage.getItem(TOKEN_KEY)

      if (!token) {
        setLoading(false)
        return
      }

      try {
        const authenticatedUser = await getAuthenticatedUser()
        setUser(authenticatedUser)
      } catch {
        localStorage.removeItem(TOKEN_KEY)
        setUser(null)
      } finally {
        setLoading(false)
      }
    }

    loadAuthenticatedUser()
  }, [])

  async function login(request: LoginRequest) {
    const authResponse = await loginRequest(request)

    localStorage.setItem(TOKEN_KEY, authResponse.token)

    const authenticatedUser = await getAuthenticatedUser()
    setUser(authenticatedUser)
  }

  async function register(request: RegisterRequest) {
    const authResponse = await registerRequest(request)

    localStorage.setItem(TOKEN_KEY, authResponse.token)

    const authenticatedUser = await getAuthenticatedUser()
    setUser(authenticatedUser)
  }

  function logout() {
    localStorage.removeItem(TOKEN_KEY)
    setUser(null)
  }

  return (
    <AuthContext.Provider
      value={{
        user,
        loading,
        isAuthenticated: Boolean(user),
        login,
        register,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)

  if (!context) {
    throw new Error('useAuth must be used inside AuthProvider')
  }

  return context
}