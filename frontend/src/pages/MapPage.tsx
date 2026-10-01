import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import MapContainer from '../components/ads/MapContainer';
import { Plus, LogOut, LogIn, User as UserIcon } from 'lucide-react';

export default function MapPage() {
  const navigate = useNavigate();
  const { isAuthenticated, user, logout } = useAuthStore();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <div className="relative w-full h-screen">
      <MapContainer />

      <div className="app-title">
        <div className="app-title-text">Classified Ads Kerala</div>
      </div>

      <div className="absolute top-6 right-6 z-[1000] flex gap-3">
        {isAuthenticated ? (
          <>
            <button
              onClick={() => navigate('/my-ads')}
              className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg flex items-center gap-2 shadow-lg"
            >
              <Plus size={20} />
              Post Ad
            </button>
            <button
              onClick={() => navigate('/profile')}
              className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg flex items-center gap-2 shadow-lg"
            >
              <UserIcon size={20} />
              {user?.fullName}
            </button>
            <button
              onClick={handleLogout}
              className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-lg flex items-center gap-2 shadow-lg"
            >
              <LogOut size={20} />
              Logout
            </button>
          </>
        ) : (
          <>
            <button
              onClick={() => navigate('/login')}
              className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg flex items-center gap-2 shadow-lg"
            >
              <LogIn size={20} />
              Login
            </button>
            <button
              onClick={() => navigate('/register')}
              className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg flex items-center gap-2 shadow-lg"
            >
              Sign Up
            </button>
          </>
        )}
      </div>
    </div>
  );
}
