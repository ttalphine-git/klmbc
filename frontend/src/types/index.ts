export interface User {
  id: number;
  email: string;
  fullName: string;
  phoneNumber?: string;
  profileImageUrl?: string;
  bio?: string;
  isVerified: boolean;
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  refreshToken?: string;
  userId: number;
  email: string;
  fullName: string;
  message: string;
}

export interface ClassifiedAd {
  id: number;
  title: string;
  description: string;
  price?: number;
  category?: string;
  status: string;
  latitude: number;
  longitude: number;
  locationName?: string;
  viewCount: number;
  createdAt: string;
  updatedAt: string;
  userId: number;
  userName: string;
  userEmail: string;
  userPhone?: string;
  userProfileImage?: string;
  imageUrls: string[];
}

export interface AdMapPoint {
  id: number;
  title: string;
  latitude: number;
  longitude: number;
  locationName?: string;
  category?: string;
  thumbnailUrl?: string;
  userId: number;
  userName: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
}
