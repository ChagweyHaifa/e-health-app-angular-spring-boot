import { Status } from '../enum/status.enum';
import { Speciality } from './speciality';
import { User } from './user';

export class Doctor extends User {
  status: string;
  speciality: Speciality = new Speciality();
  public profileImageUrl: string;
  nbOfRatings: number;
  averageOfRatings: number;
}
