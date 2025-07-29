import React from "react";
import { Link } from "react-router-dom";

export default function Navbar() {
  return (
    <nav className="navbar navbar-dark bg-primary justify-content-between">
      <Link className="navbar-brand" to="/">CRUD Application</Link>
        <Link className="btn btn-outline-light my-2 my-sm-0" to="/adduser">
          Add User
        </Link>
    </nav>
  );
}
