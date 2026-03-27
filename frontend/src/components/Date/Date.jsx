import React, { useEffect, useRef, useState } from 'react';
import DatePicker from 'react-datepicker';
import "react-datepicker/dist/react-datepicker.css";
import './Date.css';
import { assets } from '../../assets/assets';

const Date = ({ startDate, setStartDate }) => {
  const [showCalendar, setShowCalendar] = useState(false);
  const wrapperRef = useRef(null);

  useEffect(() => {
    const handleOutsideClick = (event) => {
      if (wrapperRef.current && !wrapperRef.current.contains(event.target)) {
        setShowCalendar(false);
      }
    };

    document.addEventListener('mousedown', handleOutsideClick);
    return () => {
      document.removeEventListener('mousedown', handleOutsideClick);
    };
  }, []);

  const handleDateChange = (date) => {
    setStartDate(date);
    setShowCalendar(false);
  };

  const handleClearDate = () => {
    setStartDate(null);
    setShowCalendar(false);
  };

  return (
    <div className="calendar-wrapper" ref={wrapperRef}>
      <button className="date-btn-book" onClick={() => setShowCalendar((previous) => !previous)}>
        <img src={assets.calendar_icon} alt="Calendar" />
        <span>{startDate ? startDate.toLocaleDateString() : 'Select Date'}</span>
      </button>
      {showCalendar && (
        <div className="calendar-container">
          <DatePicker
            selected={startDate}
            onChange={handleDateChange}
            className="datepicker"
            inline
          />
          <button className="date-clear-btn" onClick={handleClearDate}>
            Clear date
          </button>
        </div>
      )}
    </div>
  );
};

export default Date;

