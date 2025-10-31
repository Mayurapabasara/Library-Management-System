import React, { useState, useEffect } from "react";
import axios from "axios";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const SellerForm = () => {
  const [sellers, setSellers] = useState([]);
  const [formData, setFormData] = useState({
    sellerName: "",
    contactNumber: "",
    email: "",
    address: "",
    companyName: "",
  });

  const [loading, setLoading] = useState(false);

  // Load all sellers on component mount
  useEffect(() => {
    fetchSellers();
  }, []);

  const fetchSellers = async () => {
    try {
      const res = await axios.get("http://localhost:8080/api/sellers");
      setSellers(res.data);
    } catch (err) {
      console.error("Error fetching sellers:", err);
      toast.error("Failed to load sellers!");
    }
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!formData.sellerName || !formData.contactNumber || !formData.email) {
      toast.warn("Please fill all required fields!");
      return;
    }

    setLoading(true);
    try {
      await axios.post("http://localhost:8080/api/sellers", formData);
      toast.success("Seller added successfully!");
      setFormData({
        sellerName: "",
        contactNumber: "",
        email: "",
        address: "",
        companyName: "",
      });
      fetchSellers();
    } catch (error) {
      console.error("Error adding seller:", error);
      toast.error("Error adding seller!");
    } finally {
      setLoading(false);
    }
  };

  const deleteSeller = async (id) => {
    if (!window.confirm("Are you sure you want to delete this seller?")) return;
    try {
      await axios.delete(`http://localhost:8080/api/sellers/${id}`);
      toast.success("Seller deleted successfully!");
      fetchSellers();
    } catch (error) {
      console.error("Error deleting seller:", error);
      toast.error("Failed to delete seller!");
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 p-8">
      <ToastContainer />
      <div className="max-w-4xl mx-auto bg-white shadow-lg rounded-2xl p-8">
        <h2 className="text-2xl font-semibold text-center text-indigo-600 mb-6">
          📚 Seller Management
        </h2>

        {/* Add Seller Form */}
        <form
          onSubmit={handleSubmit}
          className="grid grid-cols-1 md:grid-cols-2 gap-6"
        >
          <div>
            <label className="block font-medium text-gray-700">Seller Name</label>
            <input
              type="text"
              name="sellerName"
              value={formData.sellerName}
              onChange={handleChange}
              className="w-full mt-1 p-2 border border-gray-300 rounded-lg"
              required
            />
          </div>

          <div>
            <label className="block font-medium text-gray-700">Contact Number</label>
            <input
              type="text"
              name="contactNumber"
              value={formData.contactNumber}
              onChange={handleChange}
              className="w-full mt-1 p-2 border border-gray-300 rounded-lg"
              required
            />
          </div>

          <div>
            <label className="block font-medium text-gray-700">Email</label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              className="w-full mt-1 p-2 border border-gray-300 rounded-lg"
              required
            />
          </div>

          <div>
            <label className="block font-medium text-gray-700">Address</label>
            <input
              type="text"
              name="address"
              value={formData.address}
              onChange={handleChange}
              className="w-full mt-1 p-2 border border-gray-300 rounded-lg"
            />
          </div>

          <div className="md:col-span-2">
            <label className="block font-medium text-gray-700">Company Name</label>
            <input
              type="text"
              name="companyName"
              value={formData.companyName}
              onChange={handleChange}
              className="w-full mt-1 p-2 border border-gray-300 rounded-lg"
            />
          </div>

          <div className="md:col-span-2 text-center">
            <button
              type="submit"
              disabled={loading}
              className="bg-indigo-600 text-white py-2 px-8 rounded-lg hover:bg-indigo-700 disabled:opacity-50"
            >
              {loading ? "Saving..." : "Add Seller"}
            </button>
          </div>
        </form>
      </div>

      {/* Seller List Table */}
      <div className="max-w-5xl mx-auto mt-10 bg-white shadow-md rounded-2xl p-6">
        <h3 className="text-xl font-semibold text-gray-800 mb-4">Seller List</h3>
        {sellers.length === 0 ? (
          <p className="text-gray-500 text-center">No sellers found.</p>
        ) : (
          <table className="min-w-full border border-gray-200 rounded-lg">
            <thead>
              <tr className="bg-indigo-600 text-white">
                <th className="py-2 px-4 text-left">ID</th>
                <th className="py-2 px-4 text-left">Name</th>
                <th className="py-2 px-4 text-left">Email</th>
                <th className="py-2 px-4 text-left">Company</th>
                <th className="py-2 px-4 text-center">Actions</th>
              </tr>
            </thead>
            <tbody>
              {sellers.map((seller) => (
                <tr key={seller.sellerId} className="border-b hover:bg-gray-50">
                  <td className="py-2 px-4">{seller.sellerId}</td>
                  <td className="py-2 px-4">{seller.sellerName}</td>
                  <td className="py-2 px-4">{seller.email}</td>
                  <td className="py-2 px-4">{seller.companyName}</td>
                  <td className="py-2 px-4 text-center">
                    <button
                      onClick={() => deleteSeller(seller.sellerId)}
                      className="bg-red-500 text-white px-3 py-1 rounded-lg hover:bg-red-600"
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};

export default SellerForm;
