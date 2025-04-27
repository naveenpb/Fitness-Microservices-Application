import { Button, Box, Typography } from "@mui/material";
import { useEffect, useContext, useState } from "react";
import { AuthContext } from "react-oauth2-code-pkce";
import { BrowserRouter as Router, Navigate, Routes, Route } from "react-router-dom";
import { useDispatch } from "react-redux";
import { setCredentials } from "./store/authSlice";
import ActivityForm from "./components/ActivityForm";
import ActivityList from "./components/ActivityList";
import ActivityDetail from "./components/ActivityDetail";

// we are combining two components
const ActivitiesPage = () => {
  // This component renders the ActivityForm and ActivityList inside a styled Box
  return (
    <Box component="section" sx={{ p: 2, border: '1px dashed grey' }}>
      <ActivityForm onActivitiesAdded={() => window.location.reload()} />
      <ActivityList />
    </Box>
  );
};

function App() {
  const { token, tokenData, logIn, logOut } = useContext(AuthContext); // Using AuthContext to get token, login, and logout functions
  const dispatch = useDispatch();
  const [authReady, setAuthReady] = useState(false); // State to track if authentication is ready

  useEffect(() => {
    if (token) {
      // Dispatching credentials to Redux store when token is available
      dispatch(setCredentials({ token, user: tokenData }));
      setAuthReady(true); // Marking authentication as ready
    }
  }, [token, tokenData, dispatch]); // Whenever there is a change in the array of dependencies, this function is executed

  return (
    <Router>
      {!token ? (
        // Login Page UI
        <Box
          sx={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            height: '100vh',
            textAlign: 'center',
            backgroundColor: '#f5f5f5',
            padding: 2,
          }}
        >
          <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 2 }}>
            Welcome to Fitness Microservices App
          </Typography>
          <Typography variant="body1" sx={{ mb: 4, color: '#757575' }}>
            Please log in to access your fitness activities.
          </Typography>
          <Button variant="contained" color="primary" onClick={() => logIn()}>
            Login
          </Button>
        </Box>
      ) : (
        <>
          {/* Logout Button */}
          <Box sx={{ display: 'flex', justifyContent: 'flex-start', p: 2 }}>
            <Button variant="contained" color="secondary" onClick={() => logOut()}>
              Logout
            </Button>
          </Box>

          {/* Routes */}
          <Routes>
            <Route path="/activities" element={<ActivitiesPage />} />
            <Route path="/activities/:id" element={<ActivityDetail />} />
            <Route
              path="/"
              element={<Navigate to="/activities" replace />}
            />
          </Routes>
        </>
      )}
    </Router>
  );
}

export default App;