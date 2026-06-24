import { Link } from 'react-router-dom'

export function HomePage() {
  return (
    <main className="page">
      <section className="hero">
        <p className="eyebrow">Flowvent</p>

        <h1>Discover events and manage your tickets</h1>

        <p className="heroText">
          A full-stack event management platform built with Spring Boot,
          PostgreSQL, JWT authentication and React.
        </p>

        <div className="actions">
          <Link to="/events" className="button">
            Browse events
          </Link>

          <Link to="/login" className="button secondary">
            Login
          </Link>
        </div>
      </section>
    </main>
  )
}