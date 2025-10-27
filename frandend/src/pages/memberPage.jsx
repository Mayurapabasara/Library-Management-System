import React, { useState, useEffect } from "react";
import MemberForm from "../components/MemberForm";

export default function MemberPage() {
  // Sample data
  const [members, setMembers] = useState([
    { id: 1, name: "John Doe", address: "Colombo", phone: "0771234567" },
  ]);

  const [books] = useState([
    { id: 1, title: "Harry Potter", price: 500 },
    { id: 2, title: "Game of Thrones", price: 750 },
  ]);

  const addMember = (member) => {
    api.addMember(member)
    setMembers([...members, { ...member, id: Date.now() }]);
  };

  const deleteMember = (id) => {
    api.deleteAuthor(id);
    setMembers(members.filter((member) => member.id !== id));
  };

  const updateMember = (id, updatedMember) => {
    setMembers(
      members.map((member) => (member.id === id ? updatedMember : member))
    );
  };

  const buyBook = (memberId, book) => {
    alert(
      `${members.find((m) => m.id === memberId).name} bought "${book.title}" for ₹${book.price}`
    );
  };

  useEffect(() => {
    api.getMember()
        .then(
            res => setMember(res.data)
        )
  }, []);


  return (
    <div className="page-container">
      <h2>👤 Manage Members</h2>

      {/* Add Member Form */}
      <MemberForm addMember={addMember} />

      {/* Members Table */}
      <div className="member-table">
        <table>
          <thead>
            <tr>
              <th>Name</th>
              <th>Address</th>
              <th>Phone</th>
              <th>Actions</th>
              <th>Buy Book</th>
            </tr>
          </thead>
          <tbody>
            {members.map((member) => (
              <tr key={member.id}>
                <td>{member.name}</td>
                <td>{member.address}</td>
                <td>{member.phone}</td>
                <td>
                  <button
                    className="update-btn"
                    onClick={() => {
                      const newName = prompt("Enter new name", member.name);
                      const newAddress = prompt(
                        "Enter new address",
                        member.address
                      );
                      const newPhone = prompt("Enter new phone", member.phone);
                      if (newName && newAddress && newPhone) {
                        updateMember(member.id, {
                          id: member.id,
                          name: newName,
                          address: newAddress,
                          phone: newPhone,
                        });
                      }
                    }}
                  >
                    Update
                  </button>
                  <button
                    className="delete-btn"
                    onClick={() => deleteMember(member.id)}
                  >
                    Delete
                  </button>
                </td>
                <td>
                  {books.map((book) => (
                    <button
                      key={book.id}
                      className="buy-btn"
                      onClick={() => buyBook(member.id, book)}
                    >
                      Buy "{book.title}"
                    </button>
                  ))}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
