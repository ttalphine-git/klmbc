import { useEffect, useState } from 'react';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import L from 'leaflet';
import { useMapStore } from '../../store/mapStore';
import { adsApi } from '../../api/ads';
import AdPopup from './AdPopup';
import 'leaflet/dist/leaflet.css';

const markerIcon = new L.Icon({
  iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-shadow.png',
  iconSize: [25, 41],
  shadowSize: [41, 41],
  iconAnchor: [12, 41],
  shadowAnchor: [13, 41],
  popupAnchor: [1, -34],
});

export default function Map() {
  const { center, zoom, setCenter, setZoom, mapPoints, setMapPoints, isLoading, setIsLoading } =
    useMapStore();
  const [selectedAdId, setSelectedAdId] = useState<number | null>(null);

  useEffect(() => {
    fetchMapPoints();
  }, []);

  const fetchMapPoints = async () => {
    setIsLoading(true);
    try {
      const minLat = 8.0;
      const maxLat = 12.5;
      const minLon = 73.5;
      const maxLon = 78.5;
      const points = await adsApi.getMapPoints(minLat, maxLat, minLon, maxLon);
      setMapPoints(points);
    } catch (error) {
      console.error('Failed to fetch map points:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleMapMove = (mapInstance: L.Map) => {
    const bounds = mapInstance.getBounds();
    const minLat = bounds.getSouth();
    const maxLat = bounds.getNorth();
    const minLon = bounds.getWest();
    const maxLon = bounds.getEast();
    fetchMapPointsForBounds(minLat, maxLat, minLon, maxLon);
  };

  const fetchMapPointsForBounds = async (minLat: number, maxLat: number, minLon: number, maxLon: number) => {
    try {
      const points = await adsApi.getMapPoints(minLat, maxLat, minLon, maxLon);
      setMapPoints(points);
    } catch (error) {
      console.error('Failed to fetch map points:', error);
    }
  };

  return (
    <MapContainer
      center={center}
      zoom={zoom}
      style={{ height: '100vh', width: '100%' }}
      onMove={(e) => handleMapMove(e.target)}
    >
      <TileLayer
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
      />

      {mapPoints.map((point) => (
        <Marker
          key={point.id}
          position={[point.latitude, point.longitude]}
          icon={markerIcon}
          eventHandlers={{
            click: () => setSelectedAdId(point.id),
          }}
        >
          <Popup>
            <div className="w-48">
              <h3 className="font-bold text-sm">{point.title}</h3>
              {point.thumbnailUrl && (
                <img src={point.thumbnailUrl} alt={point.title} className="w-full h-24 object-cover mt-2 rounded" />
              )}
              <p className="text-xs text-gray-600 mt-1">{point.locationName}</p>
              <button
                onClick={() => setSelectedAdId(point.id)}
                className="mt-2 w-full bg-blue-600 text-white py-1 rounded text-sm hover:bg-blue-700"
              >
                View Details
              </button>
            </div>
          </Popup>
        </Marker>
      ))}

      {selectedAdId && <AdPopup adId={selectedAdId} onClose={() => setSelectedAdId(null)} />}
    </MapContainer>
  );
}
