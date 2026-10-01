import { create } from 'zustand';
import { User, AuthResponse } from '../types';
import { authApi } from '../api/auth';

interface AuthStore {
  user: User | null;
  token: string | null;
  isLoading: boolean;
  error: string | null;
  login: (email: string, password: string) => Promise<void>;
  register: (
    email: string,
    password: string,
    fullName: string,
    phoneNumber?: string,
    bio?: string,
    profileImage?: File
  ) => Promise<void>;
  logout: () => void;
  loadFromStorage: () => void;
  isAuthenticated: () => boolean;
}

export const useAuthStore = create<AuthStore>((set, get) => ({
  user: null,
  token: null,
  isLoading: false,
  error: null,

  login: async (email: string, password: string) => {
    set({ isLoading: true, error: null });
    try {
      const response: AuthResponse = await authApi.login(email, password);
      const user: User = {
        id: response.userId,
        email: response.email,
        fullName: response.fullName,
        isVerified: false,
        createdAt: new Date().toISOString(),
      };
      localStorage.setItem('token', response.token);
      localStorage.setItem('user', JSON.stringify(user));
      set({ user, token: response.token, isLoading: false });
    } catch (error: any) {
      set({ error: error.response?.data?.message || 'Login failed', isLoading: false });
      throw error;
    }
  },

  register: async (email, password, fullName, phoneNumber, bio, profileImage) => {
    set({ isLoading: true, error: null });
    try {
      const response: AuthResponse = await authApi.register(
        email,
        password,
        fullName,
        phoneNumber,
        bio,
        profileImage
      );
      const user: User = {
        id: response.userId,
        email: response.email,
        fullName: response.fullName,
        phoneNumber,
        bio,
        isVerified: false,
        createdAt: new Date().toISOString(),
      };
      localStorage.setItem('token', response.token);
      localStorage.setItem('user', JSON.stringify(user));
      set({ user, token: response.token, isLoading: false });
    } catch (error: any) {
      set({ error: error.response?.data?.message || 'Registration failed', isLoading: false });
      throw error;
    }
  },

  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    set({ user: null, token: null });
  },

  loadFromStorage: () => {
    const token = localStorage.getItem('token');
    const userStr = localStorage.getItem('user');
    if (token && userStr) {
      try {
        const user = JSON.parse(userStr);
        set({ token, user });
      } catch (e) {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
      }
    }
  },

  isAuthenticated: () => {
    return get().token !== null && get().user !== null;
  },
}));
