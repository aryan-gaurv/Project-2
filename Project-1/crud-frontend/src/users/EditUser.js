import axios from 'axios'
import React, { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'

export default function EditUser() {
    let navigate=useNavigate();
    const {id}=useParams()
    const[user,setusers]=useState({
        username:"",
        name:"",
        email:""
    })
    const {username,name,email}=user
    const onInputChange=(e)=>{
        setusers({...user,[e.target.name]:e.target.value})
    }
    useEffect(()=>{
        loadUser()},[])
    
    const onSubmit=async(e)=>{
        e.preventDefault();
        await axios.put(`http://localhost:8080/user/${id}`,user)
        navigate("/")
    }
    const loadUser=async ()=>{
        const response=await axios.get(`http://localhost:8080/user/${id}`)
        setusers(response.data)
    }
  return (
    <div className="container">
        <div className="row">
            <div className="col-md-6 offset-md-3 border rounded p-4 mt-4 shadow" >
                <h2 className="text-center m-4">Edit User</h2>
                <form onSubmit={(e)=>onSubmit(e)}>
                <div className="mb-3">
                    <label htmlFor="Name" className="form-label">Name</label>
                    <input type={"text"} className="form-control" id="Name" name="name" placeholder="Enter your Username" value={name} onChange={(e)=>onInputChange(e)}/>
                </div>
                <div className="mb-3">
                    <label htmlFor="Username" className="form-label">Username</label>
                    <input type={"text"} className="form-control" id="Username" name="username" placeholder="Enter your name" value={username} onChange={(e)=>onInputChange(e)}/>
                </div>
                <div className="mb-3">
                    <label htmlFor="Email" className="form-label">Email</label>
                    <input type={"text"} className="form-control" id="Email" name="email" placeholder="Enter your Email" value={email} onChange={(e)=>onInputChange(e)}/>
                </div>
                <button type={"submit"} className="btn btn-outline-primary">Submit</button>
                <Link type={"submit"} className="btn btn-outline-danger m-2"to="/">Cancel</Link></form>
            </div>
        </div>
    </div>
  )
}
