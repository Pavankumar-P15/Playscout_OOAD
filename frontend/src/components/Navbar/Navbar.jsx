import React, {useContext, useEffect} from 'react'
import './Navbar.css'
import { assets } from '../../assets/assets'
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { StoreContext } from '../../context/storeContextInstance'
import { Shield, ClipboardPen } from 'lucide-react';
import {toast} from "react-toastify"

const Navbar = ({setShowLogin, setShowAdmin}) => {

  const { menu, setMenu, token, setToken } = useContext(StoreContext)
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    const currentPath = location.pathname;
    if (currentPath === '/') {
      setMenu('home');
    } else if (currentPath === '/book') {
      setMenu('book');
    } else if (currentPath === '/meet') {
      setMenu('meet');
    } else if (currentPath === '/chat') {
      setMenu('chat');
    } else if (currentPath === '/news') {
      setMenu('news');
    } else {
      setMenu('');
    }
  }, [location.pathname]);

  return (
    <div className='navbar'>
      <img src={assets.logo} alt="" className='navbar-logo' />
      <ul className="navbar-menu">
      <Link to='/' onClick={() => setMenu("home")} className={menu === "home" ? "active" : ""}><li>Home</li></Link>
      <Link to='/book' onClick={() => setMenu("book")} className={menu === "book" ? "active" : ""}><li>Book</li></Link>
      <Link to='/meet' onClick={() => setMenu("meet")} className={menu === "meet" ? "active" : ""}><li>Meet</li></Link>
      <Link to='/chat' onClick={() => setMenu("chat")} className={menu === "chat" ? "active" : ""}><li>Chat</li></Link>
      <Link to='/news' onClick={() => setMenu("news")} className={menu === "news" ? "active" : ""}><li>News</li></Link>
      </ul>
      <div className="navbar-right">
        {!token
        ?<button onClick={() => setShowLogin(true)} className='navbar-login-button'><span className="navbar-login-text">Login / SignUp</span></button>
        :<div className='navbar-profile'>
            <img src={assets.profile_icon} alt="" />
            <ul className='navbar-profile-dropdown'>
              <Link to='upcoming'><li><ClipboardPen style={{ color: 'red', paddingBottom: '10px', paddingRight: '6px'}} size={26} /><p style={{ paddingRight: '10px'}}>Upcoming</p></li></Link>
              <hr />
              {isAdmin ? (
                <Link to="/admin">
                  <li><Shield style={{ color: 'red', paddingBottom: '10px', paddingRight: '6px'}} size={26} /><p>Admin</p></li>
                </Link>
              ) : (
                <li onClick={() => setShowAdmin(true)}>
                  <Shield style={{ color: 'red', paddingBottom: '10px', paddingRight: '6px'}} size={26} /><p>Become Admin</p>
                </li>
              )}
              <hr />
              <li onClick={logout}><img src={assets.logout_icon} alt="" /><p style={{ paddingLeft: '5px'}}>Logout</p></li>
              <hr />
            </ul>
         </div>}
        
      </div>
    </div>
  )
}

export default Navbar
