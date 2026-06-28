import { apiRequest } from './apiClients'
import type { Event, EventCreateRequest, EventUpdateRequest } from '../types/event'
import type { PageResponse } from '../types/page'

interface SearchEventsParams {
  title?: string
  date?: string
  page?: number
  size?: number
}

export function getEvents(page = 0, size = 6) {
  return apiRequest<PageResponse<Event>>(
    `/events?page=${page}&size=${size}&sort=date,asc`,
  )
}

export function searchEvents({
  title,
  date,
  page = 0,
  size = 6,
}: SearchEventsParams) {
  const params = new URLSearchParams()

  params.set('page', String(page))
  params.set('size', String(size))
  params.set('sort', 'date,asc')

  if (title) {
    params.set('title', title)
  }

  if (date) {
    params.set('date', date)
  }

  return apiRequest<PageResponse<Event>>(`/events/search?${params.toString()}`)
}

export function getUpcomingEvents(page = 0, size = 6) {
  return apiRequest<PageResponse<Event>>(
    `/events/upcoming?page=${page}&size=${size}&sort=date,asc`,
  )
}

export function getEventById(id: number) {
  return apiRequest<Event>(`/events/${id}`)
}

export function createEvent(request: EventCreateRequest) {
  return apiRequest<Event>('/events', {
    method: 'POST',
    body: JSON.stringify(request),
  })
}

export function updateEvent(id: number, request: EventUpdateRequest) {
  return apiRequest<Event>(`/events/${id}`, {
    method: 'PUT',
    body: JSON.stringify(request),
  })
}

export function deleteEvent(id: number) {
  return apiRequest<void>(`/events/${id}`, {
    method: 'DELETE',
  })
}

