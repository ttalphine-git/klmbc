import { useEffect, useState } from 'react';
import { adsApi } from '../../api/ads';
import { ClassifiedAd } from '../../types';
import { X, MapPin, User, Phone } from 'lucide-react';

interface AdPopupProps {
  adId: number;
  onClose: () => void;
}

export default function AdPopup({ adId, onClose }: AdPopupProps) {
  const [ad, setAd] = useState<ClassifiedAd | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAd();
  }, [adId]);

  const fetchAd = async () => {
    try {
      const data = await adsApi.getAdById(adId);
      setAd(data);
    } catch (error) {
      console.error('Failed to fetch ad:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50"><div className="bg-white rounded-lg p-8">Loading...</div></div>;
  if (!ad) return null;

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50" onClick={onClose}>
      <div className="bg-white rounded-lg max-w-2xl w-full mx-4 max-h-[90vh] overflow-y-auto" onClick={(e) => e.stopPropagation()}>
        <div className="sticky top-0 bg-white border-b p-4 flex justify-between items-center">
          <h2 className="text-2xl font-bold">{ad.title}</h2>
          <button onClick={onClose} className="text-gray-600 hover:text-gray-900">
            <X size={24} />
          </button>
        </div>

        <div className="p-6">
          {ad.imageUrls.length > 0 && (
            <div className="mb-6">
              <img src={ad.imageUrls[0]} alt={ad.title} className="w-full h-64 object-cover rounded-lg" />
            </div>
          )}

          <div className="grid grid-cols-2 gap-4 mb-6">
            {ad.price && (
              <div className="bg-blue-50 p-3 rounded">
                <p className="text-gray-600 text-sm">Price</p>
                <p className="text-xl font-bold text-blue-600">₹{ad.price}</p>
              </div>
            )}
            {ad.category && (
              <div className="bg-green-50 p-3 rounded">
                <p className="text-gray-600 text-sm">Category</p>
                <p className="font-semibold">{ad.category}</p>
              </div>
            )}
          </div>

          <div className="mb-6">
            <h3 className="font-semibold mb-2">Description</h3>
            <p className="text-gray-700">{ad.description}</p>
          </div>

          <div className="mb-6 flex items-center gap-2 text-gray-700">
            <MapPin size={18} />
            <span>{ad.locationName}</span>
          </div>

          <div className="border-t pt-4">
            <h3 className="font-semibold mb-3">Seller Information</h3>
            <div className="flex gap-3">
              {ad.userProfileImage && (
                <img src={ad.userProfileImage} alt={ad.userName} className="w-12 h-12 rounded-full object-cover" />
              )}
              <div>
                <p className="font-semibold flex items-center gap-2">
                  <User size={16} />
                  {ad.userName}
                </p>
                {ad.userPhone && (
                  <p className="text-gray-600 flex items-center gap-2 text-sm">
                    <Phone size={14} />
                    {ad.userPhone}
                  </p>
                )}
                <p className="text-gray-600 text-sm">{ad.userEmail}</p>
              </div>
            </div>
          </div>

          {ad.imageUrls.length > 1 && (
            <div className="mt-6">
              <h3 className="font-semibold mb-3">More Images</h3>
              <div className="grid grid-cols-3 gap-2">
                {ad.imageUrls.slice(1).map((url, idx) => (
                  <img key={idx} src={url} alt={`${ad.title}-${idx}`} className="w-full h-24 object-cover rounded" />
                ))}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
