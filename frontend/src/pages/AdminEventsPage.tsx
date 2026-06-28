import { type FormEvent, useEffect, useState } from 'react'
import { ApiError } from '../api/apiClients'
import { createEvent, getEvents } from '../api/apiEvents'
import type { Event } from '../types/event'

export function AdminEventsPage() {
  const [events, setEvents] = useState<Event[]>([])
  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')
  const [date, setDate] = useState('')
  const [maximumCapacity, setMaximumCapacity] = useState('')
  const [ticketPrice, setTicketPrice] = useState('')

  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  useEffect(() => {
    loadEvents()
  }, [])

  async function loadEvents() {
    try {
      const response = await getEvents(0, 10)
      setEvents(response.content)
    } catch {
      setError('Could not load events.')
    } finally {
      setLoading(false)
    }
  }

  async function handleSubmit(eventForm: FormEvent<HTMLFormElement>) {
    eventForm.preventDefault()
    setError('')
    setSuccess('')

    const parsedCapacity = Number(maximumCapacity)
    const parsedPrice = Number(ticketPrice)

    if (!title.trim()) {
      setError('Title is required.')
      return
    }

    if (!description.trim()) {
      setError('Description is required.')
      return
    }

    if (!date) {
      setError('Date is required.')
      return
    }

    if (!parsedCapacity || parsedCapacity < 1) {
      setError('Maximum capacity must be at least 1.')
      return
    }

    if (Number.isNaN(parsedPrice) || parsedPrice < 0) {
      setError('Ticket price cannot be negative.')
      return
    }

    setSubmitting(true)

    try {
      const createdEvent = await createEvent({
        title: title.trim(),
        description: description.trim(),
        date,
        maximumCapacity: parsedCapacity,
        ticketPrice: parsedPrice,
      })

      setEvents((currentEvents) => [createdEvent, ...currentEvents])
      setTitle('')
      setDescription('')
      setDate('')
      setMaximumCapacity('')
      setTicketPrice('')
      setSuccess('Event created successfully.')
    } catch (createError) {
      if (createError instanceof ApiError) {
        setError(getAdminEventErrorMessage(createError))
      } else {
        setError('Could not create event. Please try again.')
      }
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <main className="page">
      <section className="eventsHeader">
        <p className="eyebrow">Admin</p>
        <h1>Manage events</h1>
        <p>Create new events that clients can discover and book.</p>
      </section>

      <section className="adminLayout">
        <section className="panel formPanel">
          <p className="eyebrow">New event</p>
          <h2>Create event</h2>

          <form className="form" onSubmit={handleSubmit}>
            <label>
              Title
              <input
                type="text"
                value={title}
                onChange={(event) => setTitle(event.target.value)}
                placeholder="The Strokes Live"
              />
            </label>

            <label>
              Description
              <input
                type="text"
                value={description}
                onChange={(event) => setDescription(event.target.value)}
                placeholder="A live concert experience."
              />
            </label>

            <label>
              Date
              <input
                type="date"
                value={date}
                onChange={(event) => setDate(event.target.value)}
              />
            </label>

            <label>
              Maximum capacity
              <input
                type="number"
                min="1"
                value={maximumCapacity}
                onChange={(event) => setMaximumCapacity(event.target.value)}
                placeholder="100"
              />
            </label>

            <label>
              Ticket price
              <input
                type="number"
                min="0"
                step="0.01"
                value={ticketPrice}
                onChange={(event) => setTicketPrice(event.target.value)}
                placeholder="49.99"
              />
            </label>

            {error && <p className="error">{error}</p>}

            {success && (
              <div className="successBox">
                <strong>{success}</strong>
              </div>
            )}

            <button className="button" type="submit" disabled={submitting}>
              {submitting ? 'Creating...' : 'Create event'}
            </button>
          </form>
        </section>

        <section className="adminEventsList">
          <h2>Latest events</h2>

          {loading && <p className="statusText">Loading events...</p>}

          {!loading &&
            events.map((event) => (
              <article className="adminEventItem" key={event.id}>
                <div>
                  <p className="eventDate">{formatDate(event.date)}</p>
                  <h3>{event.title}</h3>
                </div>

                <div>
                  <strong>{formatPrice(event.ticketPrice)}</strong>
                  <span>{event.availableTickets} left</span>
                </div>
              </article>
            ))}
        </section>
      </section>
    </main>
  )
}

function getAdminEventErrorMessage(error: ApiError) {
  const message = error.message.toLowerCase()

  if (message.includes('future')) {
    return 'Event date must be in the future.'
  }

  if (message.includes('title')) {
    return 'Title is required.'
  }

  if (message.includes('description')) {
    return 'Description is required.'
  }

  if (message.includes('capacity')) {
    return 'Maximum capacity must be at least 1.'
  }

  if (message.includes('price')) {
    return 'Ticket price cannot be negative.'
  }

  if (error.status === 403) {
    return 'Only administrators can create events.'
  }

  return 'Could not create event. Please check the form and try again.'
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