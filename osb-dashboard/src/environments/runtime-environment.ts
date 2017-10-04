// this var may be injected via index.html and server-side include
const injectedEnv = window['INJECTED_ENVIRONMENT'];
if (!injectedEnv) {
  // tslint:disable-next-line:no-console
  console.warn('DASHBOARD DEFAULT DEVELOPMENT ENV ACTIVE - DID YOU FORGET TO INJECT A RUNTIME ENV?');
}

export interface Environment {
  serviceInstanceId: string;
  token: string;
  production: boolean;
  baseUrls: {
    serviceBrokerUrl: string;
  };
  ui: {
    title: string;
    logoSrc: string;
  };
};

// we use quoutes here because that makes it easier to copy config to nginx.conf or cf manifest files
// tslint:disable:quotemark
const seedEnv = {
  serviceInstanceId: 'SI1',
  token: 'Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6ImxlZ2FjeS10b2tlbi1rZXkiLCJ0eXAiOiJKV1QifQ.eyJqdGkiOiI4ZTUxZTM2MzQ1YzE0ZWU1OTlkNzdlNDY5YzIyYjJmOCIsInN1YiI6IjUwNWM1NDIxLWYxYWYtNGZmNi1iZTY1LWU4MmViZWFhMDc5MSIsInNjb3BlIjpbImNsb3VkX2NvbnRyb2xsZXJfc2VydmljZV9wZXJtaXNzaW9ucy5yZWFkIiwib3BlbmlkIl0sImNsaWVudF9pZCI6Im15c3FsLWRldiIsImNpZCI6Im15c3FsLWRldiIsImF6cCI6Im15c3FsLWRldiIsImdyYW50X3R5cGUiOiJhdXRob3JpemF0aW9uX2NvZGUiLCJ1c2VyX2lkIjoiNTA1YzU0MjEtZjFhZi00ZmY2LWJlNjUtZTgyZWJlYWEwNzkxIiwib3JpZ2luIjoidWFhIiwidXNlcl9uYW1lIjoiYWRtaW4iLCJlbWFpbCI6ImFkbWluIiwiYXV0aF90aW1lIjoxNTAyMTA1NDI3LCJyZXZfc2lnIjoiMTZhN2Y2YWMiLCJpYXQiOjE1MDIxMDU0MjgsImV4cCI6MTUwMjE0ODYyOCwiaXNzIjoiaHR0cHM6Ly91YWEuY2YuZXUtZGUtbmV0ZGUubXNoLmhvc3Qvb2F1dGgvdG9rZW4iLCJ6aWQiOiJ1YWEiLCJhdWQiOlsibXlzcWwtZGV2Iiwib3BlbmlkIiwiY2xvdWRfY29udHJvbGxlcl9zZXJ2aWNlX3Blcm1pc3Npb25zIl19.JuYdHhFgKGo2azevAY4fOYbjCth5C2w0fN-gpWj_PQVJ8pD-3T1F_dutsCFsX_Dw3o1_FzcWviVUAckVnXKoW5nbUSG50iJYhLyAYPf0xMieMgh7Vf5HsOorPjwqzB7q73zfhU5NzmF2kbAm5OHCr5LeBjtFUBwCbVII21XOkfo',
  production: false,
  baseUrls: {
    serviceBrokerUrl: 'http://localhost:8080',
  },
  ui: {
    title: "Service Broker Panel",
    logoSrc: "./assets/core/sb-white.svg"
  }
};

// overwrite default env with injected vars
export const environment: Environment = Object.assign({}, seedEnv, injectedEnv);
