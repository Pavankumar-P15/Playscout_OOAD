import React, { useEffect } from 'react';
import './Privacy.css';

const Privacy = () => {
  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);

  return (
    <div className='privacy-policy'>
      <h1>Privacy Policy</h1>
      <p><strong>Effective Date:</strong> March 25, 2026</p>
      <p>
        At PlayScout (we, us, our), we value your privacy and are committed to protecting your personal
        information. This Privacy Policy outlines how we collect, use, and safeguard your information when
        you visit our website or use our services.
      </p>
      <p>
        By accessing or using PlayScout, you agree to the collection, use, and disclosure of your information
        as described in this Privacy Policy. If you do not agree with this policy, please do not use our services.
      </p>

      <h2>1. Information We Collect</h2>
      <p>We collect both personal and non-personal information to provide and improve our services.</p>

      <h3>1.1 Personal Information</h3>
      <p>You may provide personal information when you:</p>
      <ul>
        <li>Register for an account</li>
        <li>Book a sports facility</li>
        <li>Join a game or interact with other users</li>
        <li>Contact customer support</li>
      </ul>

      <h2>2. How We Use Your Information</h2>
      <p>We use the information we collect to:</p>
      <ul>
        <li>Provide and manage your bookings for sports facilities.</li>
        <li>Facilitate user interactions, including chat features and game arrangements.</li>
        <li>Communicate with you about your account, bookings, and service-related announcements.</li>
      </ul>

      <h2>3. Security of Your Information</h2>
      <p>
        We take reasonable precautions to protect your information from unauthorized access, use, or disclosure.
      </p>

      <h2>4. Contact Us</h2>
      <p>
        PlayScout Support
        <br />
        Email: contact@playscout.com
      </p>
    </div>
  );
};

export default Privacy;
