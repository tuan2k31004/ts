import api from './api';
import { LoginSession, ApiResponse } from '@/types';

export const sessionService = {
  getSessions: async (): Promise<LoginSession[]> => {
    const refreshToken = localStorage.getItem('refreshToken');
    const response = await api.get<ApiResponse<LoginSession[]>>(
      `/api/auth/sessions${refreshToken ? `?refreshToken=${refreshToken}` : ''}`
    );
    return response.data.data;
  },

  revokeSession: async (sessionId: string): Promise<void> => {
    await api.delete(`/api/auth/sessions/${sessionId}`);
  },

  revokeAllSessions: async (): Promise<void> => {
    await api.delete('/api/auth/sessions');
  },
};
