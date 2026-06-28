export interface Event {
  id: number
  title: string
  description: string
  date: string
  maximumCapacity: number
  ticketPrice: number
  soldTickets: number
  availableTickets: number
}

export interface EventCreateRequest {
  title: string
  description: string
  date: string
  maximumCapacity: number
  ticketPrice: number
}

export type EventUpdateRequest = EventCreateRequest