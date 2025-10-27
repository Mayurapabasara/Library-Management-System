import React, { useState } from "react";

export default function BookForm({ addBook }) {
  const [title, setTitle] = useState("");
  const [price, setPrice] = useState("");
  const [author, setAuthor] = useState("");
  const [available, setAvailable] = useState(true);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!title || !price) return;
    addBook({ title, price: parseFloat(price) , author:{"name":author} ,available });
    setTitle("");
    setPrice("");
    setAuthor("");
    setAvailable(true);
  };

  return (
    <form className="book-form" onSubmit={handleSubmit}>
      <input
        type="text"
        placeholder="Book Title"
        value={title}
        onChange={(e) => setTitle(e.target.value)}
      />
      <input
        type="number"
        placeholder="Price"
        value={price}
        onChange={(e) => setPrice(e.target.value)}
      />
      <input
        type="text"
        placeholder="Book Author"
        value={author}
        onChange={(e) => setAuthor(e.target.value)}
      />
      <select
        value={available}
        onChange={(e) => setAvailable(e.target.value === "true")}
      >
        <option value="true">Available</option>
        <option value="false">Not Available</option>
      </select>
      <button type="submit">Add Book</button>
    </form>
  );
}
