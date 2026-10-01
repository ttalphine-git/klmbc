import { create } from 'zustand';
import { AdMapPoint, ClassifiedAd } from '../types';

interface MapStore {
  center: [number, number];
  zoom: number;
  selectedAdId: number | null;
  selectedAd: ClassifiedAd | null;
  mapPoints: AdMapPoint[];
  isLoading: boolean;
  setCenter: (center: [number, number]) => void;
  setZoom: (zoom: number) => void;
  setSelectedAdId: (id: number | null) => void;
  setSelectedAd: (ad: ClassifiedAd | null) => void;
  setMapPoints: (points: AdMapPoint[]) => void;
  setIsLoading: (loading: boolean) => void;
}

export const useMapStore = create<MapStore>((set) => ({
  center: [10.8505, 76.2711],
  zoom: 8,
  selectedAdId: null,
  selectedAd: null,
  mapPoints: [],
  isLoading: false,
  setCenter: (center) => set({ center }),
  setZoom: (zoom) => set({ zoom }),
  setSelectedAdId: (id) => set({ selectedAdId: id }),
  setSelectedAd: (ad) => set({ selectedAd: ad }),
  setMapPoints: (points) => set({ mapPoints: points }),
  setIsLoading: (loading) => set({ isLoading: loading }),
}));
