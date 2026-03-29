import React, {useContext, useState} from 'react';
import { RefreshCw } from 'lucide-react';
import './MeetHeader.css';
import Location from '../Location/location'
import Date from '../Date/Date'
import Sports from '../Sport/Sport'
import { StoreContext } from '../../context/storeContextInstance';

const MeetHeader = () => {
  const {selectedMeetLocation, setSelectedMeetLocation, selectedMeetSport, setSelectedMeetSport, startDate, setStartDate, fetchGameList} = useContext(StoreContext);
  const [isRefreshing, setIsRefreshing] = useState(false);

  const handleRefresh = async () => {
    setIsRefreshing(true);
    await fetchGameList({ force: true });
    setIsRefreshing(false);
  };

  return (
    <div className="meet-header">
        <div className="meet-title">
            <h2>Find Players Nearby</h2>
        </div>
        <div className="meet-bar">
        <Location selectedLocation={selectedMeetLocation} setSelectedLocation={setSelectedMeetLocation} />
        <Date startDate={startDate} setStartDate={setStartDate}/>
        <Sports selectedSport={selectedMeetSport} setSelectedSport={setSelectedMeetSport} />
        <button
          className='refresh-data-btn'
          onClick={handleRefresh}
          disabled={isRefreshing}
          aria-label='Refresh games'
          title='Refresh games'
        >
          <RefreshCw size={18} className={isRefreshing ? 'spin-icon' : ''} />
        </button>
        </div>
        <div className="meet-line"></div>
    </div>
  )
}

export default MeetHeader