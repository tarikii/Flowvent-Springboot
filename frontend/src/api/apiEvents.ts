import { apiRequest } from './apiClient'
import type { Event } from '../types/event'
import type { PageResponse } from '../types/page'

export function getEvents(page = 0, size = 6) {
  return apiRequest<PageResponse<Event>>(
    `/events?page=${page}&size=${size}&sort=date,asc`,
  )
}