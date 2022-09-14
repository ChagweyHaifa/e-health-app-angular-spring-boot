import { Doctor } from './doctor';

export class DoctorDto {
  profileImage: File;
  doctor: Doctor = new Doctor();

  currentDoctorUsername: string;
}
