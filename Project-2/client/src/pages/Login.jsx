import axios from 'axios';
import React, { useState } from 'react'
import { useContext } from 'react';
import { Link, useNavigate } from "react-router-dom";
import { toast } from 'react-toastify';
import { AppContext } from '../context/AppContext';

const Login = () => {
  const [isCreateAccount, setIsCreateAccount] = useState(false);
  const [name,setName]=useState("");
  const [email,setEmail]=useState("");
  const [password,setPassword]=useState("");
  const [loading,setLoading]=useState(false);
  const {backend_URL,setIsLoggedIn,getUserData}=useContext(AppContext);
 const navigate=useNavigate();
  const onSubmitHandler=async(e)=>{
    e.preventDefault();
    axios.defaults.withCredentials=true;
    setLoading(true);
    try{
      if(isCreateAccount){
        const response=await axios.post(`${backend_URL}/register`,{name,email,password});
        console.log(response);
        if(response.status===201){
          navigate("/");
          toast.success("Account Created Successfully");
      }else{
        toast.error("Email already exists");
      }
    }
    else{
        const response= await axios.post(`${backend_URL}/login`,{email,password})
        if(response.status===200){
         setIsLoggedIn(true);
         getUserData();
          navigate("/");
      }else{
        toast.error("Invalid Credentials");
      }
  }}catch(err){
      toast.error(err.response.data.message);
  }finally{
      setLoading(false);
  }}
  return <>
    <div className="position-relative min-vh-100 d-flex justify-content-center align-items-center"
    style={{background:"linear-gradient(90deg,#6a5af9,#8268f9)",border:"none"}}>
      <div style={{position:"absolute",top:"20px",left:"30px",display:"flex",alignItems:"center"}}>
        <Link to="/" style={{
          display:"flex",
          alignItems:"center",
          gap:5,
          fontWeight:"bold",
          fontSize:"24px",
          textDecoration:"none",
        }}>
          <img src="/resorce-tick.png" alt="logo" height={32} width={32} />
          <span className="fw-bold fs-4 text-light">Authify</span>
        </Link>
        </div>
        
          <div className="card p-4" style={{maxWidth:"400px",width:"100%"}}>
            <h2 className="text-center mb-4">
              {isCreateAccount?"Create Account":"Login"}
            </h2>
            <form onSubmit={onSubmitHandler}> 
              {isCreateAccount &&(
                <div className="mb-3">
                <label htmlFor="name" className="form-label">Full Name</label>
                <input type="text" id="name" className="form-control" value={name} placeholder="Enter Name" required 
                onChange={(e)=>setName(e.target.value)} />
              </div>
              ) }
              <div className="mb-3">
                <label htmlFor="email" className="form-label">Email Id</label>
                <input type="text" id="email" className="form-control" value={email} placeholder="Enter email" required 
                onChange={(e)=>setEmail(e.target.value)}/>
              </div>
              <div className="mb-3">
                <label htmlFor="password" className="form-label">Password</label>
                <input type="password" id="password" className="form-control" value={password} placeholder="***************" required onChange={(e)=>setPassword(e.target.value)}/>
              </div>
              <div className='d-flex justify-content-between mb-3'>
                <Link to={"/reset-password" } className="text-decoration-none">forget password?</Link>
              </div>
              <button type='submit' className='btn btn-primary w-100' disabled={loading}>
                {loading?"Loading...": isCreateAccount?"Sign Up":"Login"}
              </button>
            </form>
            <div className='text-center mt-3'>
              <p className="mb-0">
                {isCreateAccount?
                (<>
                Already have an account?
                <span className="text-decoration-underline" style={{cursor:"pointer"}} onClick={()=>setIsCreateAccount(false)}>Login Here</span>
                </>):(<>
                Don't have an account?
                <span className="text-decoration-underline" style={{cursor:"pointer"}} onClick={()=>setIsCreateAccount(true)}>Sign up</span>
                </>)}
              </p>
            </div>
          </div>
    </div>
  </>
}
export default Login;