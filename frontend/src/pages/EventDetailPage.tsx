import { type FormEvent, useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { getEventById } from '../api/apiEvents'
import { buyTicket } from '../api/apiTickets'
import { useAuth } from '../context/AuthContext'
import type { Event } from '../types/event'
import type { Ticket } from '../types/ticket'
import { ApiError } from '../api/apiClients'

export function EventDetailPage() {
  const { id } = useParams()
  const { isAuthenticated } = useAuth()

  const [event, setEvent] = useState<Event | null>(null)
  const [seatNumber, setSeatNumber] = useState('')
  const [ticket, setTicket] = useState<Ticket | null>(null)
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState('')
  const [purchaseError, setPurchaseError] = useState('')

  useEffect(() => {
    async function loadEvent() {
      if (!id) {
        setError('Event not found')
        setLoading(false)
        return
      }

      try {
        const response = await getEventById(Number(id))
        setEvent(response)
      } catch {
        setError('Could not load event')
      } finally {
        setLoading(false)
      }
    }

    loadEvent()
  }, [id])

  async function handleBuyTicket(eventForm: FormEvent<HTMLFormElement>) {
    eventForm.preventDefault()
    setPurchaseError('')
    setTicket(null)

    if (!event) return

    const parsedSeatNumber = Number(seatNumber)

    if (!parsedSeatNumber || parsedSeatNumber < 1) {
      setPurchaseError('Seat number must be at least 1')
      return
    }

    setSubmitting(true)

    try {
      const response = await buyTicket({
        eventId: event.id,
        seatNumber: parsedSeatNumber,
      })

      setTicket(response)
      setSeatNumber('')

      const refreshedEvent = await getEventById(event.id)
      setEvent(refreshedEvent)
    } catch (error) {
      if (error instanceof ApiError) {
        setPurchaseError(getPurchaseErrorMessage(error))
      } else {
        setPurchaseError('Could not buy ticket. Please try again.')
      }
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) {
    return (
      <main className="page">
        <p className="statusText">Loading event...</p>
      </main>
    )
  }

  if (error || !event) {
    return (
      <main className="page">
        <section className="panel">
          <h1>Event not found</h1>
          <p>{error}</p>
          <Link className="button" to="/events">
            Back to events
          </Link>
        </section>
      </main>
    )
  }

  return (
    <main className="page">
      <Link className="backLink" to="/events">
        ← Back to events
      </Link>

      <section className="eventDetail">
        <div>
          <p className="eventDate">{formatDate(event.date)}</p>
          <h1>{event.title}</h1>
          <p className="eventDescription">{event.description}</p>

          <div className="eventDetails detailBadges">
            <span>{event.availableTickets} tickets left</span>
            <span>{event.soldTickets} sold</span>
            <span>{event.maximumCapacity} capacity</span>
          </div>
        </div>

        <aside className="purchasePanel">
          <p className="eyebrow">Ticket</p>
          <strong className="price">{formatPrice(event.ticketPrice)}</strong>

          {!isAuthenticated && (
            <div className="infoBox">
              <p>You need to login before buying a ticket.</p>
              <Link className="button" to="/login">
                Login
              </Link>
            </div>
          )}

          {isAuthenticated && (
            <form className="form" onSubmit={handleBuyTicket}>
              <label>
                Seat number
                <input
                  type="number"
                  min="1"
                  value={seatNumber}
                  onChange={(inputEvent) => setSeatNumber(inputEvent.target.value)}
                  placeholder="12"
                />
              </label>

              {purchaseError && <p className="error">{purchaseError}</p>}

              <button className="button" type="submit" disabled={submitting}>
                {submitting ? 'Buying...' : 'Buy ticket'}
              </button>
            </form>
          )}

          {ticket && (
            <div className="successBox">
              <strong>Ticket purchased!</strong>
              <span>Seat: {ticket.seat}</span>
              <span>{ticket.eventTitle}</span>
            </div>
          )}
        </aside>
      </section>
    </main>
  )
}

function getPurchaseErrorMessage(error: ApiError) {
  const message = error.message.toLowerCase()

  if (message.includes('past event')) {
    return 'This event has already passed.'
  }

  if (message.includes('seat') && message.includes('already')) {
    return 'This seat is already taken. Please choose another one.'
  }

  if (message.includes('full')) {
    return 'This event is sold out.'
  }

  if (error.status === 403) {
    return 'You are not allowed to perform this action.'
  }

  if (error.status === 401) {
    return 'Please log in before buying a ticket.'
  }

  return 'Could not buy ticket. Please try again.'
}

function formatDate(date: string) {
  return new Intl.DateTimeFormat('en', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
  }).format(new Date(date))
}

function formatPrice(price: number) {
  return new Intl.NumberFormat('en', {
    style: 'currency',
    currency: 'EUR',
  }).format(price)
}