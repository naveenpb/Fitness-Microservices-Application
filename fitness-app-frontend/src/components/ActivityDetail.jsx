import { Typography, Box, Card, CardContent, Divider } from '@mui/material'; // Importing Material-UI components
import React, { useEffect } from 'react'; // Importing React and useEffect
import { useParams } from 'react-router-dom'; // Hook to get route parameters
import { getActivityDetail } from '../services/api'; // Importing the API function to fetch activity details

const ActivityDetail = () => {
  const { id } = useParams(); // Extracting the activity ID from the route parameters
  const [activity, setActivity] = React.useState(null); // State to hold activity details
  const [recommendation, setRecommendation] = React.useState(null); // State to hold AI recommendations

  useEffect(() => {
    const fetchActivityDetail = async () => {
      try {
        const response = await getActivityDetail(id); // Fetching activity details from the API
        setActivity(response.data); // Setting the activity details to state
        setRecommendation(response.data.recommendation); // Setting the recommendation to state
      } catch (error) {
        console.error("Error fetching activity detail:", error); // Logging error if any
      }
    };

    fetchActivityDetail(); // Calling the function to fetch activity details
  }, [id]); // Dependency array ensures this runs only when the ID changes

  if (!activity) {
    return <Typography>Loading....</Typography>; // Displaying a loading message while fetching data
  }

  return (
    <Box sx={{ maxWidth: 800, mx: 'auto', p: 2 }}>
      {/* Activity Details Card */}
      <Card
        sx={{
          mb: 3,
          boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)', // Subtle shadow for depth
          borderRadius: 3, // Rounded corners
          overflow: 'hidden', // Ensure content stays within the card
          backgroundColor: '#f9f9f9', // Light background color
        }}
      >
        <CardContent>
          <Typography variant="h5" gutterBottom sx={{ fontWeight: 'bold', color: '#3f51b5' }}>
            Activity Details
          </Typography>
          {/* <Typography sx={{ fontSize: '1rem', color: '#555' }}>Type: {activity.type}</Typography> */}
          <Typography sx={{ fontSize: '1rem', color: '#555' }}>Duration: {activity.duration} minutes</Typography>
          <Typography sx={{ fontSize: '1rem', color: '#555' }}>Calories Burned: {activity.caloriesBurned}</Typography>
          <Typography sx={{ fontSize: '1rem', color: '#555' }}>
            Date: {new Date(activity.createdAt).toLocaleString()}
          </Typography>
        </CardContent>
      </Card>

      {/* AI Recommendation Card */}
      {recommendation && (
        <Card
          sx={{
            boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)', // Subtle shadow for depth
            borderRadius: 3, // Rounded corners
            overflow: 'hidden', // Ensure content stays within the card
            backgroundColor: '#ffffff', // White background for contrast
          }}
        >
          <CardContent>
            <Typography variant="h5" gutterBottom sx={{ fontWeight: 'bold', color: '#3f51b5' }}>
              AI Recommendation
            </Typography>

            <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#333' }}>
              Analysis
            </Typography>
            <Typography paragraph sx={{ color: '#555' }}>
              {activity.recommendation}
            </Typography>

            <Divider sx={{ my: 2 }} />

            <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#333' }}>
              Improvements
            </Typography>
            {activity?.improvements?.map((improvement, index) => (
              <Typography key={index} paragraph sx={{ color: '#555' }}>
                • {improvement}
              </Typography>
            ))}

            <Divider sx={{ my: 2 }} />

            <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#333' }}>
              Suggestions
            </Typography>
            {activity?.suggestions?.map((suggestion, index) => (
              <Typography key={index} paragraph sx={{ color: '#555' }}>
                • {suggestion}
              </Typography>
            ))}

            <Divider sx={{ my: 2 }} />

            <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#333' }}>
              Safety Guidelines
            </Typography>
            {activity?.safety?.map((safety, index) => (
              <Typography key={index} paragraph sx={{ color: '#555' }}>
                • {safety}
              </Typography>
            ))}
          </CardContent>
        </Card>
      )}
    </Box>
  );
};

export default ActivityDetail;