export const parseId = (id: string): number | null => {
  const parsed = parseInt(id, 10);
  return isNaN(parsed) ? null : parsed;
};

export const generateUserId = (): number => {
  return Math.floor(Math.random() * 1000000);
};