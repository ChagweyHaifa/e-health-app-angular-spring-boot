import { Doctor } from './doctor';
import { Visitor } from './visitor';

export class Review {
  id: number;
  visitor: Visitor;
  doctor: Doctor;
  content: string;
  creationDate: Date;
}
