import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import { ArrowLeft, Upload } from 'lucide-react';

export default function ProfilePage() {
  const navigate = useNavigate();
  const { user } = useAuthStore();
  const [isEditing, setIsEditing] = useState(false);

  const handleSaveProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    // TODO: Implement profile update
    setIsEditing(false);
  };

  if (!user) {
    return <div>Loading...</div>;
  }

  return (
    <div className="min-h-screen bg-gray-100 p-4">
      <div className="max-w-2xl mx-auto">
        <button
          onClick={() => navigate('/')}
          className="flex items-center gap-2 text-blue-600 hover:text-blue-700 font-semibold mb-8"
        >
          <ArrowLeft size={20} />
          Back to Map
        </button>

        <div className="bg-white rounded-lg shadow-lg p-8">
          <div className="flex justify-between items-start mb-6">
            <h1 className="text-3xl font-bold text-gray-800">My Profile</h1>
            <button
              onClick={() => setIsEditing(!isEditing)}
              className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg"
            >
              {isEditing ? 'Cancel' : 'Edit Profile'}
            </button>
          </div>

          <div className="flex gap-6 mb-8">
            {user.profileImageUrl ? (
              <img
                src={user.profileImageUrl}
                alt={user.fullName}
                className="w-24 h-24 rounded-full object-cover"
              />
            ) : (
              <div className="w-24 h-24 rounded-full bg-gray-300 flex items-center justify-center">
                <span className="text-2xl text-white">{user.fullName.charAt(0)}</span>
              </div>
            )}

            {!isEditing ? (
              <div className="flex-1">
                <h2 className="text-2xl font-bold text-gray-800">{user.fullName}</h2>
                <p className="text-gray-600">{user.email}</p>
                {user.phoneNumber && <p className="text-gray-600">{user.phoneNumber}</p>}
                {user.bio && <p className="text-gray-700 mt-2">{user.bio}</p>}
                <p className="text-sm text-gray-500 mt-2">
                  Member since {new Date(user.createdAt).toLocaleDateString()}
                </p>
              </div>
            ) : (
              <form onSubmit={handleSaveProfile} className="flex-1 space-y-4">
                <input
                  type="text"
                  placeholder="Full Name"
                  defaultValue={user.fullName}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <input
                  type="tel"
                  placeholder="Phone Number"
                  defaultValue={user.phoneNumber || ''}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <textarea
                  placeholder="Bio"
                  defaultValue={user.bio || ''}
                  rows={3}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <button
                  type="submit"
                  className="bg-green-600 hover:bg-green-700 text-white px-6 py-2 rounded-lg font-semibold w-full"
                >
                  Save Changes
                </button>
              </form>
            )}
          </div>

          {!isEditing && (
            <div className="border-t pt-6">
              <h3 className="text-lg font-semibold text-gray-800 mb-4">Upload New Avatar</h3>
              <label className="flex items-center justify-center w-full px-4 py-6 border-2 border-dashed border-gray-300 rounded-lg cursor-pointer hover:border-blue-500 transition">
                <Upload className="text-gray-400 mr-2" size={24} />
                <span className="text-gray-600">Click to upload new avatar</span>
                <input type="file" accept="image/*" className="hidden" />
              </label>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
