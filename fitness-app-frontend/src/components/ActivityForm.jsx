import React from 'react';
import { addActivity } from '../services/api';
import { Box, Button, FormControl, InputLabel, MenuItem, Select, TextField } from '@mui/material';

const ActivityForm = ({ onActivityAdded }) => {
  const [activity, setActivity] = React.useState({
    type: 'Running',
    duration: '',
    caloriesBurned: '',
    additionalMetrics: {},
  });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await addActivity(activity); // Add the activity via the API
      console.log('Activity added:', response.data); // Log the added activity
      if (onActivityAdded) {
        onActivityAdded(); // Call the callback function after successfully adding the activity
      }
      setActivity({ type: 'Running', duration: '', caloriesBurned: '' }); // Reset the form
    } catch (err) {
      console.error('Error adding activity:', err); // Log the error
    }
  };

  return (
    <Box component="form" onSubmit={handleSubmit} sx={{ mb: 4 }}>
      <FormControl fullWidth variant="outlined" sx={{ mb: 2 }}>
        <InputLabel>Activity Type</InputLabel>
        <Select
          value={activity.type}
          onChange={(e) => setActivity({ ...activity, type: e.target.value })}
        >
          <MenuItem value="RUNNING">RUNNING</MenuItem>
          <MenuItem value="WALKING">WALKING</MenuItem>
          <MenuItem value="CYCLING">CYCLING</MenuItem>
          <MenuItem value="SWIMMING">SWIMMING</MenuItem>
          <MenuItem value="WEIGHT_TRAINING">WEIGHT_TRAINING</MenuItem>
          <MenuItem value="YOGA">YOGA</MenuItem>
          <MenuItem value="CARDIO">CARDIO</MenuItem>
          <MenuItem value="PRANAYAMA">PRANAYAMA</MenuItem>
          <MenuItem value="JUMPING">JUMPING</MenuItem>
          <MenuItem value="SKIPPING">SKIPPING</MenuItem>
          <MenuItem value="DANCING">DANCING</MenuItem>
          <MenuItem value="JOGGING">JOGGING</MenuItem>
          <MenuItem value="MEDITATION">MEDITATION</MenuItem>
          <MenuItem value="PUSHUPS">PUSHUPS</MenuItem>
          <MenuItem value="PULLUPS">PULLUPS</MenuItem>
        </Select>
      </FormControl>
      <TextField
        fullWidth
        label="Duration (Minutes)"
        type="number"
        sx={{ mb: 2 }}
        value={activity.duration}
        onChange={(e) => setActivity({ ...activity, duration: e.target.value })}
      />
      <TextField
        fullWidth
        label="Calories Burned"
        type="number"
        value={activity.caloriesBurned}
        onChange={(e) => setActivity({ ...activity, caloriesBurned: e.target.value })}
      />
      <Button type="submit" variant="contained">
        Add Activity
      </Button>
    </Box>
  );
};

export default ActivityForm;