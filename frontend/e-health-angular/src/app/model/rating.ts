import { Doctor } from './doctor';
import { Visitor } from './visitor';

export class Rating {
  visitor: Visitor;
  doctor: Doctor;
  rating: number;
  review: string;
  creationDate: Date;

  constructor() {
    this.visitor = new Visitor();
    this.doctor = new Doctor();
    this.rating = 0;
    this.review = '';
    this.creationDate = null;
  }
}
