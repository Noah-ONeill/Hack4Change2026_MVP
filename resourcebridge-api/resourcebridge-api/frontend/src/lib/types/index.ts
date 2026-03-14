export type Role = 'COORDINATOR' | 'STAFF';
export type Urgency = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export type DonationStatus = 'OFFERED' | 'ASSIGNED' | 'DELIVERED' | 'RECEIVED';
export type TransferStatus = 'PENDING' | 'IN_TRANSIT' | 'COMPLETED';
export type AnnouncementType = 'EXPIRY' | 'SURPLUS' | 'URGENT';

export interface Organization {
  id: number;
  name: string;
  type: string;
  address: string;
  populationServed: string;
  contactEmail: string;
  contactPhone: string;
}

export interface Item {
  id: number;
  name: string;
  description: string;
  unit: string;
  expiryRelevant: boolean;
}

export interface Need {
  id: number;
  organization: Organization;
  item: Item;
  quantityNeeded: number;
  urgency: Urgency;
  fulfilled: boolean;
  createdAt: string;
}

export interface Donation {
  id: number;
  donorName: string;
  donorEmail: string;
  item: Item;
  quantity: number;
  expiryDate: string | null;
  status: DonationStatus;
  createdAt: string;
}

export interface Transfer {
  id: number;
  donation: Donation;
  toOrganization: Organization;
  quantityAssigned: number;
  coordinator: { id: number; name: string; email: string };
  status: TransferStatus;
  createdAt: string;
}

export interface Inventory {
  id: number;
  organization: Organization;
  item: Item;
  quantity: number;
  expiryDate: string | null;
}

export interface Announcement {
  id: number;
  organization: Organization;
  item: Item;
  quantity: number;
  type: AnnouncementType;
  message: string;
  createdAt: string;
}

export interface AuthResponse {
  id: number;
  token: string;
  name: string;
  email: string;
  role: Role;
  organizationId: number;
}
