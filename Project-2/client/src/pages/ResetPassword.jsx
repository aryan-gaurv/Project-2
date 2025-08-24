import React, { useContext, useRef, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { AppConstants } from '../util/constants';
import { AppContext } from '../context/AppContext';
import axios from 'axios';
import { toast } from 'react-toastify';

const ResetPassword = () => {
  const inputRef=useRef([])
  const navigate=useNavigate();
  const[loading,setLoading]=useState(false)
  const [email,setemail]=useState("");
  const [newPassword,setNewPassword]=useState("");
  const [isEmailSent,setIsEmailSent]=useState(false);
  const [otp,setOtp]=useState("");
  const [isOtpSubmitted,setIsOtpSubmitted]=useState(false);
  axios.defaults.withCredentials=true;
  const {getUserData,isLoddedIn,userData,backend_URL}=useContext(AppContext)
   const handleChange=(e,index)=>{
        const value=e.target.value.replace(/\D/,"");
        e.target.value=value;
        if(value && index<5){
            inputRef.current[index+1].focus();
        }
    }
    const handleKeyDown=(e,index)=>{
        if(e.key==="Backspace" && !e.target.value && index>0){
            inputRef.current[index-1].focus();
        }
    }
    const handlePaste=(e)=>{
        e.preventDefault();
        const pasteData=e.clipboardData.getData("text").slice(0,6).split("");
        pasteData.forEach((digit,i)=>{
            if(inputRef.current[i]){
                inputRef.current[i].value=digit;
            }
        });
        const next=pasteData.length<6 ? pasteData.length : 5;
        inputRef.current[next].focus();
    }
const onSubmitEmail=async(e)=>{
  e.preventDefault();
  setLoading(true);
  try{
    const response=await axios.post(backend_URL+"/send-reset-otp?email="+email)
    if(response.status===200){
      toast.success("Otp sent to your email");
      setIsEmailSent(true);
    }else{
      toast.error("Could not send otp");
    }
  }catch(err){
    toast.error(err.response.data.message);
  }finally{
    setLoading(false);
  }
}

const handleVerify=()=>{
 const otp= inputRef.current.map(input=>input.value).join("");
 if(otp.length!==6){
  toast.error("Please enter the 6 digit otp");
  return;
 }
 setOtp(otp);
 setIsOtpSubmitted(true);
}
const onSubmitNewPassword=async(e)=>{
  e.preventDefault();
  setLoading(true);
try{
 const response= await axios.post(backend_URL+"/reset-password",{email,otp,newPassword})
  if(response.status===200){
    toast.success("Password reset successfully");
    navigate("/login");
}else{
  toast.error("Could not reset password");
}
}catch(err){
  toast.error(err.response.data.message);
}finally{
  setLoading(false);
}}
  return (
    <div className="d-flex align-items-center justify-content-center vh-100 position-relative" style={{background:"linear-gradient(90deg,#6a5af9,#8268f9)",border:"none"}}>

      <Link to="/" className="position-absolute top-0 start-0 p-4 d-flex align-items-center gap-2 text-decoration-none">
      <img src="/public/resorce-tick.png" alt="logo" height={32} width={32}/>
      <span className="fs-4 fw-semibold text-light">Authify</span>
      </Link>
      {/* Reset Password Form */}
      {!isEmailSent && (
        <div className="rounded-4 p-5 text-center bg-white" style={{width:'100%',maxWidth:'400px'}}>
          <h4 className='mb-2'>Reset Password</h4>
          <p className='mb-4'>Enter your register email address</p>
          <form onSubmit={onSubmitEmail}>
            <div className='input-group mb-4 bg-secondary bg-opacity-10 rounded-pill '>
              <span className='input-group-text bg-transparent border-0 ps-4'>
                <i className='bi bi-envelope'></i>
              </span>
              <input type="email" className='form-control bg-transparent border-0  ps-1 pe-4 rounded-end' placeholder='Enter email address' style={{height:'50px'}}
              onChange={(e)=>setemail(e.target.value)}
              value={email} required />
            </div>
            <button className='btn btn-primary w-100 py-2 ' type="submit">
              Submit
            </button>
          </form>
        </div>
      )}
      {!isOtpSubmitted && isEmailSent &&(
         <div className="p-4 rounded-4 shadow bg-white" style={{width:"400px"}}>
            <h4 className="text-center fw-bold mb-2 ">Email verify Otp</h4>
            <p className="text-center text-black mb-4">
                Enter the 6 digit code sent to your email address.
            </p>
            <div className="d-flex justify-content-between gap-2 mb-4 text-center text-white-50 mb-2">
                {[...Array(6)].map((_,i)=>(
                    <input
                    key={i}
                     type="text"
                    maxLength={1}
                    className="form-control text-center fs-4 otp-input" ref={el=>(inputRef.current[i]=el)}
                    onChange={(e)=>handleChange(e,i)}
                    onKeyDown={(e)=>handleKeyDown(e,i)}
                    onPaste={handlePaste}
                     />
                ))}
                
            </div>
            <button className="btn btn-primary w-100 fw-semibold" disabled={loading} onClick={handleVerify} >
                {loading ? "Verifying..." : "Verify email"}
            </button>
        </div>
      )}
      {isOtpSubmitted &&isEmailSent &&(
                  <div className='rounded-4 p-4 text-center bg-white' style={{width:"100%",maxWidth:"400px"}}>
                  <h4>New Password</h4>
                  <p className='mb-4'> Enter the new Password below</p>
                  <form onSubmit={onSubmitNewPassword} disabled={loading}>
                     <div className='input-group mb-4 bg-secondary bg-opacity-10 rounded-pill'>
                      <span className='input-group-text bg-transparent border-0 ps-4'>
                        <i className='bi bi-person-fill-lock'></i>
                      </span>
                      <input type="password" className='form-control bg-transparent border-0 ps-1 pe-4 rounded-end' placeholder="******************" onChange={(e)=>setNewPassword(e.target.value)} value={newPassword} required />
                     </div>
                     <button className='btn btn-primary w-100' type="submit" disabled={loading}>
                      {loading?"Loading...":"Submit" }
                       </button>
                  </form>
                  </div>
                )}
    </div>
  )
}

export default ResetPassword;