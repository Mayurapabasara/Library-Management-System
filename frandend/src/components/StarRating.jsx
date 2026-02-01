import { useEffect, useState } from "react";
import { FaStar } from "react-icons/fa";
import axios from "axios";

function StarRating({ itemId }) {

  const [savedRating, setSavedRating] = useState(0);
  const [selectedRating, setSelectedRating] = useState(0);
  const [ratingCount, setRatingCount] = useState(0);

  // 🔹 GET rating function
  const fetchRating = async () => {
    try {
      const response = await axios.get(
        `http://localhost:8080/api/rating/${itemId}`
      );

      if (response.data.length > 0) {
        const total = response.data.reduce((sum, r) => sum + r.stars, 0);
        const avg = Math.round(total / response.data.length);

        setSavedRating(avg);
        setRatingCount(response.data.length);
      } else {
        setSavedRating(0);
        setRatingCount(0);
      }
    } catch (error) {
      console.error("GET rating failed:", error);
    }
  };

  // 🔹 Load rating on component mount
  useEffect(() => {
    fetchRating();
  }, [itemId]);

  // 🔹 POST rating
  const submitRating = async () => {
    try {
      await axios.post("http://localhost:8080/api/rating", {
        itemId,
        stars: selectedRating,
      });

      setSelectedRating(0);   // reset UI
      fetchRating();          // refresh GET value
    } catch (error) {
      console.error("POST rating failed:", error);
    }
  };

  return (
    <div>
      {/* ⭐ GET VALUE */}
      <h4>Average Rating ({ratingCount})</h4>
      <div style={{ display: "flex", gap: "5px" }}>
        {[...Array(5)].map((_, index) => {
          const value = index + 1;
          return (
            <FaStar
              key={index}
              size={24}
              color={value <= savedRating ? "#ffc107" : "#e4e5e9"}
            />
          );
        })}
      </div>

      <hr />

      {/* ⭐ POST VALUE */}
      <h4>Your Rating</h4>
      <div style={{ display: "flex", gap: "5px" }}>
        {[...Array(5)].map((_, index) => {
          const value = index + 1;
          return (
            <FaStar
              key={index}
              size={30}
              color={value <= selectedRating ? "#ffc107" : "#e4e5e9"}
              onClick={() => setSelectedRating(value)}
              style={{ cursor: "pointer" }}
            />
          );
        })}
      </div>

      <button
        onClick={submitRating}
        disabled={selectedRating === 0}
        style={{ marginTop: "10px" }}
      >
        Submit Rating
      </button>
    </div>
  );
}

export default StarRating;
