import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export function HomePage() {
  const { isAuthenticated, user } = useAuth()

  return (
    <main className="page homePage">
      <section className="homeHero">
        <div className="homeHeroContent">
          <p className="eyebrow">Event ticketing platform</p>

          <h1>Discover events, book tickets and manage experiences.</h1>

          <p className="heroText">
            Flowvent is a full-stack event management app where clients can
            browse events, buy tickets and track their purchases, while admins
            can manage the event catalog.
          </p>

          <div className="actions">
            <Link className="button" to="/events">
              Browse events
            </Link>

            {!isAuthenticated && (
              <Link className="button secondary" to="/register">
                Create account
              </Link>
            )}

            {user?.role === 'ADMIN' && (
              <Link className="button secondary" to="/admin/events">
                Admin panel
              </Link>
            )}
          </div>
        </div>

        <aside className="heroPreview">
          <div className="previewCard mainPreviewCard">
            <span className="previewBadge">Live event</span>
            <h2>Muse Live Experience</h2>
            <p>Jun 10, 2030</p>

            <div className="previewStats">
              <div>
                <strong>€69.99</strong>
                <span>Ticket price</span>
              </div>

              <div>
                <strong>128</strong>
                <span>Tickets left</span>
              </div>
            </div>
          </div>

          <div className="previewCard smallPreviewCard">
            <strong>Ticket purchased</strong>
            <span>Seat 24 · Client booking confirmed</span>
          </div>
        </aside>
      </section>

      <section className="homeStats">
        <div>
          <strong>JWT Auth</strong>
          <span>Secure login and protected routes</span>
        </div>

        <div>
          <strong>Spring Boot API</strong>
          <span>REST backend with validation and business rules</span>
        </div>

        <div>
          <strong>React Frontend</strong>
          <span>Modern client experience with real API integration</span>
        </div>
      </section>

      <section className="featuresSection">
        <div className="sectionHeader">
          <p className="eyebrow">Features</p>
          <h2>Built like a real product</h2>
          <p>
            Flowvent includes authentication, role-based access, event search,
            ticket purchasing and an admin management area.
          </p>
        </div>

        <div className="featuresGrid">
          <article className="featureCard">
            <span>01</span>
            <h3>Browse and search events</h3>
            <p>
              Clients can explore available events, filter by title or date and
              view detailed event information.
            </p>
          </article>

          <article className="featureCard">
            <span>02</span>
            <h3>Buy tickets securely</h3>
            <p>
              Ticket purchases are validated by the backend, preventing duplicate
              seats, full events and expired events.
            </p>
          </article>

          <article className="featureCard">
            <span>03</span>
            <h3>Manage personal tickets</h3>
            <p>
              Authenticated clients can review their purchased tickets, seats,
              prices and purchase dates.
            </p>
          </article>

          <article className="featureCard">
            <span>04</span>
            <h3>Admin event control</h3>
            <p>
              Admins can create, edit and delete events, with protection against
              deleting events that already have sold tickets.
            </p>
          </article>
        </div>
      </section>

      <section className="ctaSection">
        <div>
          <p className="eyebrow">Ready to explore?</p>
          <h2>Start with the event catalog.</h2>
          <p>
            Browse the available events and test the complete ticket purchase
            flow from the frontend.
          </p>
        </div>

        <Link className="button" to="/events">
          View events
        </Link>
      </section>
    </main>
  )
}