import { createContext, useEffect, useState } from "react";
import { sport_list } from "../assets/assets";

export const StoreContext = createContext(null);

const StoreContextProvider = (props) => {
    const [menu, setMenu] = useState("home");
    const [selectedSport, setSelectedSport] = useState('Select Sport');
    const [selectedMeetSport, setSelectedMeetSport] = useState('Select Sport');
    const [selectedMeetLocation, setSelectedMeetLocation] = useState('Select Location');
    const [startDate, setStartDate] = useState(null);
    
    const url = import.meta.env.VITE_BACKEND_URL;
    const [token, setToken] = useState("");
    
    // Data lists
    const [COURT_list, setCourtList] = useState([]);
    const [player_list, setPlayerList] = useState([]);

    // Placeholder functions 
    // const fetchGameList = async () => {
    //     try {
    //         const response = await axios.get(url + "/api/game/game-list");
    //         setPlayerList(response.data.data);
    //     } catch (error) {
    //         console.error("Error fetching games:", error);
    //     }
    // }

    // const fetchVenueList = async () => {
    //     try {
    //         const response = await axios.get(url + "/api/venue/venue-list");
    //         setCourtList(response.data.data);
    //     } catch (error) {
    //         console.error("Error fetching venues:", error);
    //     }
    // }

    useEffect(() => {
        // Load token from localStorage if it exists
        if (localStorage.getItem("token")) {
            setToken(localStorage.getItem("token"));
        }
        
        // when API endpoints are ready
        // async function loadData() {
        //     await fetchVenueList();
        //     await fetchGameList();
        // }
        // loadData();
    }, []);

    const contextValue = {
        sport_list,
        menu,
        setMenu,
        selectedSport,
        setSelectedSport,
        selectedMeetSport,
        setSelectedMeetSport,
        selectedMeetLocation,
        setSelectedMeetLocation,
        startDate,
        setStartDate,
        url,
        token,
        setToken,
        COURT_list,
        setCourtList,
        player_list,
        setPlayerList,
        // fetchGameList,
        // fetchVenueList,
    };

    return (
        <StoreContext.Provider value={contextValue}>
            {props.children}
        </StoreContext.Provider>
    );
};

export default StoreContextProvider;