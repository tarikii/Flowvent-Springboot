export interface TicketCreateRequest {
  eventId: number
  seatNumber: number
}

export interface Ticket {
  id: number
  clientName: string
  clientEmail: string
  eventId: number
  eventTitle: string
  eventDate: string
  seat: number
  ticketPrice: number
  purchaseDate: string
}