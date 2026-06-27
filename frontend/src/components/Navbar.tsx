import { Link, NavLink } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export function Navbar() {
  const { user, isAuthenticated, logout } = useAuth()

  return (
    <header className="navbar">
      <Link to="/" className="logo">
        Flowvent
      </Link>

      <nav>
        <NavLink to="/events">Events</NavLink>

        {isAuthenticated && <NavLink to="/my-tickets">My Tickets</NavLink>}

        {!isAuthenticated && <NavLink to="/login">Login</NavLink>}

        {!isAuthenticated && (
          <NavLink to="/register" className="navButton">
            Register
          </NavLink>
        )}

        {user && (
          <div className="userNav">
            <span>
              {user.username} · {user.role}
            </span>

            <button type="button" className="logoutButton" onClick={logout}>
              Logout
            </button>
          </div>
        )}
      </nav>
    </header>
  )
}