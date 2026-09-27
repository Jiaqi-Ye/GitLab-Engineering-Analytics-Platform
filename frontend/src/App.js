import './App.css';
import { Button } from 'react-bootstrap';
import { useState } from "react";


function App() {
  const [name, setName] = useState("");
  const [backendResponse, setBackendResponse] = useState("");

  const sendName = async (e) => {
    e.preventDefault();
    if (!name) return;
    try{
      const response = await fetch("http://localhost:8080/backend", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ name })
      });
      const data = await response.json();
      setBackendResponse(data.message);
    } catch (error) {
      console.error("Error sending name to backend:", error);
    }
  }

  return (
    <div className="App">
      <header className="App-header">
          <p>
          Frontend for skeleton requirement.
        </p>
        <form onSubmit={sendName}>
          <input 
            type="text" 
            placeholder="Enter your name" 
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
          <Button type="submit">Send name to backend</Button>
        </form>
        <br></br>
        <p>Backend Response: {backendResponse}</p>
      </header>
    </div>
  );
}

export default App;
