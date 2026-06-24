import { FormEvent, useState } from 'react'
import { getAuthenticatedUser, login } from '../api/authApi'
import type { AuthUser } from '../types/auth'

export function LoginPage() {
  const [email, setEmail] = useState('client@flowvent.com')
  const [password, setPassword] = useState('password123')
  const [user, setUser] = useState<AuthUser | null>(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')
    setLoading(true)

    try {
      const authResponse = await login({ email, password })

      localStorage.setItem('flowvent_token', authResponse.token)

      const authenticatedUser = await getAuthenticatedUser()
      setUser(authenticatedUser)
    } catch {
      setError('Invalid email or password')
      localStorage.removeItem('flowvent_token')
      setUser(null)
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="page">
      <section className="panel formPanel">
        <p className="eyebrow">Welcome back</p>
        <h1>Login</h1>

        <form className="form" onSubmit={handleSubmit}>
          <label>
            Email
            <input
              type="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              placeholder="client@flowvent.com"
            />
          </label>

          <label>
            Password
            <input
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              placeholder="password123"
            />
          </label>

          {error && <p className="error">{error}</p>}

          <button className="button" type="submit" disabled={loading}>
            {loading ? 'Logging in...' : 'Login'}
          </button>
        </form>

        {user && (
          <div className="successBox">
            <strong>Logged in as {user.username}</strong>
            <span>{user.email}</span>
            <span>Role: {user.role}</span>
          </div>
        )}
      </section>
    </main>
  )
}