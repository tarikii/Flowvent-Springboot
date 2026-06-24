import { Link } from 'react-router-dom'

export function Navbar() {
  return (
    <header className="navbar">
      <Link to="/" className="logo">
        Flowvent
      </Link>

      <nav>
        <Link to="/events">Events</Link>
        <Link to="/my-tickets">My Tickets</Link>
        <Link to="/login">Login</Link>
        <Link to="/register" className="navButton">
          Register
        </Link>
      </nav>
    </header>
  )
}