import React, {useEffect, useContext } from 'react'
import Bookheader from '../../components/Bookheader/Bookheader'
import { StoreContext } from '../../context/storeContextInstance'
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
    </div>
  )
}

export default Booking
