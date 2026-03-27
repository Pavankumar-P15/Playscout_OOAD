import React, { useState, useEffect, useContext } from 'react'
import './Meet.css'
import MeetHeader from '../../components/MeetHeader/MeetHeader'
import PlayerDisplay from '../../components/PlayerDisplay/PlayerDisplay'
import { StoreContext } from '../../context/storeContextInstance'

const Meet = () => {
  const {selectedMeetLocation, selectedMeetSport, startDate, setSelectedMeetSport, setStartDate} = useContext(StoreContext);
  useEffect(() => {
    window.scrollTo(0, 0);

    return () => {
      setSelectedMeetSport('Select Sport');
      setStartDate(null);
    };
  }, [setSelectedMeetSport, setStartDate]);

  return (
    <div>
      <MeetHeader />
      <PlayerDisplay selectedMeetSport={selectedMeetSport} startDate={startDate} selectedMeetLocation={selectedMeetLocation}/>
    </div>
  )
}

export default Meet