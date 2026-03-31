import React, { useContext, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { StoreContext } from '../../context/storeContextInstance';
import { toast } from 'react-toastify';
import './OAuthSuccess.css';

const OAuthSuccess = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { setToken, setRole, setUserId, setUserImage } = useContext(StoreContext);

  useEffect(() => {
    const token = searchParams.get('token');
    const userImage = searchParams.get('userImage');
    const oauthError = searchParams.get('error');

    if (oauthError) {
      toast.error(oauthError);
      navigate('/');
      return;
    }

    if (!token) {
      toast.error('OAuth login failed. No token received.');
      navigate('/');
      return;
    }

    localStorage.setItem('token', token);
    setToken(token);

    try {
      const payload = JSON.parse(atob(token.split('.')[1] || ''));
      const role = payload?.role || 'PLAYER';
      const userId = payload?.userId || '';

      localStorage.setItem('role', role);
      setRole(role);
      if (userId) {
        localStorage.setItem('userId', userId);
        setUserId(userId);
      }
      const resolvedUserImage = userImage || localStorage.getItem('userImage') || 'avatars/m_avatar1.png';
      localStorage.setItem('userImage', resolvedUserImage);
      setUserImage(resolvedUserImage);
    } catch (error) {
      localStorage.setItem('role', 'PLAYER');
      setRole('PLAYER');
    }

    toast.success('Logged in with Google');
    navigate('/');
  }, [navigate, searchParams, setRole, setToken, setUserId, setUserImage]);

  return (
    <div className='oauth-success-page'>
      <div className='oauth-success-card'>
        <h2>Completing sign in...</h2>
        <p>Please wait while we redirect you.</p>
      </div>
    </div>
  );
};

export default OAuthSuccess;
