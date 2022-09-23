import { Doctor } from './doctor';

export class QuestionResponse {
  id: bigint;
  content: string;
  doctor: Doctor = new Doctor();
}
