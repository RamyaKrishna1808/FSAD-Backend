# Frontend Configuration for LMS

## API Base URL Configuration

Your frontend at `http://localhost:5178/` needs to connect to the backend at `http://localhost:8080/api`.

### For React/Vue with Vite:

Create or update `.env` file in your frontend project root:

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

### For Axios HTTP Client:

Create `src/config/api.js` or `src/services/api.js`:

```javascript
import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add request interceptor for authentication if needed
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;
```

### For Fetch API:

```javascript
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

const apiCall = async (endpoint, options = {}) => {
  const url = `${API_BASE_URL}${endpoint}`;
  const token = localStorage.getItem('token');

  const config = {
    headers: {
      'Content-Type': 'application/json',
      ...(token && { Authorization: `Bearer ${token}` }),
      ...options.headers,
    },
    ...options,
  };

  const response = await fetch(url, config);
  return response.json();
};
```

## CORS Configuration

The backend already has `@CrossOrigin(origins = "*")` on controllers, so CORS should work.

## Example API Calls:

```javascript
// Get assignments
const assignments = await api.get('/assignments');

// Get notifications
const notifications = await api.get('/notifications?userId=123');

// Submit assignment (multipart)
const formData = new FormData();
formData.append('studentId', '123');
formData.append('assignmentId', '456');
formData.append('file', file);
const response = await api.post('/submissions', formData, {
  headers: { 'Content-Type': 'multipart/form-data' }
});
```