import axios from "axios";

const API_URL = 'http://localhost:8080/api'; // This is common in all APIs

const api = axios.create({
  baseURL: API_URL,
});

api.interceptors.request.use((config) => {
    const userId = localStorage.getItem('userId'); // Get userId from local storage
    const token = localStorage.getItem('token');


    if(token){
        config.headers['Authorization'] = `Bearer ${token}`; // Set the Authorization header with the token
    }
    
    if (userId) {
        config.headers['X-User-ID'] = userId; // Set the Authorization header with the userId
    }
    return config; // Ensure the modified config is returned
});

export const getActivities = () => api.get('/activities'); // GET request to fetch all activities
export const addActivity = (activity) => api.post('/activities', activity); // POST request to add a new activity
export const getActivityDetail = (id) => api.get(`/recommendations/activity/${id}`); // GET request to fetch a specific activity by ID
export const deleteActivity = (id) => api.delete(`/activities/${id}`); // DELETE request to delete a specific activity by ID