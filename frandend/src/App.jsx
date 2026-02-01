import React from "react";
import { BrowserRouter, Routes, Route, NavLink } from "react-router-dom";

import HomePage from "./pages/homePage";
import AuthorPage from "./pages/authorPage";
import BookPage from "./pages/bookPage";
import MemberPage from "./pages/memberPage";
import NotFoundPage from "./pages/notFoundPage";
import StarRating from "./components/StarRating";


export default function App() {
  return (
    <BrowserRouter>
      <header className="navbar">
        <h1 className="logo">📚 Library Management</h1>
        <nav>
          <NavLink to="/" className="nav-link">Home</NavLink>
          <NavLink to="/author" className="nav-link">Author</NavLink>
          <NavLink to="/book" className="nav-link">Book</NavLink>
          <NavLink to="/member" className="nav-link">Member</NavLink>
        </nav>
      </header>

      <main className="main-content">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/author" element={<AuthorPage />} />
          <Route path="/book" element={<BookPage />} />
          <Route path="/member" element={<MemberPage />} />
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </main>

      <footer className="footer">
        &copy; 2025 Library Management System. All rights reserved.
      </footer>

      <div className="App">
        <h1>Rate this item</h1>
        <StarRating itemId={1} />
      </div>
    </BrowserRouter>
  );
}
