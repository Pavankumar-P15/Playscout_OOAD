import React, { useContext, useEffect, useState } from 'react';
import axios from 'axios';
import './Upcoming.css';
import { StoreContext } from '../../context/storeContextInstance';
import { toast } from 'react-toastify';

const Upcoming = () => {
  const [bookings, setBookings] = useState([]);
  const [plannedGames, setPlannedGames] = useState([]);
  const { url, fetchGameList, fetchVenueList, token, getImageUrl } = useContext(StoreContext);

  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);

  const fetchBookings = async () => {
    try {
      const response = await axios.get(`${url}/api/bookings`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setBookings(response?.data?.data || []);
    } catch (error) {
      setBookings([]);
    }
  };

  const fetchPlannedGames = async () => {
    try {
      const response = await axios.get(`${url}/api/games/me`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setPlannedGames(response?.data?.data || []);
    } catch (error) {
      setPlannedGames([]);
    }
  };

  const removeGame = async (gameID) => {
    try {
      const response = await axios.delete(`${url}/api/games/${gameID}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      if (response?.data?.success) {
        toast.success(response?.data?.message || 'Game cancelled successfully');
        setPlannedGames((prev) => prev.filter((game) => game._id !== gameID));
        await Promise.all([fetchPlannedGames(), fetchGameList()]);
      } else {
        toast.error(response?.data?.message || 'Unable to cancel planned game');
      }
    } catch (error) {
      toast.error(error?.response?.data?.message || 'Game cancellation endpoint is unavailable.');
    }
  };

  const cancelBooking = async (bookId) => {
    try {
      const response = await axios.patch(
        `${url}/api/bookings/${bookId}`,
        { status: 'CANCELLED' },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      if (response?.data?.success) {
        toast.success(response.data.message);
        setBookings((prev) => prev.filter((booking) => booking._id !== bookId));
        fetchBookings();
        fetchVenueList();
      } else {
        toast.error('Unable to cancel booking');
      }
    } catch (error) {
      toast.error('Booking cancellation endpoint is unavailable.');
    }
  };

  useEffect(() => {
    if (!token) {
      toast.info('Please login to view upcoming activities.');
      return;
    }

    fetchBookings();
    fetchPlannedGames();
  }, [token]);

  if (!token) {
    return (
      <div className='upcoming-container'>
        <h2>Upcoming</h2>
        <p className='no-data-message'>Login to see your bookings, planned games, and requests.</p>
      </div>
    );
  }

  return (
    <div className='upcoming-container'>
      <h2>Upcoming Games</h2>

      <div>
        <h3>Booked</h3>
        <table className='bookings-table'>
          <thead>
            <tr>
              <th>Preview</th>
              <th>Venue</th>
              <th>Location</th>
              <th>Sport</th>
              <th>Date</th>
              <th>Slot</th>
              <th>Cancel</th>
            </tr>
          </thead>
          <tbody>
            {bookings.length > 0 ? (
              bookings.map((booking, index) => (
                <tr key={index}>
                  <td className='image-cell'>
                    <img src={getImageUrl(booking.courtImage)} alt={booking.courtName} className='court-image' />
                  </td>
                  <td>{booking.courtName}</td>
                  <td>{booking.courtLocation}</td>
                  <td>{booking.sport}</td>
                  <td>{booking.bookingDate}</td>
                  <td>{booking.slot}</td>
                  <td>
                    <p onClick={() => cancelBooking(booking._id)} className='book-cancel'>
                      x
                    </p>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan='9'>No upcoming bookings</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      <div>
        <h3>Planned</h3>
        <table className='bookings-table'>
          <thead>
            <tr>
              <th>Sport</th>
              <th>Date</th>
              <th>Level</th>
              <th>Venue</th>
              <th>Location</th>
              <th>Members Joined</th>
              <th>Total Members</th>
              <th>Cancel</th>
            </tr>
          </thead>
          <tbody>
            {plannedGames.length > 0 ? (
              plannedGames.map((game, index) => {
                return (
                  <tr key={index}>
                    <td>
                      <img src={getImageUrl(game.sportIcon)} alt='Sport' className='icon-image' />
                    </td>
                    <td>{game.date}</td>
                    <td>{game.level}</td>
                    <td>{game.courtName}</td>
                    <td>{game.location}</td>
                    <td>{game.membersJoined}</td>
                    <td>{game.totalMembers}</td>
                    <td>
                      <p onClick={() => removeGame(game._id)} className='book-cancel'>
                        x
                      </p>
                    </td>
                  </tr>
                );
              })
            ) : (
              <tr>
                <td colSpan='8'>No planned games</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Upcoming;
