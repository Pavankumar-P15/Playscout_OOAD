import React, { useEffect, useRef, useState } from 'react';
import './Sport.css';
import { assets } from '../../assets/assets';

const Sports = ({selectedSport ,setSelectedSport }) => {
  const [showDropdown, setShowDropdown] = useState(false);
  const wrapperRef = useRef(null);

  const sports = [
    'Badminton',
    'Cricket',
    'Volleyball',
    'Table Tennis',
    'Football',
    'Basketball',
    'Tennis',
    'Hockey',
    'Squash',
    'Baseball',
    'Golf',
    'Rugby',
    'Swimming'
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

  const handleSportSelect = (sport) => {
    setSelectedSport(sport);
    setShowDropdown(false);
  };

  return (
    <div className="sports-wrapper" ref={wrapperRef}>
      <button className="sports-btn-book" onClick={toggleDropdown}>
        <img src={assets.trophy_icon} alt="Sports" />
        <span>{selectedSport}</span>
      </button>
      {showDropdown && (
        <ul className="sports-dropdown">
          {selectedSport !== 'Select Sport' && (
            <li className="sports-clear-option" onClick={() => handleSportSelect('Select Sport')}>
              Clear selection
            </li>
          )}
          {sports.map((sport, index) => (
            <li key={index} onClick={() => handleSportSelect(sport)}>
              {sport}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default Sports;
