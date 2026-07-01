import { type FormEvent, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import {
  getEvents,
  getUpcomingEvents,
  searchEvents,
} from '../api/apiEvents'
import type { Event } from '../types/event'
import type { PageResponse } from '../types/page'
import { SkeletonCards } from '../components/SkeletonCards'

export function EventsPage() {
  const [eventPage, setEventPage] = useState<PageResponse<Event> | null>(null)
  const [page, setPage] = useState(0)

  const [titleFilter, setTitleFilter] = useState('')
  const [dateFilter, setDateFilter] = useState('')
  const [upcomingOnly, setUpcomingOnly] = useState(false)

  const [appliedTitle, setAppliedTitle] = useState('')
  const [appliedDate, setAppliedDate] = useState('')
  const [appliedUpcomingOnly, setAppliedUpcomingOnly] = useState(false)

  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    loadEvents()
  }, [page, appliedTitle, appliedDate, appliedUpcomingOnly])

  async function loadEvents() {
    setLoading(true)
    setError('')

    try {
      let response: PageResponse<Event>

      if (appliedUpcomingOnly) {
        response = await getUpcomingEvents(page)
      } else if (appliedTitle || appliedDate) {
        response = await searchEvents({
          title: appliedTitle,
          date: appliedDate,
          page,
        })
      } else {
        response = await getEvents(page)
      }

      setEventPage(response)
    } catch {
      setError('Could not load events.')
    } finally {
      setLoading(false)
    }
  }

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    setPage(0)
    setAppliedTitle(titleFilter.trim())
    setAppliedDate(dateFilter)
    setAppliedUpcomingOnly(upcomingOnly)
  }

  function clearFilters() {
    setTitleFilter('')
    setDateFilter('')
    setUpcomingOnly(false)

    setAppliedTitle('')
    setAppliedDate('')
    setAppliedUpcomingOnly(false)

    setPage(0)
  }

  const events = eventPage?.content ?? []
  const hasActiveFilters = appliedTitle || appliedDate || appliedUpcomingOnly

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

      <section className="filtersPanel">
        <form className="filtersForm" onSubmit={handleSubmit}>
          <label>
            Search by title
            <input
              type="text"
              value={titleFilter}
              onChange={(event) => setTitleFilter(event.target.value)}
              placeholder="Muse, The Strokes, Tech Conference..."
              disabled={upcomingOnly}
            />
          </label>

          <label>
            Date
            <input
              type="date"
              value={dateFilter}
              onChange={(event) => setDateFilter(event.target.value)}
              disabled={upcomingOnly}
            />
          </label>

          <label className="checkboxLabel">
            <input
              type="checkbox"
              checked={upcomingOnly}
              onChange={(event) => setUpcomingOnly(event.target.checked)}
            />
            Upcoming only
          </label>

          <div className="filtersActions">
            <button className="button" type="submit">
              Apply filters
            </button>

            {hasActiveFilters && (
              <button
                className="button secondary"
                type="button"
                onClick={clearFilters}
              >
                Clear
              </button>
            )}
          </div>
        </form>
      </section>

      {loading && <SkeletonCards count={4} />}

      {error && <p className="error">{error}</p>}

      {!loading && !error && events.length === 0 && (
        <section className="panel">
          <h2>No events found</h2>
          <p>
            {hasActiveFilters
              ? 'No events match your current filters.'
              : 'There are no events available right now.'}
          </p>

          {hasActiveFilters && (
            <button className="button secondary" type="button" onClick={clearFilters}>
              Clear filters
            </button>
          )}
        </section>
      )}

      {!loading && !error && events.length > 0 && (
        <>
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
                  <Link className="button" to={`/events/${event.id}`}>
                    View event
                  </Link>
                </div>
              </article>
            ))}
          </section>

          {eventPage && eventPage.totalPages > 1 && (
            <div className="pagination">
              <button
                className="button secondary"
                type="button"
                disabled={eventPage.first}
                onClick={() => setPage((currentPage) => currentPage - 1)}
              >
                Previous
              </button>

              <span>
                Page {eventPage.number + 1} of {eventPage.totalPages}
              </span>

              <button
                className="button secondary"
                type="button"
                disabled={eventPage.last}
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

function formatPrice(price: number) {
  return new Intl.NumberFormat('en', {
    style: 'currency',
    currency: 'EUR',
  }).format(price)
}