import React from 'react';
import { Link } from 'react-router-dom';

const PaymentCancel = () => {
  return (
    <div className="payment-cancel" style={{ padding: '2rem', textAlign: 'center' }}>
      <h2>Payment Canceled</h2>
      <p>Your transaction was canceled. You can try again.</p>
      <Link to="/book">Back to Booking</Link>
    </div>
  );
};

export default PaymentCancel;
