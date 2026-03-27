import React, { useState, useContext, useEffect, useRef } from 'react';
import './Chat.css';
import axios from 'axios';
import { StoreContext } from '../../context/storeContextInstance';
import { toast } from 'react-toastify';
import io from 'socket.io-client';
import { useLocation } from 'react-router-dom';

const Chat = () => {
  const { url } = useContext(StoreContext);
  const token = localStorage.getItem('token');
  const currentUserId = localStorage.getItem("userId");
  const location = useLocation();
  const { recipientId, recipientName, userImage } = location.state || {};
  const socketRef = useRef(null);

  const [users, setUsers] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);
  const [messages, setMessages] = useState([]);
  const [inputText, setInputText] = useState('');
  const messagesEndRef = useRef(null);

  useEffect(() => {
    const fetchInitialData = async () => {
      if (!token) return;
      try {
        const res = await axios.get(`${url}/api/messages`, {
          headers: { Authorization: `Bearer ${token}` }
        });
        const allMessages = res.data.data;
        setMessages(allMessages);

        const uniqueUsersMap = {};
        allMessages.forEach(msg => {
          const user = msg.senderId._id === currentUserId ? msg.recipientId : msg.senderId;
          if (user && user._id) {
            uniqueUsersMap[user._id] = user;
          }
        });


        const uniqueUsers = Object.values(uniqueUsersMap).sort((a, b) => {
          const lastMsgA = [...allMessages].reverse().find(
            msg => msg.senderId._id === a._id || msg.recipientId._id === a._id
          );
          const lastMsgB = [...allMessages].reverse().find(
            msg => msg.senderId._id === b._id || msg.recipientId._id === b._id
          );
          return new Date(lastMsgB?.timestamp) - new Date(lastMsgA?.timestamp);
        });

        setUsers(uniqueUsers);

        if (recipientId) {
          const existingUser = uniqueUsers.find(u => u._id === recipientId);
          if (existingUser) {
            setSelectedUser(existingUser);
          } else {
            const newUser = {
              _id: recipientId,
              name: recipientName,
              userImage: userImage || 'm_avatar1.png'
            };
            setUsers(prev => [...prev, newUser]);
            setSelectedUser(newUser);
          }
        } else if (!selectedUser && uniqueUsers.length > 0) {
          setSelectedUser(uniqueUsers[0]);
        }
      } catch (err) {
        toast.error("Failed to load messages");
      }
    };

    fetchInitialData();
  }, [url, token, currentUserId, recipientId]);

  useEffect(() => {
    if (currentUserId) {
      socketRef.current = io(url);
      socketRef.current.emit("join", { userId: currentUserId });

      socketRef.current.on("receive-message", (newMessage) => {
        setMessages((prevMessages) => [...prevMessages, newMessage]);
      });

      socketRef.current.on("message-sent", (sentMessage) => {
        setMessages((prevMessages) => [...prevMessages, sentMessage]);
      });

      return () => {
        socketRef.current.off("receive-message");
        socketRef.current.off("message-sent");
        socketRef.current.disconnect();
      };
    }
  }, [url, currentUserId]);

  const handleSendMessage = () => {
    if (!inputText.trim() || !selectedUser || !socketRef.current) return;
    socketRef.current.emit("send-message", {
      senderId: currentUserId,
      recipientId: selectedUser._id,
      content: inputText.trim()
    });
    setInputText('');
  };

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth", block: "end" });
  }, [messages]);

  return (
    <div className="chat-container">
      <div className="chat-sidebar">
        <div className="chat-sidebar-header">Conversations</div>
        {users.map((user) => (
          <div
            key={user._id}
            className={`chat-user-entry ${selectedUser?._id === user._id ? 'selected' : ''}`}
            onClick={() => setSelectedUser(user)}
          >
            <img
              src={`${url}/images/${user.userImage}${user.userImage.includes('.') ? '' : '.png'}`}
              className="chat-user-icon"
              alt="User"
            />
            <div className="chat-user-name">{user.name}</div>
          </div>
        ))}
      </div>

      <div className="chat-main">
        {selectedUser ? (
          <>
            <div className="chat-header">
              <img
                src={`${url}/images/${selectedUser.userImage}${selectedUser.userImage.includes('.') ? '' : '.png'}`}
                className="chat-user-icon"
                alt="User"
              />
              <div className="chat-header-name">{selectedUser.name}</div>
            </div>

            <div className="chat-messages">
              {messages
                .filter(msg =>
                  (msg.senderId._id === currentUserId && msg.recipientId._id === selectedUser._id) ||
                  (msg.senderId._id === selectedUser._id && msg.recipientId._id === currentUserId)
                )
                .map((msg) => (
                  <div
                    key={msg._id}
                    className={`chat-message ${msg.senderId._id === currentUserId ? 'sent' : 'received'}`}
                  >
                    <div className="message-content">{msg.content}</div>
                    <div className="message-timestamp">
                      {msg.timestamp ? new Date(msg.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : ''}
                    </div>
                  </div>
                ))}
              <div ref={messagesEndRef} />
            </div>

            <div className="chat-input-area">
              <input
                type="text"
                value={inputText}
                onChange={e => setInputText(e.target.value)}
                placeholder="Type a message..."
                className="chat-input"
                onKeyDown={e => {
                  if (e.key === 'Enter') handleSendMessage();
                }}
              />
              <button onClick={handleSendMessage} className="chat-send-button">
                Send
              </button>
            </div>
          </>
        ) : (
          <div className="no-chat-selected">
            <h2>Select a conversation to start chatting</h2>
          </div>
        )}
      </div>
    </div>
  );
};

export default Chat;
