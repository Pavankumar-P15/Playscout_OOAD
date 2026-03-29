import React, { useContext } from 'react';
import './Bookdisplay.css'; 
import { StoreContext } from '../../context/storeContextInstance';
import Bookvenue from '../Bookvenue/Bookvenue'; 

const BookDisplay = ({ selectedSport, selectedLocation }) => {
  const { COURT_list } = useContext(StoreContext);

  const sportFilter = selectedSport ?? 'Select Sport';
  const locationFilter = selectedLocation ?? 'Select Location';

  const filteredCourts = COURT_list.filter((item) => {
    const sportMatch = sportFilter === 'Select Sport' || sportFilter === 'All' || item.sport === sportFilter;
    const locationMatch = locationFilter === 'Select Location' || locationFilter === 'All' || item.courtLocation === locationFilter;
    const hasBookableCourt = item.courtsAvailable > 0;

    return sportMatch && locationMatch && hasBookableCourt;
  });

  if (filteredCourts.length === 0) {
    return <div>No more courts available for this category.</div>;
  }

  return (
    <div className='booking-display'>
      <div className="booking-display-list">
        {filteredCourts.map((item,index) => (
          <Bookvenue 
            key={index}
            className='booking-display-list-item'
            courtName={item.courtName}
            courtLocation={item.courtLocation}
            courtsAvailable={item.courtsAvailable}
            price={item.price}
            courtImage={item.courtImage}
            game_icon={item.game_icon}
            sport={item.sport}
            id={item._id}
          />
        ))}
      </div>
    </div>
  );
}

export default BookDisplay;
