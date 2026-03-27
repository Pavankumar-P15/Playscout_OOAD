import React, { useContext, useEffect, useState } from 'react';
import { Plus, List, UploadCloud } from 'lucide-react';
import './Admin.css';
import axios from 'axios';
import { StoreContext } from '../../context/storeContextInstance';
import { toast } from 'react-toastify';

const Admin = () => {
  const { url, token, role, getImageUrl } = useContext(StoreContext);
  const [showAddForm, setShowAddForm] = useState(false);
  const [venues, setVenues] = useState([]);

  const [venueData, setVenueData] = useState({
    courtName: '',
    sport: '',
    courtLocation: '',
    courtsAvailable: '',
    price: '',
    courtImage: null
  });
  const [imagePreview, setImagePreview] = useState(null);

  const fetchVenues = async () => {
    try {
      const response = await axios.get(`${url}/api/venues`);
      setVenues(response?.data?.data || []);
    } catch (error) {
      toast.error('Failed to fetch venues.');
      setVenues([]);
    }
  };

  useEffect(() => {
    fetchVenues();
  }, [url]);

  useEffect(() => {
    return () => {
      if (imagePreview) {
        URL.revokeObjectURL(imagePreview);
      }
    };
  }, [imagePreview]);

  const handleImageUpload = (e) => {
    const file = e.target.files[0];
    if (!file) {
      return;
    }

    if (imagePreview) {
      URL.revokeObjectURL(imagePreview);
    }

    setImagePreview(URL.createObjectURL(file));
    setVenueData((prev) => ({ ...prev, courtImage: file }));
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setVenueData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!token) {
      toast.error('Please login first.');
      return;
    }

    const formData = new FormData();
    formData.append('courtName', venueData.courtName);
    formData.append('sport', venueData.sport);
    formData.append('courtLocation', venueData.courtLocation);
    formData.append('courtsAvailable', Number(venueData.courtsAvailable));
    formData.append('price', Number(venueData.price));
    formData.append('court-image', venueData.courtImage);

    try {
      const response = await axios.post(`${url}/api/venues`, formData, {
        headers: { Authorization: `Bearer ${token}` }
      });

      if (response?.data?.success) {
        toast.success(response.data.message || 'Venue added');
        setVenueData({
          courtName: '',
          sport: '',
          courtLocation: '',
          courtsAvailable: '',
          price: '',
          courtImage: null
        });
        setImagePreview(null);
        fetchVenues();
      } else {
        toast.error(response?.data?.message || 'Unable to add venue');
      }
    } catch (error) {
      toast.error('Venue add endpoint is unavailable or unauthorized.');
    }
  };

  const removeVenue = async (venueID) => {
    if (!token) {
      toast.error('Please login first.');
      return;
    }

    try {
      const response = await axios.delete(
        `${url}/api/venues/${venueID}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );

      if (response?.data?.success) {
        toast.success(response.data.message || 'Venue removed');
        fetchVenues();
      } else {
        toast.error(response?.data?.message || 'Unable to remove venue');
      }
    } catch (error) {
      toast.error('Venue remove endpoint is unavailable or unauthorized.');
    }
  };

  if (!token || role !== 'ADMIN') {
    return (
      <div className='admin-container'>
        <div className='card'>
          <div className='card-header'>
            <h2 className='card-title'>Admin Access Required</h2>
          </div>
          <div className='card-content'>
            <p>You need an admin account to access this page.</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className='admin-container'>
      <div className='button-group'>
        <button onClick={() => setShowAddForm(false)} className={`custom-button ${!showAddForm ? 'active' : ''}`}>
          <List className='button-icon' />
          List Venues
        </button>
        <button onClick={() => setShowAddForm(true)} className={`custom-button ${showAddForm ? 'active' : ''}`}>
          <Plus className='button-icon' />
          Add Venue
        </button>
      </div>

      {showAddForm ? (
        <div className='card'>
          <div className='card-header'>
            <h2 className='card-title'>Add New Venue</h2>
          </div>
          <div className='card-content'>
            <form onSubmit={handleSubmit} className='venue-form'>
              <div className='form-grid'>
                <div className='form-group'>
                  <label>Court Name</label>
                  <input name='courtName' value={venueData.courtName} onChange={handleInputChange} required className='input-court' />
                </div>
                <div className='form-group'>
                  <label>Sport</label>
                  <input name='sport' value={venueData.sport} onChange={handleInputChange} required className='input' />
                </div>
              </div>

              <div className='form-group'>
                <label>Court Location</label>
                <input name='courtLocation' value={venueData.courtLocation} onChange={handleInputChange} required className='input' />
              </div>

              <div className='form-grid'>
                <div className='form-group'>
                  <label>Courts Available</label>
                  <input
                    type='number'
                    name='courtsAvailable'
                    value={venueData.courtsAvailable}
                    onChange={handleInputChange}
                    min='1'
                    required
                    className='input'
                  />
                </div>
                <div className='form-group'>
                  <label>Price/hr</label>
                  <input
                    type='number'
                    name='price'
                    value={venueData.price}
                    onChange={handleInputChange}
                    min='0'
                    required
                    className='input'
                  />
                </div>
              </div>

              <div className='form-group'>
                <label>Court Image</label>
                <div className='image-upload-container'>
                  <input type='file' accept='image/*' onChange={handleImageUpload} className='hidden' id='court-image-upload' required />
                  <label htmlFor='court-image-upload' className='upload-label'>
                    {imagePreview ? (
                      <img src={imagePreview} alt='Preview' className='image-preview' />
                    ) : (
                      <div className='upload-placeholder'>
                        <UploadCloud className='upload-icon' />
                        <span>Upload court image</span>
                      </div>
                    )}
                  </label>
                </div>
              </div>

              <button type='submit' className='submit-button'>
                Add Venue
              </button>
            </form>
          </div>
        </div>
      ) : (
        <div className='card'>
          <div className='card-header'>
            <h2 className='card-title'>Venue List</h2>
          </div>
          <div className='card-content'>
            {venues.length > 0 ? (
              <table className='venue-table'>
                <thead>
                  <tr>
                    <th>Court Name</th>
                    <th>Sport</th>
                    <th>Location</th>
                    <th>Courts Available</th>
                    <th>Price/hr</th>
                    <th>Rating</th>
                    <th>Image</th>
                    <th>Remove</th>
                  </tr>
                </thead>
                <tbody>
                  {venues.map((venue) => (
                    <tr key={venue._id || venue.id}>
                      <td>{venue.courtName}</td>
                      <td>{venue.sport}</td>
                      <td>{venue.courtLocation}</td>
                      <td>{venue.courtsAvailable}</td>
                      <td>{venue.price}</td>
                      <td>{venue.rating}</td>
                      <td>
                        <img src={getImageUrl(venue.courtImage)} alt='Court' className='court-image' />
                      </td>
                      <td>
                        <p onClick={() => removeVenue(venue._id || venue.id)} className='remove-icon'>
                          x
                        </p>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : (
              <div className='empty-state'>No venues added yet</div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default Admin;
