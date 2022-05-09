import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CustomHttpResponse } from '../model/custom-http-response';
import { Question } from '../model/question';
import { QuestionResponse } from '../model/question-response';

@Injectable({
  providedIn: 'root',
})
export class QuestionService {
  host = environment.apiUrl;
  constructor(private http: HttpClient) {}

  getQuestionsBySpeciality(specialityName: string): Observable<Question[]> {
    return this.http.get<Question[]>(
      `${this.host}/questions/${specialityName}`
    );
  }

  public addQuestion(formData: FormData): Observable<Question> {
    return this.http.post<Question>(`${this.host}/questions`, formData);
  }

  public editQuestion(formData: FormData): Observable<Question> {
    return this.http.put<Question>(`${this.host}/questions`, formData);
  }

  deleteQuestion(questionId: bigint): Observable<CustomHttpResponse> {
    return this.http.delete<CustomHttpResponse>(
      `${this.host}/questions/${questionId}`
    );
  }

  addResponse(
    questionId: bigint,
    questionResponse: QuestionResponse
  ): Observable<Question> {
    return this.http.post<Question>(
      `${this.host}/questions/responses/${questionId}`,
      questionResponse
    );
  }

  editQuestionResponse(
    questionId: bigint,
    questionResponse: QuestionResponse
  ): Observable<Question> {
    return this.http.put<Question>(
      `${this.host}/questions/responses/${questionId}`,
      questionResponse
    );
  }

  deleteQuestionResponse(questionId: bigint): Observable<Question> {
    return this.http.delete<Question>(
      `${this.host}/questions/responses/${questionId}`
    );
  }
}
