import { Doctor } from './doctor';
import { User } from './user';
import { Visitor } from './visitor';

export class Rating {
  user: User = new User();
  doctor: Doctor;
  rating: number;
  review: string;
  creationDate: Date;

  constructor() {
    this.doctor = new Doctor();
    this.rating = 0;
    this.review = '';
    this.creationDate = null;
  }
}
