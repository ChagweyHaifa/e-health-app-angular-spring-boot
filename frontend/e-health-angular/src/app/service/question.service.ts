import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Question } from '../model/question';

@Injectable({
  providedIn: 'root',
})
export class QuestionService {
  host = environment.apiUrl;
  constructor(private http: HttpClient) {}

  public addQuestion(formData: FormData): Observable<Question> {
    return this.http.post<Question>(`${this.host}/questions`, formData);
  }
}
