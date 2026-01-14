import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import Layout from './components/Layout'
import RecordList from './pages/RecordList'
import AssetManagement from './pages/AssetManagement'
import LiabilityManagement from './pages/LiabilityManagement'
import Statistics from './pages/Statistics'
import OperationLogList from './pages/OperationLogList'
import Login from './pages/Login'
import { auth } from './utils/auth'

function ProtectedRoute({ children }: { children: React.ReactElement }) {
  if (!auth.isAuthenticated()) {
    return <Navigate to="/login" replace />
  }
  return children
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route
          path="/*"
          element={
            <ProtectedRoute>
              <Layout>
                <Routes>
                  <Route path="/" element={<Navigate to="/records" replace />} />
                  <Route path="/records" element={<RecordList />} />
                  <Route path="/assets" element={<AssetManagement />} />
                  <Route path="/liabilities" element={<LiabilityManagement />} />
                  <Route path="/statistics" element={<Statistics />} />
                  <Route path="/operation-logs" element={<OperationLogList />} />
                </Routes>
              </Layout>
            </ProtectedRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  )
}

export default App
