import { Doctor } from './doctor';
import { Visitor } from './visitor';

export class Review {
  id: bigint;
  visitor: Visitor;
  doctor: Doctor;
  content: string;
  creationDate: Date;
}
