import React, {useEffect, useContext } from 'react'
import Bookheader from '../../components/Bookheader/Bookheader'
import Bookvenue from '../../components/Bookvenue/Bookvenue'
import BookingDisplay from '../../components/Bookingdisplay/Bookdisplay'
import { StoreContext } from '../../context/StoreContext'
const Booking = () => {
  const {setSelectedSport} = useContext(StoreContext)

  useEffect(() => {
    window.scrollTo(0, 0);

    return () => {
      setSelectedSport('Select Sport');
    };
  }, []);

  return (
    <div className='booking'>
      <Bookheader/>
      <Bookvenue/>
      <BookingDisplay/>
    </div>
  )
}

export default Booking
