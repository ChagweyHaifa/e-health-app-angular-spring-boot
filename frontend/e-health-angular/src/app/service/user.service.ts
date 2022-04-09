import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { User } from '../model/user';
import { CustomHttpRespone } from '../model/custom-http-response';
import { Doctor } from '../model/doctor';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private host = environment.apiUrl;

  constructor(private http: HttpClient) {}

  public getUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.host}/users`);
  }

  public addUser(formData: FormData): Observable<User> {
    return this.http.post<User>(`${this.host}/users`, formData);
  }

  public updateUser(formData: FormData): Observable<User> {
    return this.http.put<User>(`${this.host}/users`, formData);
  }

  public deleteUser(username: string): Observable<CustomHttpRespone> {
    return this.http.delete<CustomHttpRespone>(
      `${this.host}/users/${username}`
    );
  }

  public getDoctorInfo(username: string): Observable<Doctor> {
    return this.http.get<Doctor>(`${this.host}/users/doctors/${username}`);
  }

  public resetPassword(email: string): Observable<CustomHttpRespone> {
    return this.http.get<CustomHttpRespone>(
      `${this.host}/resetpassword/${email}`
    );
  }

  public updateProfileImage(formData: FormData): Observable<HttpEvent<User>> {
    return this.http.post<User>(`${this.host}/updateProfileImage`, formData, {
      reportProgress: true,
      observe: 'events',
    });
  }

  public addUsersToLocalCache(users: User[]): void {
    localStorage.setItem('users', JSON.stringify(users));
  }

  public getUsersFromLocalCache(): User[] {
    const users = localStorage.getItem('users');
    // if (users != null) {
    return JSON.parse(users);
    // }
    // return users;
  }

  public createUserFormDate(
    loggedInUsername: string,
    user: User,
    profileImage: File
  ): FormData {
    const formData = new FormData();
    formData.append('currentUsername', loggedInUsername);
    formData.append('firstName', user.firstName);
    formData.append('lastName', user.lastName);
    formData.append('username', user.username);
    formData.append('email', user.email);
    formData.append('role', user.role);
    formData.append('profileImage', profileImage);
    formData.append('isActive', JSON.stringify(user.active));
    formData.append('isNonLocked', JSON.stringify(user.notLocked));
    console.log(formData.get('profileImage'));
    return formData;
  }
}
