import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { sessionService } from '@/services/sessionService';
import { LoginSession } from '@/types';
import { format } from 'date-fns';

export default function Sessions() {
  const navigate = useNavigate();
  const [sessions, setSessions] = useState<LoginSession[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadSessions();
  }, []);

  const loadSessions = async () => {
    try {
      const data = await sessionService.getSessions();
      setSessions(data);
    } catch (error) {
      console.error('Failed to load sessions:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleRevokeSession = async (sessionId: string) => {
    if (!confirm('Are you sure you want to revoke this session?')) return;

    try {
      await sessionService.revokeSession(sessionId);
      loadSessions();
    } catch (error) {
      console.error('Failed to revoke session:', error);
    }
  };

  const handleRevokeAllSessions = async () => {
    if (!confirm('Are you sure you want to revoke all sessions? You will be logged out.')) return;

    try {
      await sessionService.revokeAllSessions();
      localStorage.clear();
      navigate('/login');
    } catch (error) {
      console.error('Failed to revoke all sessions:', error);
    }
  };

  const getDeviceIcon = (deviceType: string) => {
    switch (deviceType) {
      case 'MOBILE':
        return '📱';
      case 'TABLET':
        return '📲';
      case 'DESKTOP':
      default:
        return '💻';
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <div className="flex justify-between items-center">
            <div className="flex items-center gap-4">
              <button
                onClick={() => navigate('/dashboard')}
                className="text-gray-600 hover:text-gray-900"
              >
                ← Back
              </button>
              <h1 className="text-2xl font-bold text-gray-900">Active Sessions</h1>
            </div>
            <button
              onClick={handleRevokeAllSessions}
              className="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700"
            >
              Revoke All Sessions
            </button>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {loading ? (
          <div className="text-center py-12">
            <div className="text-gray-600">Loading sessions...</div>
          </div>
        ) : sessions.length === 0 ? (
          <div className="text-center py-12">
            <div className="text-gray-600">No active sessions found.</div>
          </div>
        ) : (
          <div className="space-y-4">
            {sessions.map((session) => (
              <div
                key={session.id}
                className={`bg-white p-6 rounded-lg shadow ${
                  session.current ? 'ring-2 ring-blue-500' : ''
                }`}
              >
                <div className="flex justify-between items-start">
                  <div className="flex gap-4 flex-1">
                    <div className="text-4xl">{getDeviceIcon(session.deviceType)}</div>
                    <div className="flex-1">
                      <div className="flex items-center gap-2">
                        <h3 className="text-lg font-semibold text-gray-900">
                          {session.browser} on {session.operatingSystem}
                        </h3>
                        {session.current && (
                          <span className="px-2 py-1 text-xs bg-blue-100 text-blue-800 rounded-full">
                            Current Session
                          </span>
                        )}
                      </div>
                      <div className="mt-2 space-y-1 text-sm text-gray-600">
                        <div>
                          <span className="font-medium">Device:</span> {session.deviceType}
                        </div>
                        <div>
                          <span className="font-medium">IP Address:</span> {session.ipAddress}
                        </div>
                        {session.location && (
                          <div>
                            <span className="font-medium">Location:</span> {session.location}
                          </div>
                        )}
                        <div>
                          <span className="font-medium">Signed in:</span>{' '}
                          {format(new Date(session.createdAt), 'PPp')}
                        </div>
                        <div>
                          <span className="font-medium">Last activity:</span>{' '}
                          {format(new Date(session.lastActivity), 'PPp')}
                        </div>
                        <div>
                          <span className="font-medium">Expires:</span>{' '}
                          {format(new Date(session.expiresAt), 'PPp')}
                        </div>
                      </div>
                    </div>
                  </div>
                  {!session.current && (
                    <button
                      onClick={() => handleRevokeSession(session.id)}
                      className="px-4 py-2 text-sm text-red-600 hover:text-red-800 hover:bg-red-50 rounded-md transition-colors"
                    >
                      Revoke
                    </button>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}

        <div className="mt-8 p-4 bg-blue-50 border border-blue-200 rounded-lg">
          <h4 className="font-semibold text-blue-900 mb-2">Session Security</h4>
          <ul className="text-sm text-blue-800 space-y-1">
            <li>• Sessions are automatically revoked after 7 days of inactivity</li>
            <li>• You can have up to 5 concurrent sessions</li>
            <li>• Revoking a session will log out that device immediately</li>
            <li>• Your current session is highlighted in blue</li>
          </ul>
        </div>
      </main>
    </div>
  );
}
