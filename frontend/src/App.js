import React, { useState, useEffect } from 'react';
import './App.css';

function App() {
  const [user, setUser] = useState(null);
  const [error, setError] = useState(null);
  const [emails, setEmails] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
    const [isFetchingEmails, setIsFetchingEmails] = useState(false);
  const [selectedEmail, setSelectedEmail] = useState(null);
  const [attachments, setAttachments] = useState([]);
    const [isFetchingAttachments, setIsFetchingAttachments] = useState(false);
  const [nextPageToken, setNextPageToken] = useState(null);

    useEffect(() => {
    const fetchUser = async () => {
      try {
        const response = await fetch('http://localhost:8080/user', { credentials: 'include' });
        if (response.ok) {
          const data = await response.json();
          setUser(data);
        } else {
          setUser(null);
        }
      } catch (err) {
        setError('Could not connect to the backend. Is it running?');
        setUser(null);
      } finally {
        setIsLoading(false);
      }
    };
    fetchUser();
  }, []);

  const login = () => {
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
  };

  const logout = async () => {
    await fetch('http://localhost:8080/logout', { method: 'POST', credentials: 'include' });
    setUser(null);
    setEmails([]);
  };

        const fetchInitialEmails = async () => {
    setIsFetchingEmails(true);
    setError(null);
    setEmails([]);
    setSelectedEmail(null);
    setAttachments([]);
    setNextPageToken(null);

    try {
      const response = await fetch('http://localhost:8080/api/emails', { credentials: 'include' });
      if (response.ok) {
        const data = await response.json();
        setEmails(data.emails);
        setNextPageToken(data.nextPageToken);
      } else {
        setError('Failed to fetch emails. Please try again.');
      }
    } catch (err) {
      setError('An error occurred while fetching emails.');
    } finally {
      setIsFetchingEmails(false);
    }
  };

  const loadMoreEmails = async () => {
    if (!nextPageToken) return;

    setIsFetchingEmails(true);
    setError(null);

    try {
      const response = await fetch(`http://localhost:8080/api/emails?pageToken=${nextPageToken}`, { credentials: 'include' });
      if (response.ok) {
        const data = await response.json();
        setEmails(prevEmails => [...prevEmails, ...data.emails]);
        setNextPageToken(data.nextPageToken);
      } else {
        setError('Failed to fetch more emails. Please try again.');
      }
    } catch (err) {
      setError('An error occurred while fetching more emails.');
    } finally {
      setIsFetchingEmails(false);
    }
  };

      const handleEmailClick = async (email) => {
    if (selectedEmail && selectedEmail.id === email.id) {
      // If the same email is clicked again, hide the attachments
      setSelectedEmail(null);
      setAttachments([]);
      return;
    }

    setSelectedEmail(email);
    setIsFetchingAttachments(true);
    setError(null);
    setAttachments([]);

    try {
      const response = await fetch(`http://localhost:8080/api/attachments/${email.id}`, { credentials: 'include' });
      if (response.ok) {
        const data = await response.json();
        setAttachments(data);
      } else {
        setError('Could not fetch attachments.');
        setSelectedEmail(null); // Deselect email on error
      }
    } catch (err) {
      setError('An error occurred while fetching attachments.');
      setSelectedEmail(null); // Deselect email on error
    } finally {
      setIsFetchingAttachments(false);
    }
  };

  const downloadAttachment = ({ filename, mimeType, data }) => {
    const byteCharacters = atob(data.replace(/-/g, '+').replace(/_/g, '/'));
    const byteNumbers = new Array(byteCharacters.length);
    for (let i = 0; i < byteCharacters.length; i++) {
      byteNumbers[i] = byteCharacters.charCodeAt(i);
    }
    const byteArray = new Uint8Array(byteNumbers);
    const blob = new Blob([byteArray], { type: mimeType });

    const link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

    if (isLoading) {
    return <div className="App"><header className="App-header"><p>Loading...</p></header></div>;
  }

  return (
    <div className="App">
      <header className="App-header">
        <h1>Gmail Plugin</h1>
        {user && user.name !== 'Guest' ? (
          <div>
            <p>Welcome, {user.name}!</p>
            <button onClick={logout}>Logout</button>
            <hr />
                        <div className="button-container">
              <button onClick={fetchInitialEmails} disabled={isFetchingEmails && emails.length === 0}>
                {isFetchingEmails && emails.length === 0 ? 'Fetching...' : 'Get Emails with Attachments'}
              </button>
              {nextPageToken && (
                <button onClick={loadMoreEmails} disabled={isFetchingEmails}>
                  {isFetchingEmails ? 'Loading More...' : 'Load More'}
                </button>
              )}
            </div>
            
            {error && <p className="error-message">{error}</p>}

                        <div className="content-area">
              <ul className="email-list">
                {emails.map((email) => (
                  <li 
                    key={email.id} 
                    onClick={() => handleEmailClick(email)}
                    className={selectedEmail && selectedEmail.id === email.id ? 'selected' : ''}
                  >
                    {email.subject}
                  </li>
                ))}
              </ul>
              <div className="attachment-area">
                {isFetchingAttachments && <p>Loading attachments...</p>}
                {selectedEmail && !isFetchingAttachments && attachments.length === 0 && (
                  <p>No attachments found for this email.</p>
                )}
                {attachments.length > 0 && (
                  <div>
                    <h4>Attachments:</h4>
                    <ul className="attachment-list">
                      {attachments.map((att, index) => (
                        <li key={index} onClick={() => downloadAttachment(att)}>
                          {att.filename}
                        </li>
                      ))}
                    </ul>
                  </div>
                )}
              </div>
            </div>
          </div>
        ) : (
          <div>
            <button onClick={login}>Login with Google</button>
            {error && <p className="error-message">{error}</p>}
          </div>
        )}
      </header>
    </div>
  );
}

export default App;

