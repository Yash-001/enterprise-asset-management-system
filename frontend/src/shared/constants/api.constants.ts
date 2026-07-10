export const API_CONSTANTS = {
  // HTTP Methods
  METHOD: {
    GET: 'GET',
    POST: 'POST',
    PUT: 'PUT',
    PATCH: 'PATCH',
    DELETE: 'DELETE'
  },

  // Headers
  HEADERS: {
    CONTENT_TYPE: 'Content-Type',
    AUTHORIZATION: 'Authorization',
    ACCEPT: 'Accept'
  },

  // Content Types
  CONTENT_TYPE: {
    JSON: 'application/json',
    FORM_DATA: 'multipart/form-data',
    URL_ENCODED: 'application/x-www-form-urlencoded'
  },

  // HTTP Status Codes
  STATUS: {
    OK: 200,
    CREATED: 201,
    NO_CONTENT: 204,
    BAD_REQUEST: 400,
    UNAUTHORIZED: 401,
    FORBIDDEN: 403,
    NOT_FOUND: 404,
    CONFLICT: 409,
    UNPROCESSABLE: 422,
    INTERNAL_ERROR: 500
  },

  // Timeouts (ms)
  TIMEOUT: {
    DEFAULT: 30_000,
    UPLOAD: 120_000,
    DOWNLOAD: 60_000
  }
} as const
