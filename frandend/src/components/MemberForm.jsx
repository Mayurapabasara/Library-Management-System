import React, { useState } from "react";

export default function MemberForm({ addMember }) {
  const [name, setName] = useState("");
  const [address, setAddress] = useState("");
  const [phone, setPhone] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!name || !address || !phone) return;
    addMember({ name, address, phone }); //"phone" matchs backend "phone"
    setName("");
    setAddress("");
    setPhone("");
  };

  return (
    <form className="member-form" onSubmit={handleSubmit}>
      <input
        type="text"
        placeholder="Member Name"
        value={name}
        onChange={(e) => setName(e.target.value)}
      />
      <input
        type="text"
        placeholder="Address"
        value={address}
        onChange={(e) => setAddress(e.target.value)}
      />
      <input
        type="text"
        placeholder="Phone Number"
        value={phone}
        onChange={(e) => setPhone(e.target.value)}
      />
      <button type="submit">Add Member</button>
    </form>
  );
}
