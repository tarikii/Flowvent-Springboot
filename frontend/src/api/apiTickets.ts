import { apiRequest } from './apiClients'
import type { Ticket, TicketCreateRequest } from '../types/ticket'

export function buyTicket(request: TicketCreateRequest) {
  return apiRequest<Ticket>('/tickets', {
    method: 'POST',
    body: JSON.stringify(request),
  })
}