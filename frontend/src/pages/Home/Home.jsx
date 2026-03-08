import React, {useEffect} from "react";
import "./Home.css";

const Home = () => {

  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);

  return (
    <div className='home'>
    </div>
  )
}

export default Home