import './App.css'
import { ShortenForm } from './ShortenForm'
import { UrlList } from './UrlList'

function App() {
  return (
    <main className="max-w-2xl mx-auto py-10 px-4">
      <h1 className="text-2xl font-bold mb-6">URL Shortener</h1>
      <ShortenForm />
      <UrlList />
    </main>
  )
}

export default App
