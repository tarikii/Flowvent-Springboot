import { useEffect, useState } from 'react'
import { getEvents } from '../api/apiEvents'
import type { Event } from '../types/event'

export function EventsPage() {
  const [events, setEvents] = useState<Event[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    async function loadEvents() {
      try {
        const response = await getEvents()
        setEvents(response.content)
      } catch {
        setError('Could not load events')
      } finally {
        setLoading(false)
      }
    }

    loadEvents()
  }, [])

  return (
    <main className="page">
      <section className="eventsHeader">
        <p className="eyebrow">Events</p>
        <h1>Discover upcoming experiences</h1>
        <p>
          Browse available events, check ticket availability and choose your
          next experience.
        </p>
      </section>

      {loading && <p className="statusText">Loading events...</p>}

      {error && <p className="error">{error}</p>}

      {!loading && !error && events.length === 0 && (
        <section className="panel">
          <h2>No events found</h2>
          <p>There are no events available right now.</p>
        </section>
      )}

      {!loading && !error && events.length > 0 && (
        <section className="eventsGrid">
          {events.map((event) => (
            <article className="eventCard" key={event.id}>
              <div>
                <p className="eventDate">{formatDate(event.date)}</p>
                <h2>{event.title}</h2>
                <p>{event.description}</p>
              </div>

              <div className="eventDetails">
                <span>{event.availableTickets} tickets left</span>
                <span>{event.soldTickets} sold</span>
              </div>

              <div className="eventFooter">
                <strong>{formatPrice(event.ticketPrice)}</strong>
                <button className="button" type="button">
                  View event
                </button>
              </div>
            </article>
          ))}
        </section>
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

function formatPrice(price: number) {
  return new Intl.NumberFormat('en', {
    style: 'currency',
    currency: 'EUR',
  }).format(price)
}