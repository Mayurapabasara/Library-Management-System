import React from 'react';
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';

// Import your pages
import Home from './pages/Home';
import Author from './pages/Author';
import Book from './pages/Book';
import Member from './pages/Member';

export default function App() {
  return (
    <BrowserRouter>
      <div>
        <h1>🌐 My React Router Example</h1>
        {/* Navigation Links */}
        <nav>
          <Link to="/">Home</Link> |{" "}
          <Link to="/author">Author</Link> |{" "}
          <Link to="/book">Book</Link> |{" "}
          <Link to="/member">Member</Link>
        </nav>

        <hr />

        {/* Define Routes */}
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/author" element={<Author />} />
          <Route path="/book" element={<Book />} />
          <Route path="/member" element={<Member />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App
