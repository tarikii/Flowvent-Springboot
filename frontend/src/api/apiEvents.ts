import { apiRequest } from './apiClients'
import type { Event } from '../types/event'
import type { PageResponse } from '../types/page'

export function getEvents(page = 0, size = 6) {
  return apiRequest<PageResponse<Event>>(
    `/events?page=${page}&size=${size}&sort=date,asc`,
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