import React, { useEffect, useState } from "react";
import AuthorForm from "../components/AuthorForm";
import {api} from '../../src/service'

export default function AuthorPage() {
  // Sample state for authors
  const [authors, setAuthors] = useState([]);

  const addAuthor = (author) => {
    //create a put function
    api.addAuthor(author);
    setAuthors([...authors, { ...author, id: Date.now() }]);

  };

  const deleteAuthor = (id) => {
    //create a delete function
    api.deleteAuthor(id);
    setAuthors(authors.filter((author) => author.id !== id));
  };

  const updateAuthor = (id, updatedAuthor) => {
    setAuthors(
      authors.map((author) => (author.id === id ? updatedAuthor : author))
    );
  };

  //create a get function
  useEffect(() => {
    api.getAuthor()
        .then(
            res => setAuthors(res.data)
            
        )
        
  } , []);

  return (
    <div className="page-container">
      <h2>🧑‍💼 Manage Authors</h2>
      <AuthorForm addAuthor={addAuthor} />

      <div className="author-table">
        <table>
          <thead>
            <tr>
              <th>Name</th>
              <th>Stream</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {authors.map((author) => (
              <tr key={author.id}>
                <td>{author.name}</td>
                <td>{author.stream}</td>
                <td>
                  <button
                    className="update-btn"
                    onClick={() => {
                      const newName = prompt("Enter new name", author.name);
                      const newStream = prompt(
                        "Enter new stream",
                        author.stream
                      );
                      if (newName && newStream) {
                        updateAuthor(author.id, {
                          id: author.id,
                          name: newName,
                          stream: newStream,
                        });
                      }
                    }}
                  >
                    Update
                  </button>
                  <button
                    className="delete-btn"
                    onClick={() => deleteAuthor(author.id)}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
