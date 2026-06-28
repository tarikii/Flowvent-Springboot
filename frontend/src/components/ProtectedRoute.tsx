import { type ReactNode } from 'react'
import { Navigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import type { Role } from '../types/auth'

interface ProtectedRouteProps {
  children: ReactNode
  allowedRoles?: Role[]
}

export function ProtectedRoute({ children, allowedRoles }: ProtectedRouteProps) {
  const { user, isAuthenticated, loading } = useAuth()
  const location = useLocation()

  if (loading) {
    return (
      <main className="page">
        <p className="statusText">Checking authentication...</p>
      </main>
    )
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace state={{ from: location }} />
  }

  if (allowedRoles && user && !allowedRoles.includes(user.role)) {
      return (
        <main className="page">
          <section className="panel">
            <p className="eyebrow">Access denied</p>
            <h1>You are not allowed to view this page</h1>
            <p>This area is only available for administrators.</p>
          </section>
        </main>
      )
    }

  return <>{children}</>
}