import React, { useContext, useState } from 'react';
import './Bookheader.css';
import Location from '../Location/location';
import Sports from '../Sport/Sport';
import BookDisplay from '../Bookingdisplay/Bookdisplay';
import { StoreContext } from '../../context/StoreContext';


const Bookheader = () => {
  const {selectedSport, setSelectedSport} = useContext(StoreContext)
  const [selectedLocation, setSelectedLocation] = useState('Select Location');

  return (
    <div className="book-header">
        <div className='first-text'>
            <p className="left-text">Hello Player!</p>
            <p className="right-text">Don't be.....lazy, rack up!</p>
        </div>
        <div className="book-bar">
           <Location setSelectedLocation={setSelectedLocation} />
           <Sports selectedSport={selectedSport} setSelectedSport={setSelectedSport} />
        </div>
        <br/>
        <div className="book-line"></div>

       <BookDisplay selectedSport={selectedSport} selectedLocation={selectedLocation}/>
    </div>
  );
};

export default Bookheader;
