import { type FormEvent, useEffect, useState } from 'react'
import { ApiError } from '../api/apiClients'
import { createEvent, getEvents, updateEvent, deleteEvent } from '../api/apiEvents'
import type { Event, EventCreateRequest } from '../types/event'

export function AdminEventsPage() {
  const [events, setEvents] = useState<Event[]>([])
  const [selectedEventId, setSelectedEventId] = useState<number | null>(null)

  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')
  const [date, setDate] = useState('')
  const [maximumCapacity, setMaximumCapacity] = useState('')
  const [ticketPrice, setTicketPrice] = useState('')

  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const [deletingId, setDeletingId] = useState<number | null>(null)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  const isEditing = selectedEventId !== null

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

    const request: EventCreateRequest = {
      title: title.trim(),
      description: description.trim(),
      date,
      maximumCapacity: parsedCapacity,
      ticketPrice: parsedPrice,
    }

    setSubmitting(true)

    try {
      if (isEditing && selectedEventId !== null) {
        const updatedEvent = await updateEvent(selectedEventId, request)

        setEvents((currentEvents) =>
          currentEvents.map((currentEvent) =>
            currentEvent.id === updatedEvent.id ? updatedEvent : currentEvent,
          ),
        )

        setSuccess('Event updated successfully.')
      } else {
        const createdEvent = await createEvent(request)

        setEvents((currentEvents) => [createdEvent, ...currentEvents])
        setSuccess('Event created successfully.')
      }

      clearForm()
    } catch (eventError) {
      if (eventError instanceof ApiError) {
        setError(getAdminEventErrorMessage(eventError))
      } else {
        setError('Could not save event. Please try again.')
      }
    } finally {
      setSubmitting(false)
    }
  }

  function startEditing(eventToEdit: Event) {
    setSelectedEventId(eventToEdit.id)
    setTitle(eventToEdit.title)
    setDescription(eventToEdit.description)
    setDate(eventToEdit.date)
    setMaximumCapacity(String(eventToEdit.maximumCapacity))
    setTicketPrice(String(eventToEdit.ticketPrice))
    setError('')
    setSuccess('')
  }

  function cancelEditing() {
    clearForm()
    setError('')
    setSuccess('')
  }

  async function handleDelete(eventToDelete: Event) {
    const confirmed = window.confirm(
      `Are you sure you want to delete "${eventToDelete.title}"?`,
    )

    if (!confirmed) return

    setError('')
    setSuccess('')
    setDeletingId(eventToDelete.id)

    try {
      await deleteEvent(eventToDelete.id)

      setEvents((currentEvents) =>
        currentEvents.filter((currentEvent) => currentEvent.id !== eventToDelete.id),
      )

      if (selectedEventId === eventToDelete.id) {
        clearForm()
      }

      setSuccess('Event deleted successfully.')
    } catch (deleteError) {
      if (deleteError instanceof ApiError) {
        setError(getDeleteEventErrorMessage(deleteError))
      } else {
        setError('Could not delete event. Please try again.')
      }
    } finally {
      setDeletingId(null)
    }
  }

  function clearForm() {
    setSelectedEventId(null)
    setTitle('')
    setDescription('')
    setDate('')
    setMaximumCapacity('')
    setTicketPrice('')
  }

  return (
    <main className="page">
      <section className="eventsHeader">
        <p className="eyebrow">Admin</p>
        <h1>Manage events</h1>
        <p>Create, edit and delete events that clients can discover and book.</p>
      </section>

      <section className="adminLayout">
        <section className="panel formPanel">
          <p className="eyebrow">{isEditing ? 'Edit event' : 'New event'}</p>

          <div className="formTitleRow">
            <h2>{isEditing ? 'Edit event' : 'Create event'}</h2>

            {isEditing && (
              <button
                className="button secondary"
                type="button"
                onClick={cancelEditing}
              >
                Cancel
              </button>
            )}
          </div>

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
              {submitting
                ? isEditing
                  ? 'Saving...'
                  : 'Creating...'
                : isEditing
                  ? 'Save changes'
                  : 'Create event'}
            </button>
          </form>
        </section>

        <section className="adminEventsList">
          <h2>Latest events</h2>

          {loading && <p className="statusText">Loading events...</p>}

          {!loading && events.length === 0 && (
            <section className="panel">
              <p>No events found.</p>
            </section>
          )}

          {!loading &&
            events.map((adminEvent) => (
              <article className="adminEventItem" key={adminEvent.id}>
                <div>
                  <p className="eventDate">{formatDate(adminEvent.date)}</p>
                  <h3>{adminEvent.title}</h3>
                  <span>
                    {adminEvent.availableTickets} left · {adminEvent.soldTickets}{' '}
                    sold
                  </span>
                </div>

                <div className="adminEventActions">
                  <strong>{formatPrice(adminEvent.ticketPrice)}</strong>

                  <div className="adminItemActions">
                    <button
                      className="button secondary"
                      type="button"
                      onClick={() => startEditing(adminEvent)}
                    >
                      Edit
                    </button>

                    <button
                      className="dangerButton"
                      type="button"
                      disabled={deletingId === adminEvent.id}
                      onClick={() => handleDelete(adminEvent)}
                    >
                      {deletingId === adminEvent.id ? 'Deleting...' : 'Delete'}
                    </button>
                  </div>
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
    return 'Only administrators can manage events.'
  }

  return 'Could not save event. Please check the form and try again.'
}

function getDeleteEventErrorMessage(error: ApiError) {
  if (error.status === 403) {
    return 'Only administrators can delete events.'
  }

  if (error.status === 409 || error.status === 500) {
    return 'Could not delete this event. It may already have tickets linked to it.'
  }

  return 'Could not delete event. Please try again.'
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