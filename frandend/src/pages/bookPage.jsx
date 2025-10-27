import React, { useState , useEffect } from "react";
import BookForm from "../components/BookForm";
import {api} from '../../src/service'

export default function BookPage() {
  // Sample book data
  const [books, setBooks] = useState([

  ]);

  const addBook = (book) => {
    api.addBook(book);
    setBooks([...books, { ...book, id: Date.now() }]);
  };

  const deleteBook = (id) => {
    api.deleteBook(id);
    setBooks(books.filter((book) => book.id !== id));
  };

  const updateBook = (id, updatedBook) => {
    setBooks(books.map((book) => (book.id === id ? updatedBook : book)));
  };

  useEffect(() => {
    api.getBook()
        .then(
            res => setBooks(res.data)
        )
  }, []);

  return (
    <div className="page-container">
      <h2>📘 Manage Books</h2>
      <BookForm addBook={addBook} />

      <div className="book-table">
        <table>
          <thead>
            <tr>
              <th>Title</th>
              <th>Price</th>
              <th>Author</th>
              <th>Available</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {books.map((book) => (
              <tr key={book.id}>
                <td>{book.title}</td>
                <td>₹{book.price}</td>
                <td>{book.author?.name || "Unknown"}</td>
                <td>{book.available ? "Yes" : "No"}</td>
                <td>
                  <button
                    className="update-btn"
                    onClick={() => {
                      const newTitle = prompt("Enter new title", book.title);
                      const newPrice = prompt("Enter new price", book.price);
                      const newAuthor = prompt("Enter new author", book.author);
                      const newAvailable = prompt(
                        "Is available? (yes/no)",
                        book.available ? "yes" : "no"
                      );
                      if (newTitle && newPrice && newAvailable) {
                        updateBook(book.id, {
                          id: book.id,
                          title: newTitle,
                          price: parseFloat(newPrice),
                          author: newAuthor,
                          available: newAvailable.toLowerCase() === "yes",
                        });
                      }
                    }}
                  >
                    Update
                  </button>
                  <button
                    className="delete-btn"
                    onClick={() => deleteBook(book.id)}
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
