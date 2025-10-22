import api from './api';
import { Task, KanbanBoard, ApiResponse } from '@/types';

export const taskService = {
  getAllTasks: async (): Promise<Task[]> => {
    const response = await api.get<ApiResponse<Task[]>>('/api/tasks');
    return response.data.data;
  },

  getTaskById: async (id: string): Promise<Task> => {
    const response = await api.get<ApiResponse<Task>>(`/api/tasks/${id}`);
    return response.data.data;
  },

  getTasksByProject: async (projectId: string): Promise<Task[]> => {
    const response = await api.get<ApiResponse<Task[]>>(`/api/tasks/project/${projectId}`);
    return response.data.data;
  },

  getKanbanBoard: async (projectId: string): Promise<KanbanBoard> => {
    const response = await api.get<ApiResponse<KanbanBoard>>(
      `/api/tasks/project/${projectId}/kanban`
    );
    return response.data.data;
  },

  createTask: async (data: {
    title: string;
    description?: string;
    projectId: string;
    creatorId: string;
    assigneeId?: string;
    priority?: string;
    dueDate?: string;
    tags?: string;
  }): Promise<Task> => {
    const response = await api.post<ApiResponse<Task>>('/api/tasks', data);
    return response.data.data;
  },

  updateTask: async (
    id: string,
    data: {
      title?: string;
      description?: string;
      assigneeId?: string;
      status?: string;
      priority?: string;
      dueDate?: string;
      position?: number;
      tags?: string;
    }
  ): Promise<Task> => {
    const response = await api.put<ApiResponse<Task>>(`/api/tasks/${id}`, data);
    return response.data.data;
  },

  moveTask: async (id: string, status: string, position: number): Promise<Task> => {
    const response = await api.put<ApiResponse<Task>>(
      `/api/tasks/${id}/move?status=${status}&position=${position}`
    );
    return response.data.data;
  },

  deleteTask: async (id: string): Promise<void> => {
    await api.delete(`/api/tasks/${id}`);
  },
};
