import React from 'react';
import './Footer.css';
import { assets } from '../../assets/assets';
import { Link } from 'react-router-dom';

const Footer = () => {
  return (
    <div className='footer' id='footer'>
      <div className="footer-content">
        <div className="footer-content-left">
            <img src={assets.logo} id='footer-logo' alt="Logo" />
        </div>
        
        <div className="footer-content-center">
          <h2>Company</h2>
          <ul>
            <Link to='privacy-policy'><li>Privacy Policy</li></Link>
            <li>Contact Us</li>
            <li className='contact'>+91-9898989898</li>
            <li className='contact'>contact@playscout.com</li>
          </ul>
        </div>

        <div className="footer-content-right">
          <h2>Follow Us</h2>
          <div className="footer-social-icons">
              <img src={assets.facebook_icon} alt="Facebook" />
              <img src={assets.twitter_icon} alt="Twitter" />
              <img src={assets.linkedin_icon} alt="LinkedIn" />
          </div>
        </div>
      </div>
      <hr />
      <p className="footer-copyright">Copyright 2026 @ PlayScout.com -All Rights Reserved</p>
    </div>
  );
};

export default Footer;