import { type FormEvent, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { ApiError } from '../api/apiClients'
import { useAuth } from '../context/AuthContext'

export function RegisterPage() {
  const [username, setUsername] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const { register, user } = useAuth()
  const navigate = useNavigate()

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')

    if (username.trim().length < 3) {
      setError('Username must contain at least 3 characters.')
      return
    }

    if (!email.trim()) {
      setError('Email is required.')
      return
    }

    if (password.length < 8) {
      setError('Password must contain at least 8 characters.')
      return
    }

    setSubmitting(true)

    try {
      await register({
        username: username.trim(),
        email: email.trim(),
        password,
      })

      navigate('/events')
    } catch (registerError) {
      if (registerError instanceof ApiError) {
        setError(getRegisterErrorMessage(registerError))
      } else {
        setError('Could not create your account. Please try again.')
      }
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <main className="page">
      <section className="panel formPanel">
        <p className="eyebrow">Create account</p>
        <h1>Register</h1>

        {user && (
          <div className="successBox">
            <strong>You are already logged in as {user.username}</strong>
            <span>{user.email}</span>
            <span>Role: {user.role}</span>
          </div>
        )}

        {!user && (
          <>
            <form className="form" onSubmit={handleSubmit}>
              <label>
                Username
                <input
                  type="text"
                  value={username}
                  onChange={(event) => setUsername(event.target.value)}
                  placeholder="tarik"
                />
              </label>

              <label>
                Email
                <input
                  type="email"
                  value={email}
                  onChange={(event) => setEmail(event.target.value)}
                  placeholder="tarik@flowvent.com"
                />
              </label>

              <label>
                Password
                <input
                  type="password"
                  value={password}
                  onChange={(event) => setPassword(event.target.value)}
                  placeholder="At least 8 characters"
                />
              </label>

              {error && <p className="error">{error}</p>}

              <button className="button" type="submit" disabled={submitting}>
                {submitting ? 'Creating account...' : 'Create account'}
              </button>
            </form>

            <p className="formHint">
              Already have an account? <Link to="/login">Login</Link>
            </p>
          </>
        )}
      </section>
    </main>
  )
}

function getRegisterErrorMessage(error: ApiError) {
  const message = error.message.toLowerCase()

  if (message.includes('email') && message.includes('already')) {
    return 'This email is already registered.'
  }

  if (message.includes('email')) {
    return 'Please enter a valid email address.'
  }

  if (message.includes('password')) {
    return 'Password must contain at least 8 characters.'
  }

  if (message.includes('username')) {
    return 'Username must contain between 3 and 50 characters.'
  }

  return 'Could not create your account. Please try again.'
}