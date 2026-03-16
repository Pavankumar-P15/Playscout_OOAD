import React, { useContext } from 'react';
import './Bookdisplay.css'; 
import { StoreContext } from '../../context/StoreContext';
import Bookvenue from '../Bookvenue/Bookvenue'; 

const BookDisplay = ({ selectedSport, selectedLocation }) => {
  const { COURT_list } = useContext(StoreContext);

  const filteredCourts = COURT_list.filter((item) => {
    const sportMatch = selectedSport === 'Select Sport' || selectedSport === 'All' || item.sport === selectedSport;
    const locationMatch = selectedLocation === 'Select Location' || selectedLocation === 'All' || item.courtLocation === selectedLocation;

    return sportMatch && locationMatch;
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
