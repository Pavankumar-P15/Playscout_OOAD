export const avatars = [
  'avatars/m_avatar1.png',
  'avatars/m_avatar2.png',
  'avatars/m_avatar3.png',
  'avatars/m_avatar4.png',
  'avatars/m_avatar5.png',
  'avatars/f_avatar1.png',
  'avatars/f_avatar2.png',
  'avatars/f_avatar3.png',
  'avatars/f_avatar4.png',
  'avatars/f_avatar5.png'
];

export const getAvatarLabel = (avatarPath) =>
  avatarPath
    .replace('avatars/', '')
    .replace('.png', '')
    .replace('_', ' ')
    .toUpperCase();