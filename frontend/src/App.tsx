import { useState } from 'react'
import './App.css'
import { ShortenForm } from './ShortenForm'

function App() {
  const [count, setCount] = useState(0)

  return (
    <>
        <div>
            <ShortenForm></ShortenForm>
        </div>
    </>
  )
}

export default App
