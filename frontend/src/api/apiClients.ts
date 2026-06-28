const API_BASE_URL = 'http://localhost:8081/api'

interface ApiErrorResponse {
  message?: string
  error?: string
  messages?: Record<string, string>
}

export class ApiError extends Error {
  status: number
  details?: ApiErrorResponse

  constructor(status: number, message: string, details?: ApiErrorResponse) {
    super(message)
    this.status = status
    this.details = details
  }
}

export async function apiRequest<T>(
  endpoint: string,
  options: RequestInit = {},
): Promise<T> {
  const token = localStorage.getItem('flowvent_token')

  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...options.headers,
    },
  })

  if (!response.ok) {
    let errorBody: ApiErrorResponse | undefined

    try {
      errorBody = await response.json()
    } catch {
      errorBody = undefined
    }

    const message =
      errorBody?.message ||
      errorBody?.error ||
      `Request failed with status ${response.status}`

    throw new ApiError(response.status, message, errorBody)
  }

  return response.json() as Promise<T>
}