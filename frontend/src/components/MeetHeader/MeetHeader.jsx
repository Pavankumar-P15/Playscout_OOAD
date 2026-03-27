import React, {useContext} from 'react';
import './MeetHeader.css';
import Location from '../Location/location'
import Date from '../Date/Date'
import Sports from '../Sport/Sport'
import { StoreContext } from '../../context/storeContextInstance';

const MeetHeader = () => {
  const {selectedMeetLocation, setSelectedMeetLocation, selectedMeetSport, setSelectedMeetSport, startDate, setStartDate} = useContext(StoreContext);

  return (
    <div className="meet-header">
        <div className="meet-title">
            <h2>Find Players Nearby</h2>
        </div>
        <div className="meet-bar">
        <Location selectedLocation={selectedMeetLocation} setSelectedLocation={setSelectedMeetLocation} />
        <Date startDate={startDate} setStartDate={setStartDate}/>
        <Sports selectedSport={selectedMeetSport} setSelectedSport={setSelectedMeetSport} />
        </div>
        <div className="meet-line"></div>
    </div>
  )
}

export default MeetHeader