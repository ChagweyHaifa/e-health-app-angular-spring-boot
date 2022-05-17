import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpResponse } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { User } from '../model/user';
import { CustomHttpResponse } from '../model/custom-http-response';
import { Doctor } from '../model/doctor';
import { DoctorDto } from '../model/doctor-dto';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  host = environment.apiUrl;

  constructor(private http: HttpClient) {}

  public getUsersByRole(role: string): Observable<any> {
    return this.http.get<any>(`${this.host}/users?role=${role}`);
  }

  getAllDoctors(): Observable<Doctor[]> {
    return this.http.get<Doctor[]>(`${this.host}/users/doctors`);
  }

  public updateUser(
    currentUsername: string,
    user: User
  ): Observable<HttpResponse<User>> {
    return this.http.put<User>(
      `${this.host}/users/doctors/${currentUsername}`,
      user,
      {
        observe: 'response',
      }
    );
  }

  public deleteUser(username: string): Observable<CustomHttpResponse> {
    return this.http.delete<CustomHttpResponse>(
      `${this.host}/users/${username}`
    );
  }

  public resetPassword(email: string): Observable<CustomHttpResponse> {
    return this.http.get<CustomHttpResponse>(
      `${this.host}/resetpassword/${email}`
    );
  }
  // doctors
  public getDoctorInfo(username: string): Observable<Doctor> {
    return this.http.get<Doctor>(
      `${this.host}/users/doctors/search/findByUsername/${username}`
    );
  }

  public searchForDoctors(doctor: Doctor): Observable<Doctor[]> {
    return this.http.post<Doctor[]>(
      `${this.host}/users/doctors/search/findByAllParameters `,
      doctor
    );
  }

  public updateDoctor(
    doctor: Doctor,
    currentDoctorUsername: string
  ): Observable<HttpResponse<Doctor>> {
    return this.http.put<Doctor>(
      `${this.host}/users/doctors/${currentDoctorUsername}`,
      doctor,
      {
        observe: 'response',
      }
    );
  }

  public updateProfileImage(formData: FormData): Observable<Doctor> {
    return this.http.post<Doctor>(
      `${this.host}/users/doctors/updateProfileImage`,
      formData
    );
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
