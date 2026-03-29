import React, { useContext, useState } from 'react';
import { RefreshCw } from 'lucide-react';
import './Bookheader.css';
import Location from '../Location/location';
import Sports from '../Sport/Sport';
import BookDisplay from '../Bookingdisplay/Bookdisplay';
import { StoreContext } from '../../context/storeContextInstance';


const Bookheader = () => {
  const {selectedSport, setSelectedSport, fetchVenueList} = useContext(StoreContext)
  const [selectedLocation, setSelectedLocation] = useState('Select Location');
  const [isRefreshing, setIsRefreshing] = useState(false);

  const handleRefresh = async () => {
    setIsRefreshing(true);
    await fetchVenueList({ force: true });
    setIsRefreshing(false);
  };

  return (
    <div className="book-header">
        <div className='first-text'>
            <p className="left-text">Hello Player!</p>
            <p className="right-text">Don't be.....lazy, rack up!</p>
        </div>
        <div className="book-bar">
            <Location selectedLocation={selectedLocation} setSelectedLocation={setSelectedLocation} />
           <Sports selectedSport={selectedSport} setSelectedSport={setSelectedSport} />
             <button
               className='refresh-data-btn'
               onClick={handleRefresh}
               disabled={isRefreshing}
               aria-label='Refresh venues'
               title='Refresh venues'
             >
               <RefreshCw size={18} className={isRefreshing ? 'spin-icon' : ''} />
           </button>
        </div>
        <br/>
        <div className="book-line"></div>

       <BookDisplay selectedSport={selectedSport} selectedLocation={selectedLocation}/>
    </div>
  );
};

export default Bookheader;
