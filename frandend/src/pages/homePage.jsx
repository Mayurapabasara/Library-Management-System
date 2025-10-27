import React from "react";
import { Link } from "react-router-dom";

export default function homePage() {
  return (
    <div className="home-container">
      <section className="hero">
        <h2>Welcome to the Library Management System</h2>
        <p>Manage books, authors, and members easily!</p>
      </section>

      <section className="cards">
        <Link to="/author" className="card">
          <h3>Authors</h3>
          <p>View and manage author information</p>
        </Link>
        <Link to="/book" className="card">
          <h3>Books</h3>
          <p>View and manage library books</p>
        </Link>
        <Link to="/member" className="card">
          <h3>Members</h3>
          <p>Manage library members easily</p>
        </Link>
      </section>
    </div>
  );
}
