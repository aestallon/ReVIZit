export * from './auth.service';
import { AuthService } from './auth.service';
export * from './profile.service';
import { ProfileService } from './profile.service';
export * from './water.service';
import { WaterService } from './water.service';
export const APIS = [AuthService, ProfileService, WaterService];
