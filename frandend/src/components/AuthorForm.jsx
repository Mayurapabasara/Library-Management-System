import React, { useState } from "react";

export default function AuthorForm({ addAuthor }) {
  const [name, setName] = useState("");
  const [stream, setStream] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!name || !stream) return;
    addAuthor({ name, stream });
    setName("");
    setStream("");
  };

  return (
    <form className="author-form" onSubmit={handleSubmit}>
      <input
        type="text"
        placeholder="Author Name"
        value={name}
        onChange={(e) => setName(e.target.value)}
      />
      <input
        type="text"
        placeholder="Stream"
        value={stream}
        onChange={(e) => setStream(e.target.value)}
      />
      <button type="submit">Add Author</button>
    </form>
  );
}
