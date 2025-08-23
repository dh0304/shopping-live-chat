export interface LoginRequest {
  nickname: string;
}

export interface LoginResponse {
  userId: number;
  nickname: string;
}