import { Speciality } from './speciality';
import { User } from './user';

export class Doctor extends User {
  speciality: Speciality;
  nbOfRecommendations: number;
  nbOfReviews: number;
  constructor() {
    super();
    this.speciality = null;
    this.nbOfRecommendations = 0;
    this.nbOfReviews = 0;
  }
}
