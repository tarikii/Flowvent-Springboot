import { apiRequest } from './apiClients'
import type { PageResponse } from '../types/page'
import type { Ticket, TicketCreateRequest } from '../types/ticket'

export function buyTicket(request: TicketCreateRequest) {
  return apiRequest<Ticket>('/tickets', {
    method: 'POST',
    body: JSON.stringify(request),
  })
}

export function getMyTickets(page = 0, size = 6) {
  return apiRequest<PageResponse<Ticket>>(
    `/tickets/me?page=${page}&size=${size}&sort=purchaseDate,desc`,
  )
}