import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import axios from "axios";

export default function ViewUser() {
  const [user, setuser] = useState({
    name: "",
    email: "",
    username: "",
  });
  useEffect(()=>{
    loadUser()
  },[]);
  const { id } = useParams();
  const loadUser = async () => {
    const result=await axios.get(`http://localhost:8080/user/${id}`)
    setuser(result.data)
  };
  return (
    <div className="container">
      <div className="row">
        <div className="col-md-6 offset-md-3 border rounded p-4 mt-4 shadow">
          <h2 className="text-center m-4"> User Detail</h2>

          <div className="card">
            <div className="card-header">
              Details Of User id:
              <ul className="list-group list-group-flush">
                <li className="list-group-item">
                  <b>Name:</b>{user.name}
                </li>
                <li className="list-group-item">
                  <b>User Name:</b>{user.username}
                </li>
                <li className="list-group-item">
                  <b>E-mail:</b> {user.email}
                </li>
              </ul>
            </div>
          </div>
          <Link className="btn btn-primary my-2" to={"/"}>
            Back to Home
          </Link>
        </div>
      </div>
    </div>
  );
}
