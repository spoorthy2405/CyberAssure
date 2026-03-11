export interface RoleDto {
    roleId?: number;
    name?: string;
}

export interface UserDto {
    userId?: number;
    fullName: string;
    email: string;
    phoneNumber?: string;
    companyName?: string;
    industry?: string;
    companySize?: string;
    companyAddress?: string;
    companyWebsite?: string;
    registrationNumber?: string;
    annualRevenue?: string;
    accountStatus?: string;
    createdAt?: string;
    updatedAt?: string;
    role?: RoleDto;
}
