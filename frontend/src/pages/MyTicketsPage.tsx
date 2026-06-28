import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getMyTickets } from '../api/apiTickets'
import { useAuth } from '../context/AuthContext'
import type { PageResponse } from '../types/page'
import type { Ticket } from '../types/ticket'

export function MyTicketsPage() {
  const { isAuthenticated, loading: authLoading } = useAuth()

  const [ticketPage, setTicketPage] = useState<PageResponse<Ticket> | null>(
    null,
  )
  const [page, setPage] = useState(0)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    async function loadTickets() {
      if (authLoading) return

      if (!isAuthenticated) {
        setLoading(false)
        return
      }

      setLoading(true)
      setError('')

      try {
        const response = await getMyTickets(page)
        setTicketPage(response)
      } catch {
        setError('Could not load your tickets.')
      } finally {
        setLoading(false)
      }
    }

    loadTickets()
  }, [authLoading, isAuthenticated, page])

  if (authLoading || loading) {
    return (
      <main className="page">
        <p className="statusText">Loading your tickets...</p>
      </main>
    )
  }

  if (!isAuthenticated) {
    return (
      <main className="page">
        <section className="panel">
          <p className="eyebrow">My Tickets</p>
          <h1>Login required</h1>
          <p>You need to login before viewing your tickets.</p>

          <Link className="button" to="/login">
            Login
          </Link>
        </section>
      </main>
    )
  }

  const tickets = ticketPage?.content ?? []

  return (
    <main className="page">
      <section className="eventsHeader">
        <p className="eyebrow">My Tickets</p>
        <h1>Your purchased tickets</h1>
        <p>Review your upcoming event tickets and seat numbers.</p>
      </section>

      {error && <p className="error">{error}</p>}

      {!error && tickets.length === 0 && (
        <section className="panel">
          <h2>No tickets yet</h2>
          <p>You have not purchased any tickets yet.</p>

          <Link className="button" to="/events">
            Browse events
          </Link>
        </section>
      )}

      {!error && tickets.length > 0 && (
        <>
          <section className="ticketsGrid">
            {tickets.map((ticket) => (
              <article className="ticketCard" key={ticket.id}>
                <div>
                  <p className="eventDate">{formatDate(ticket.eventDate)}</p>
                  <h2>{ticket.eventTitle}</h2>
                </div>

                <div className="ticketInfo">
                  <div>
                    <span>Seat</span>
                    <strong>{ticket.seat}</strong>
                  </div>

                  <div>
                    <span>Price</span>
                    <strong>{formatPrice(ticket.ticketPrice)}</strong>
                  </div>

                  <div>
                    <span>Purchased</span>
                    <strong>{formatDateTime(ticket.purchaseDate)}</strong>
                  </div>
                </div>
              </article>
            ))}
          </section>

          {ticketPage && ticketPage.totalPages > 1 && (
            <div className="pagination">
              <button
                className="button secondary"
                type="button"
                disabled={ticketPage.first}
                onClick={() => setPage((currentPage) => currentPage - 1)}
              >
                Previous
              </button>

              <span>
                Page {ticketPage.number + 1} of {ticketPage.totalPages}
              </span>

              <button
                className="button secondary"
                type="button"
                disabled={ticketPage.last}
                onClick={() => setPage((currentPage) => currentPage + 1)}
              >
                Next
              </button>
            </div>
          )}
        </>
      )}
    </main>
  )
}

function formatDate(date: string) {
  return new Intl.DateTimeFormat('en', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
  }).format(new Date(date))
}

function formatDateTime(date: string) {
  return new Intl.DateTimeFormat('en', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(date))
}

function formatPrice(price: number) {
  return new Intl.NumberFormat('en', {
    style: 'currency',
    currency: 'EUR',
  }).format(price)
}