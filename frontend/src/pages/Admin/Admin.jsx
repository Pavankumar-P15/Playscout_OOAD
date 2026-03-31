import React, { useContext, useEffect, useState } from 'react';
import { BarChart3, Users, Building2, Gamepad2 } from 'lucide-react';
import './Admin.css';
import axios from 'axios';
import { StoreContext } from '../../context/storeContextInstance';
import { toast } from 'react-toastify';

const Admin = () => {
  const { url, token, role } = useContext(StoreContext);
  const [activeTab, setActiveTab] = useState('dashboard');
  
  // Dashboard state
  const [dashboard, setDashboard] = useState({
    totalUsers: 0,
    totalVenues: 0,
    totalGames: 0,
    totalBookings: 0
  });

  // Users state
  const [users, setUsers] = useState([]);
  
  // Venues state
  const [venues, setVenues] = useState([]);

// Games state
  const [games, setGames] = useState([]);
  
  const [loading, setLoading] = useState(false);

  // Fetch dashboard stats
  const fetchDashboard = async () => {
    try {
      const response = await axios.get(`${url}/api/admin/dashboard`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      if (response.data.success) {
        setDashboard(response.data.data);
      }
    } catch (error) {
      console.error('Error fetching dashboard:', error);
      toast.error('Failed to load dashboard');
    }
  };

  // Fetch users
  const fetchUsers = async () => {
    setLoading(true);
    try {
      const response = await axios.get(`${url}/api/admin/users`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      if (response.data.success) {
        setUsers(response.data.data);
      }
    } catch (error) {
      console.error('Error fetching users:', error);
      toast.error('Failed to load users');
    } finally {
      setLoading(false);
    }
  };

  // Fetch venues
  const fetchVenues = async () => {
    setLoading(true);
    try {
      const response = await axios.get(`${url}/api/admin/venues`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      if (response.data.success) {
        setVenues(response.data.data);
      }
    } catch (error) {
      console.error('Error fetching venues:', error);
      toast.error('Failed to load venues');
    } finally {
      setLoading(false);
    }
  };

  // Fetch games
  const fetchGames = async () => {
    setLoading(true);
    try {
      const response = await axios.get(`${url}/api/admin/games?limit=20`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      if (response.data.success) {
        setGames(response.data.data);
      }
    } catch (error) {
      console.error('Error fetching games:', error);
      toast.error('Failed to load games');
    } finally {
      setLoading(false);
    }
  };

  // Suspend user
  const handleSuspendUser = async (userId) => {
    try {
      const response = await axios.patch(
        `${url}/api/admin/users/${userId}/suspend`,
        {},
        { headers: { Authorization: `Bearer ${token}` } }
      );
      if (response.data.success) {
        toast.success('User suspension status updated');
        fetchUsers();
      }
    } catch (error) {
      toast.error('Failed to update user');
    }
  };

  // Disable venue
  const handleDisableVenue = async (venueId) => {
    try {
      const response = await axios.patch(
        `${url}/api/admin/venues/${venueId}/disable`,
        {},
        { headers: { Authorization: `Bearer ${token}` } }
      );
      if (response.data.success) {
        toast.success('Venue status updated');
        fetchVenues();
      }
    } catch (error) {
      toast.error('Failed to update venue');
    }
  };

  // Cancel game
  const handleCancelGame = async (gameId) => {
    if (!window.confirm('Are you sure you want to cancel this game?')) {
      return;
    }
    try {
      const response = await axios.patch(
        `${url}/api/admin/games/${gameId}/cancel`,
        {},
        { headers: { Authorization: `Bearer ${token}` } }
      );
      if (response.data.success) {
        toast.success('Game cancelled successfully');
        fetchGames();
      }
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to cancel game');
    }
  };

  // Load data on tab change
  useEffect(() => {
    if (!token || role !== 'ADMIN') return;

    if (activeTab === 'dashboard') {
      fetchDashboard();
    } else if (activeTab === 'users') {
      fetchUsers();
    } else if (activeTab === 'venues') {
      fetchVenues();
    } else if (activeTab === 'games') {
      fetchGames();
    }
  }, [activeTab, token, role, url]);

  // Check admin access
  if (!token || role !== 'ADMIN') {
    return (
      <div className='admin-container'>
        <div className='card'>
          <div className='card-header'>
            <h2 className='card-title'>Admin Access Required</h2>
          </div>
          <div className='card-content'>
            <p>You need admin privileges to access this page.</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className='admin-container'>
      {/* Tab Navigation */}
      <div className='button-group'>
        <button 
          onClick={() => setActiveTab('dashboard')} 
          className={`custom-button ${activeTab === 'dashboard' ? 'active' : ''}`}
        >
          <BarChart3 className='button-icon' />
          Dashboard
        </button>
        <button 
          onClick={() => setActiveTab('users')} 
          className={`custom-button ${activeTab === 'users' ? 'active' : ''}`}
        >
          <Users className='button-icon' />
          Users
        </button>
        <button 
          onClick={() => setActiveTab('venues')} 
          className={`custom-button ${activeTab === 'venues' ? 'active' : ''}`}
        >
          <Building2 className='button-icon' />
          Venues
        </button>
        <button 
          onClick={() => setActiveTab('games')} 
          className={`custom-button ${activeTab === 'games' ? 'active' : ''}`}
        >
          <Gamepad2 className='button-icon' />
          Games
        </button>
      </div>

      {/* Dashboard Tab */}
      {activeTab === 'dashboard' && (
        <div className='dashboard-grid'>
          <div className='stat-card'>
            <h3>Total Users</h3>
            <p className='stat-value'>{dashboard.totalUsers}</p>
          </div>
          <div className='stat-card'>
            <h3>Total Venues</h3>
            <p className='stat-value'>{dashboard.totalVenues}</p>
          </div>
          <div className='stat-card'>
            <h3>Total Games</h3>
            <p className='stat-value'>{dashboard.totalGames}</p>
          </div>
          <div className='stat-card'>
            <h3>Total Bookings</h3>
            <p className='stat-value'>{dashboard.totalBookings}</p>
          </div>
        </div>
      )}

      {/* Users Tab */}
      {activeTab === 'users' && (
        <div className='card'>
          <div className='card-header'>
            <h2 className='card-title'>User Management</h2>
          </div>
          <div className='card-content'>
            {loading ? (
              <p>Loading users...</p>
            ) : users.length > 0 ? (
              <table className='admin-table'>
                <thead>
                  <tr>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Role</th>
                    <th>Status</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {users.map((user) => (
                    <tr key={user.id}>
                      <td>{user.name}</td>
                      <td>{user.email}</td>
                      <td>{user.role}</td>
                      <td>
                        <span className={user.suspended ? 'status-suspended' : 'status-active'}>
                          {user.suspended ? 'Suspended' : 'Active'}
                        </span>
                      </td>
                      <td>
                        <button 
                          className={`action-button ${user.suspended ? 'unsuspend' : 'suspend'}`}
                          onClick={() => handleSuspendUser(user.id)}
                        >
                          {user.suspended ? 'Unsuspend' : 'Suspend'}
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : (
              <p>No users found</p>
            )}
          </div>
        </div>
      )}

      {/* Venues Tab */}
      {activeTab === 'venues' && (
        <div className='card'>
          <div className='card-header'>
            <h2 className='card-title'>Venue Management</h2>
          </div>
          <div className='card-content'>
            {loading ? (
              <p>Loading venues...</p>
            ) : venues.length > 0 ? (
              <table className='admin-table'>
                <thead>
                  <tr>
                    <th>Court Name</th>
                    <th>Sport</th>
                    <th>Location</th>
                    <th>Manager</th>
                    <th>Status</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {venues.map((venue) => (
                    <tr key={venue.id} className={venue.disabled ? 'disabled-row' : ''}>
                      <td>{venue.courtName}</td>
                      <td>{venue.sport}</td>
                      <td>{venue.courtLocation}</td>
                      <td>{venue.managerName || 'Unknown'}</td>
                      <td>
                        <span className={venue.disabled ? 'status-disabled' : 'status-active'}>
                          {venue.disabled ? 'Disabled' : 'Active'}
                        </span>
                      </td>
                      <td>
                        <button 
                          className={`action-button ${venue.disabled ? 'enable' : 'disable'}`}
                          onClick={() => handleDisableVenue(venue.id)}
                        >
                          {venue.disabled ? 'Enable' : 'Disable'}
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : (
              <p>No venues found</p>
            )}
          </div>
        </div>
      )}

      {/* Games Tab */}
      {activeTab === 'games' && (
        <div className='card'>
          <div className='card-header'>
            <h2 className='card-title'>Recent Games</h2>
          </div>
          <div className='card-content'>
            {loading ? (
              <p>Loading games...</p>
            ) : games.length > 0 ? (
              <table className='admin-table'>
                <thead>
                  <tr>
                    <th>Court</th>
                    <th>Sport</th>
                    <th>Date</th>
                    <th>Time</th>
                    <th>Creator</th>
                    <th>Members</th>
                    <th>Status</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {games.map((game) => (
                    <tr key={game.id} className={game.status === 'CANCELLED' ? 'cancelled-row' : ''}>
                      <td>{game.courtName}</td>
                      <td>{game.sport}</td>
                      <td>{game.date}</td>
                      <td>{game.startTime} - {game.endTime}</td>
                      <td>{game.creatorName}</td>
                      <td>{game.membersJoined}/{game.totalMembers}</td>
                      <td>
                        <span className={`status-${game.status.toLowerCase()}`}>
                          {game.status}
                        </span>
                      </td>
                      <td>
                        {game.status !== 'CANCELLED' && (
                          <button 
                            className='action-button cancel'
                            onClick={() => handleCancelGame(game.id)}
                          >
                            Cancel
                          </button>
                        )}
                        {game.status === 'CANCELLED' && <span>—</span>}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : (
              <p>No games found</p>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default Admin;
