import { useDroppable } from '@dnd-kit/core';
import { SortableContext, verticalListSortingStrategy } from '@dnd-kit/sortable';
import { Task } from '@/types';
import TaskCard from './TaskCard';

interface KanbanColumnProps {
  id: string;
  title: string;
  tasks: Task[];
  color: 'gray' | 'blue' | 'yellow' | 'green';
}

export default function KanbanColumn({ id, title, tasks, color }: KanbanColumnProps) {
  const { setNodeRef, isOver } = useDroppable({ id });

  const colorClasses = {
    gray: 'bg-gray-100 border-gray-300',
    blue: 'bg-blue-50 border-blue-300',
    yellow: 'bg-yellow-50 border-yellow-300',
    green: 'bg-green-50 border-green-300',
  };

  return (
    <div
      ref={setNodeRef}
      className={`rounded-lg p-4 min-h-[500px] transition-colors ${
        isOver ? 'bg-blue-100 border-2 border-blue-400' : colorClasses[color] + ' border-2'
      }`}
    >
      <div className="flex items-center justify-between mb-4">
        <h3 className="font-semibold text-gray-900">{title}</h3>
        <span className="text-sm text-gray-600 bg-white px-2 py-1 rounded">
          {tasks.length}
        </span>
      </div>
      <SortableContext items={tasks.map((t) => t.id)} strategy={verticalListSortingStrategy}>
        <div className="space-y-3">
          {tasks.map((task) => (
            <TaskCard key={task.id} task={task} />
          ))}
        </div>
      </SortableContext>
    </div>
  );
}
