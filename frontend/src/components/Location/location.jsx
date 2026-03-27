import React, { useEffect, useRef, useState } from 'react';
import './Location.css';
import { assets } from '../../assets/assets';

const Location = ({ selectedLocation, setSelectedLocation }) => {
  const [showDropdown, setShowDropdown] = useState(false);
  const wrapperRef = useRef(null);

  const locations = [
    'Koramangala, Bengaluru',
    'Indiranagar, Bengaluru',
    'Whitefield, Bengaluru',
    'MG Road, Bengaluru',
    'Jayanagar, Bengaluru',
    'HSR Layout, Bengaluru',
    'Bellandur, Bengaluru',
    'Marathahalli, Bengaluru',
    'Basavanagudi, Bengaluru',
    'Hebbal, Bengaluru',
    'Yeshwanthpur, Bengaluru',
    'Rajajinagar, Bengaluru',
    'Malleswaram, Bengaluru',
    'Banashankari, Bengaluru',
    'Sarjapur, Bengaluru',
    'Electronic City, Bengaluru',
    'BTM Layout, Bengaluru',
    'Ulsoor, Bengaluru',
    'Vijayanagar, Bengaluru',
    'JP Nagar, Bengaluru',
    'Domlur, Bengaluru',
    'Frazer Town, Bengaluru',
    'Kalyan Nagar, Bengaluru',
    'Kammanahalli, Bengaluru',
    'Nagarbhavi, Bengaluru',
    'Hennur, Bengaluru',
    'HBR Layout, Bengaluru',
    'Nagawara, Bengaluru',
    'Mahadevapura, Bengaluru',
    'Peenya, Bengaluru'
  ];

  useEffect(() => {
    const handleOutsideClick = (event) => {
      if (wrapperRef.current && !wrapperRef.current.contains(event.target)) {
        setShowDropdown(false);
      }
    };

    document.addEventListener('mousedown', handleOutsideClick);
    return () => {
      document.removeEventListener('mousedown', handleOutsideClick);
    };
  }, []);

  const toggleDropdown = () => {
    setShowDropdown(!showDropdown);
  };

  const handleLocationSelect = (location) => {
    setSelectedLocation(location);
    setShowDropdown(false);
  };

  return (
    <div className="location-wrapper" ref={wrapperRef}>
      <button className="location-btn-book" onClick={toggleDropdown}>
        <img src={assets.location_icon} alt="Location" />
        <span>{selectedLocation}</span>
      </button>
      {showDropdown && (
        <ul className="location-dropdown">
          {selectedLocation !== 'Select Location' && (
            <li className="location-clear-option" onClick={() => handleLocationSelect('Select Location')}>
              Clear selection
            </li>
          )}
          {locations.map((location, index) => (
            <li key={index} onClick={() => handleLocationSelect(location)}>
              {location}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default Location;
