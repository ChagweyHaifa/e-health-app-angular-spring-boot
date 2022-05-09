import { QuestionResponse } from './question-response';
import { Speciality } from './speciality';
import { User } from './user';

export class Question {
  id: bigint;
  speciality: Speciality = new Speciality();
  user: User = new User();
  title: string;
  question: string;
  currentTreatment: string;
  medicalHistory: string;
  questionerHeight: number;
  questionerWeight: number;
  response: QuestionResponse = new QuestionResponse();
}
