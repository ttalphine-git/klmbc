import apiClient from './client';
import { AuthResponse } from '../types';

export const authApi = {
  register: async (
    email: string,
    password: string,
    fullName: string,
    phoneNumber?: string,
    bio?: string,
    profileImage?: File
  ): Promise<AuthResponse> => {
    const formData = new FormData();
    const request = { email, password, fullName, phoneNumber, bio };
    formData.append('request', new Blob([JSON.stringify(request)], { type: 'application/json' }));
    if (profileImage) {
      formData.append('profileImage', profileImage);
    }

    const response = await apiClient.post('/auth/register', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return response.data;
  },

  login: async (email: string, password: string): Promise<AuthResponse> => {
    const response = await apiClient.post('/auth/login', null, {
      params: { email, password },
    });
    return response.data;
  },

  validateToken: async (token: string): Promise<boolean> => {
    const response = await apiClient.get('/auth/validate', {
      headers: { Authorization: `Bearer ${token}` },
    });
    return response.data;
  },
};
