import { type FormEvent, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export function LoginPage() {
  const [email, setEmail] = useState('client@flowvent.com')
  const [password, setPassword] = useState('client123')
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const { login, user } = useAuth()
  const navigate = useNavigate()

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')
    setSubmitting(true)

    try {
      await login({ email, password })
      navigate('/events')
    } catch {
      setError('Invalid email or password')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <main className="page">
      <section className="panel formPanel">
        <p className="eyebrow">Welcome back</p>
        <h1>Login</h1>

        {user && (
          <div className="successBox">
            <strong>You are already logged in as {user.username}</strong>
            <span>{user.email}</span>
            <span>Role: {user.role}</span>
          </div>
        )}

        {!user && (
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
                placeholder="client123"
              />
            </label>

            {error && <p className="error">{error}</p>}

            <button className="button" type="submit" disabled={submitting}>
              {submitting ? 'Logging in...' : 'Login'}
            </button>
          </form>
        )}
      </section>
    </main>
  )
}
