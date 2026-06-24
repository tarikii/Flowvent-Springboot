import { Route, Routes } from 'react-router-dom'
import './App.css'
import { Navbar } from './components/Navbar'
import { HomePage } from './pages/HomePage'
import { LoginPage } from './pages/LoginPage'
import { RegisterPage } from './pages/RegisterPage'
import { EventsPage } from './pages/EventsPage'
import { MyTicketsPage } from './pages/MyTicketsPage'

function App() {
  return (
    <div className="app">
      <Navbar />

      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/events" element={<EventsPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/my-tickets" element={<MyTicketsPage />} />
      </Routes>
    </div>
  )
}

export default App