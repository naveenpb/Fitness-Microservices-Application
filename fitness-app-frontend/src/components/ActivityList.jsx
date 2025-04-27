import { Card, Grid, CardContent, Typography, IconButton } from '@mui/material'; // Importing Material-UI components
import DeleteIcon from '@mui/icons-material/Delete'; // Importing the delete icon
import React from 'react'; // Importing React
import { useNavigate } from 'react-router'; // Hook to navigate between routes
import { getActivities, deleteActivity } from '../services/api'; // Importing the API functions

// This component displays a list of activities fetched from the API
const ActivityList = () => {
    const [activities, setActivities] = React.useState([]); // State to hold activities
    const navigate = useNavigate(); // Hook to navigate between routes

    // Function to fetch activities from the API
    const fetchActivities = async () => {
        try {
            const response = await getActivities(); // Fetching activities from the API
            setActivities(response.data); // Setting the fetched activities to state
        } catch (error) {
            console.error("Error fetching activities:", error); // Logging error if any
        }
    };

    // Function to delete an activity
    const handleDelete = async (id) => {
        try {
            await deleteActivity(id); // Deleting the activity via the API
            setActivities((prevActivities) => prevActivities.filter((activity) => activity.id !== id)); // Remove the deleted activity from the state
            console.log(`Activity with ID ${id} deleted successfully.`);
        } catch (error) {
            console.error("Error deleting activity:", error); // Logging error if any
        }
    };

    // useEffect to fetch activities when the component mounts
    React.useEffect(() => {
        fetchActivities(); // Fetch activities when the component mounts
    }, []); // Empty dependency array ensures this runs only once

    return (
        // Grid container to layout the activity cards
        <Grid container spacing={3} sx={{ padding: 2 }}> {/* Added padding to the grid */}
            {activities.map((activity) => (
                // Each activity is displayed in a Grid item
                <Grid item xs={12} sm={6} md={4} key={activity.id}>
                    <Card
                        sx={{
                            position: 'relative', // Position relative for the delete icon
                            cursor: 'pointer', // Styling to make the card clickable
                            transition: 'transform 0.2s, box-shadow 0.2s', // Smooth hover effect
                            '&:hover': {
                                transform: 'scale(1.05)', // Slightly enlarge the card on hover
                                boxShadow: '0 8px 16px rgba(0, 0, 0, 0.2)', // Add shadow on hover
                            },
                            borderRadius: 2, // Rounded corners
                            overflow: 'hidden', // Ensure content stays within the card
                        }}
                    >
                        {/* Delete Icon */}
                        <IconButton
                            aria-label="delete"
                            sx={{
                                position: 'absolute', // Position the icon in the top-right corner
                                top: 8,
                                right: 8,
                                backgroundColor: 'rgba(255, 255, 255, 0.8)', // Light background for better visibility
                                '&:hover': {
                                    backgroundColor: 'rgba(255, 0, 0, 0.8)', // Red background on hover
                                    color: '#fff', // White icon color on hover
                                },
                            }}
                            onClick={(e) => {
                                e.stopPropagation(); // Prevent the card click event
                                handleDelete(activity.id); // Call the delete function
                            }}
                        >
                            <DeleteIcon />
                        </IconButton>

                        <CardContent
                            sx={{
                                backgroundColor: '#f5f5f5', // Light background for the card content
                                padding: 3, // Add padding inside the card
                            }}
                            onClick={() => navigate(`/activities/${activity.id}`)} // Navigate to activity details on click
                        >
                            {/* Displaying the activity type */}
                            <Typography
                                variant="h6"
                                sx={{
                                    fontWeight: 'bold', // Bold text for the activity type
                                    color: '#3f51b5', // Primary color for the text
                                    marginBottom: 1, // Add spacing below the title
                                }}
                            >
                                {activity.type}
                            </Typography>
                            {/* Displaying the activity duration */}
                            <Typography
                                sx={{
                                    fontSize: '0.9rem', // Slightly smaller font size
                                    color: '#757575', // Secondary color for the text
                                }}
                            >
                                Duration: {activity.duration} minutes
                            </Typography>
                            {/* Displaying the calories burned */}
                            <Typography
                                sx={{
                                    fontSize: '0.9rem', // Slightly smaller font size
                                    color: '#757575', // Secondary color for the text
                                }}
                            >
                                Calories Burned: {activity.caloriesBurned}
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>
            ))}
        </Grid>
    );
};

export default ActivityList;