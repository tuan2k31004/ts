import api from './api';
import { Project, ApiResponse } from '@/types';

export const projectService = {
  getAllProjects: async (): Promise<Project[]> => {
    const response = await api.get<ApiResponse<Project[]>>('/api/projects');
    return response.data.data;
  },

  getProjectById: async (id: string): Promise<Project> => {
    const response = await api.get<ApiResponse<Project>>(`/api/projects/${id}`);
    return response.data.data;
  },

  getProjectsByUser: async (userId: string): Promise<Project[]> => {
    const response = await api.get<ApiResponse<Project[]>>(`/api/projects/user/${userId}`);
    return response.data.data;
  },

  createProject: async (data: {
    name: string;
    description?: string;
    ownerId: string;
    startDate?: string;
    endDate?: string;
  }): Promise<Project> => {
    const response = await api.post<ApiResponse<Project>>('/api/projects', data);
    return response.data.data;
  },

  updateProject: async (
    id: string,
    data: {
      name?: string;
      description?: string;
      status?: string;
      startDate?: string;
      endDate?: string;
    }
  ): Promise<Project> => {
    const response = await api.put<ApiResponse<Project>>(`/api/projects/${id}`, data);
    return response.data.data;
  },

  deleteProject: async (id: string): Promise<void> => {
    await api.delete(`/api/projects/${id}`);
  },

  addMember: async (
    projectId: string,
    data: { userId: string; role: string }
  ): Promise<void> => {
    await api.post(`/api/projects/${projectId}/members`, data);
  },

  removeMember: async (projectId: string, userId: string): Promise<void> => {
    await api.delete(`/api/projects/${projectId}/members/${userId}`);
  },
};
