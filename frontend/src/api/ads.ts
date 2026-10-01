import apiClient from './client';
import { ClassifiedAd, AdMapPoint, PaginatedResponse } from '../types';

export const adsApi = {
  getAllAds: async (page = 0, size = 20): Promise<PaginatedResponse<ClassifiedAd>> => {
    const response = await apiClient.get('/ads', { params: { page, size } });
    return response.data;
  },

  getAdById: async (id: number): Promise<ClassifiedAd> => {
    const response = await apiClient.get(`/ads/${id}`);
    return response.data;
  },

  getMapPoints: async (
    minLat: number,
    maxLat: number,
    minLon: number,
    maxLon: number
  ): Promise<AdMapPoint[]> => {
    const response = await apiClient.get('/ads/map-points', {
      params: { minLat, maxLat, minLon, maxLon },
    });
    return response.data;
  },

  getAdsByCategory: async (
    category: string,
    page = 0,
    size = 20
  ): Promise<PaginatedResponse<ClassifiedAd>> => {
    const response = await apiClient.get(`/ads/category/${category}`, { params: { page, size } });
    return response.data;
  },

  getUserAds: async (
    userId: number,
    page = 0,
    size = 20
  ): Promise<PaginatedResponse<ClassifiedAd>> => {
    const response = await apiClient.get(`/ads/user/${userId}`, { params: { page, size } });
    return response.data;
  },

  createAd: async (ad: {
    title: string;
    description: string;
    price?: number;
    category?: string;
    latitude: number;
    longitude: number;
    locationName?: string;
  }): Promise<ClassifiedAd> => {
    const response = await apiClient.post('/ads', ad);
    return response.data;
  },

  updateAd: async (
    id: number,
    ad: {
      title: string;
      description: string;
      price?: number;
      category?: string;
      latitude: number;
      longitude: number;
      locationName?: string;
    }
  ): Promise<ClassifiedAd> => {
    const response = await apiClient.put(`/ads/${id}`, ad);
    return response.data;
  },

  deleteAd: async (id: number): Promise<void> => {
    await apiClient.delete(`/ads/${id}`);
  },

  uploadImages: async (adId: number, files: File[]): Promise<ClassifiedAd> => {
    const formData = new FormData();
    files.forEach((file) => formData.append('files', file));
    const response = await apiClient.post(`/ads/${adId}/images`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return response.data;
  },

  deleteImage: async (adId: number, imageId: number): Promise<void> => {
    await apiClient.delete(`/ads/${adId}/images/${imageId}`);
  },
};
