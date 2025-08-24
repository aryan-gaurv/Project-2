import { createContext, useEffect, useState } from "react";
import { AppConstants } from "../util/constants";
import axios from "axios";
import { toast } from "react-toastify";

export const AppContext=createContext();
export const AppContextProvider=(props)=>{
    axios.defaults.withCredentials=true;
    const [isLoggedIn,setIsLoggedIn]=useState(false);
    const [userData,setUserData]=useState(null);
    const backend_URL=AppConstants.BACEND_URL;
    const getUserData=async()=>{
        try{
            const response= await axios.get(backend_URL+"/profile");
            if(response.status===200){
                setUserData(response.data);
            }else{
                toast.error("Could not fetch user data");
            }
        }catch(err){
            toast.error(err.message);
        }
    }

    const getAuthState= async()=>{
        try{
           const response=await axios.get(backend_URL+"/is-authenticated");
           if(response.status===200 && response.data===true){
            setIsLoggedIn(true);
            await getUserData();
           }
           else{
            setIsLoggedIn(false)
           }
        }catch(err){
            if(err.response){
                const msg=err.response.data.message||"Check Failed";
                toast.error(msg);
            }else{
                toast.error(err.message);
            }
            setIsLoggedIn(false);
        }
    }
    useEffect(()=>{
        getAuthState();
    },[])

    const contextValue={
        backend_URL,
        isLoggedIn,
        setIsLoggedIn,
        userData,
        setUserData,
        getUserData
    }

    return(
        <AppContext.Provider value={contextValue}>
            {props.children}
        </AppContext.Provider>
    )

}