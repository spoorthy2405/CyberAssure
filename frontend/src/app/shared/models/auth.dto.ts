export interface LoginRequestDto {
    email?: string;
    password?: string;
}

export interface RegisterRequestDto {
    fullName?: string;
    email?: string;
    password?: string;
    companyName?: string;
    phoneNumber?: string;
}

export interface AuthResponseDto {
    email?: string;
    role?: string;
    token?: string;
}
