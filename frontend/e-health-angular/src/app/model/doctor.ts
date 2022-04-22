import { Speciality } from './speciality';
import { User } from './user';

export class Doctor extends User {
  speciality: Speciality;
  nbOfRecommendations: number;
  nbOfReviews: number;
  public profileImageUrl: string;
  constructor() {
    super();
    this.speciality = new Speciality();
    this.nbOfRecommendations = 0;
    this.nbOfReviews = 0;
    this.profileImageUrl = '';
  }
}
