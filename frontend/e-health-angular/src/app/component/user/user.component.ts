import {
  HttpErrorResponse,
  HttpEvent,
  HttpEventType,
  HttpResponse,
} from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  NgForm,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { BehaviorSubject, Subscription } from 'rxjs';
import { NotificationType } from 'src/app/enum/notification-type.enum';
import { Role } from 'src/app/enum/role.enum';
import { City } from 'src/app/model/city';
import { Country } from 'src/app/model/country';
import { CustomHttpResponse } from 'src/app/model/custom-http-response';
import { Doctor } from 'src/app/model/doctor';
import { DoctorDto } from 'src/app/model/doctor-dto';
import { FileUploadStatus } from 'src/app/model/file-upload-status';
import { Speciality } from 'src/app/model/speciality';
import { State } from 'src/app/model/state';
import { User } from 'src/app/model/user';
import { AuthenticationService } from 'src/app/service/authentication.service';
import { FormService } from 'src/app/service/form.service';
import { NotificationService } from 'src/app/service/notification.service';
import { UserService } from 'src/app/service/user.service';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css'],
})
export class UserComponent implements OnInit, OnDestroy {
  private titleSubject = new BehaviorSubject<string>('Users');
  // a listener , an observable that will get notified if titleSubject value has been changed
  public titleAction$ = this.titleSubject.asObservable();

  public loggedInUser: User;

  public selectedUser: User;

  public editUser = new User();
  private currentUsername: string;

  fileName: string;
  profileImage: File;
  public refreshing: boolean = false;

  public fileStatus = new FileUploadStatus();

  private subscriptions: Subscription[] = [];
  doctors: Doctor[];
  public users: User[];
  specialities: Speciality[];
  selectedDoctor = new Doctor();

  countries: Country[];
  states: State[];
  cities: City[];
  editDoctorForm: FormGroup;
  currentUserUsername: string;
  isEditAbout: boolean = false;
  isEditContact: boolean = false;
  isEditAccountStatus: boolean = false;
  isEditPassword: boolean;

  constructor(
    private userService: UserService,
    private notificationService: NotificationService,
    private authenticationService: AuthenticationService,
    private formService: FormService,
    private router: Router,
    private formBuilder: FormBuilder
  ) {
    this.editDoctorForm = this.formBuilder.group({
      about: this.formBuilder.group({
        firstName: [''],
        lastName: [''],
        username: [''],
        role: [''],
        speciality: this.formBuilder.group({
          id: new FormControl(''),
          name: new FormControl(''),
        }),
        gender: [''],
      }),
      contact: this.formBuilder.group({
        email: [''],
        phoneNumber: [],
        address: this.formBuilder.group({
          country: new FormControl(''),
          state: new FormControl(''),
          city: new FormControl(''),
          street: new FormControl(''),
        }),
      }),
      accountStatus: this.formBuilder.group({
        status: [''],
        active: [],
        notLocked: [],
      }),
    });
  }

  ngOnInit(): void {
    this.loggedInUser = this.authenticationService.getUserFromLocalCache();
    this.getDoctors();
    this.getSpecialities();
  }

  public getDoctors(): void {
    this.subscriptions.push(
      this.userService.getAllDoctors().subscribe(
        (response: Doctor[]) => {
          this.doctors = response;
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
        }
      )
    );
  }

  public getUsers(role: string): void {
    this.subscriptions.push(
      this.userService.getUsersByRole(role).subscribe(
        (response: User[]) => {
          this.users = response;
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
        }
      )
    );
  }

  getSpecialities() {
    this.subscriptions.push(
      this.formService.getSpecialities().subscribe(
        (response: Speciality[]) => {
          this.specialities = response;
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
        }
      )
    );
  }

  launchEditDoctorModal(doctor: Doctor) {
    this.isEditAbout = false;
    this.isEditAccountStatus = false;
    this.isEditContact = false;
    this.isEditPassword = false;
    this.selectedDoctor = doctor;
    this.currentUserUsername = doctor.username;
    this.getCountries();
    this.getStates(doctor.address.country);
    this.getCities(doctor.address.state);
    // console.log(doctor);
    this.editDoctorForm.patchValue(doctor);
    this.clickButton('edit-doctor-modal-trigger-btn');
  }

  onEditDoctor(section: string) {
    switch (section) {
      case 'about': {
        this.isEditAbout = true;
        this.editDoctorForm.patchValue({});
        break;
      }
      case 'contact': {
        this.isEditContact = true;
        break;
      }
      case 'accountStatus': {
        this.isEditAccountStatus = true;
        break;
      }
      case 'password': {
        this.isEditPassword = true;
        break;
      }
    }
  }

  getCountries() {
    this.subscriptions.push(
      this.formService.getCountries().subscribe(
        (response: Country[]) => {
          this.countries = response;
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
        }
      )
    );
  }

  resetAddress(address: string) {
    switch (address) {
      case 'country': {
        this.editDoctorForm.controls['address'].patchValue({
          state: '',
          city: '',
          street: '',
        });
        break;
      }
      case 'state': {
        this.editDoctorForm.controls['address'].patchValue({
          city: '',
          street: '',
        });
        break;
      }
      case 'city': {
        this.editDoctorForm.controls['address'].patchValue({
          street: '',
        });
        break;
      }
    }
  }

  changeLockoutStatus() {
    const lockoutStatus = this.editDoctorForm.value.status;
    if (lockoutStatus == 'VERIFIED') {
      this.editDoctorForm.controls.notLocked.setValue(true);
    } else {
      this.editDoctorForm.controls.notLocked.setValue(false);
    }
  }
  getStates(countryName: string) {
    this.subscriptions.push(
      this.formService.getStates(countryName).subscribe(
        (response: State[]) => {
          this.states = response;
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
        }
      )
    );
  }

  getCities(stateName: string) {
    this.subscriptions.push(
      this.formService.getCities(stateName).subscribe(
        (response: City[]) => {
          this.cities = response;
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
        }
      )
    );
  }

  public editProfileImage(event: Event): void {
    this.profileImage = (<HTMLInputElement>event.target).files[0];
    this.fileName = this.profileImage.name;
  }

  editDoctor() {
    // console.log(this.editDoctorForm.value);
    const doctorDto = new DoctorDto();
    doctorDto.doctor = this.editDoctorForm.value;
    doctorDto.profileImage = this.profileImage;
    doctorDto.currentDoctorUsername = this.currentUserUsername;
    console.log(doctorDto);

    this.subscriptions.push(
      this.userService.updateDoctor(doctorDto).subscribe(
        (response: HttpResponse<Doctor>) => {
          this.getDoctors();
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
        }
      )
    );
  }

  // ****display user info****
  public onSelectUser(selectedUser: User): void {
    this.selectedUser = selectedUser;
    this.clickButton('openUserInfo');
  }

  // ****add new user****
  public saveNewUser(): void {
    this.clickButton('new-user-save');
  }

  onAddNewUser(userForm: NgForm): void {
    // console.log(userForm);
    // console.log(userForm.value);
    const formData = this.userService.createUserFormDate(
      null,
      userForm.value,
      this.profileImage
    );
    this.subscriptions.push(
      this.userService.addUser(formData).subscribe(
        (response: User) => {
          this.clickButton('new-user-close');
          // make a call to the backend to get the new list of users

          this.fileName = null;
          this.profileImage = null;
          userForm.reset();
          this.sendNotification(
            NotificationType.SUCCESS,
            `${response.firstName} ${response.lastName} added successfully`
          );
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
          this.profileImage = null;
        }
      )
    );
  }

  // ****search user****
  // public searchUsers(searchTerm: string): void {
  //   console.log('searchTerm = "' + searchTerm + '"');
  //   const results: User[] = [];
  //   for (const user of this.userService.getUsersFromLocalCache()) {
  //     if (
  //       user.firstName.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
  //       user.lastName.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
  //       user.username.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
  //       user.userId.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1
  //     ) {
  //       results.push(user);
  //     }
  //   }
  //   console.log('length' + results.length);
  //   this.users = results;
  // }

  // ****edit user****
  // user selection
  public onEditUser(editUser: User): void {
    this.editUser = editUser;
    // grab the current username
    this.currentUsername = editUser.username;
    this.clickButton('openUserEdit');
  }
  // userEdit form submission
  public onUpdateUser(): void {
    const formData = this.userService.createUserFormDate(
      this.currentUsername,
      this.editUser,
      this.profileImage
    );

    this.subscriptions.push(
      this.userService.updateUser(formData).subscribe(
        (response: User) => {
          this.clickButton('edit-user-close');

          this.fileName = null;
          this.profileImage = null;
          this.sendNotification(
            NotificationType.SUCCESS,
            `${response.firstName} ${response.lastName} updated successfully`
          );
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
          this.profileImage = null;
        }
      )
    );
  }
  // ****delete user****
  public onDeleteUser(username: string): void {
    this.subscriptions.push(
      this.userService.deleteUser(username).subscribe(
        (response: CustomHttpResponse) => {
          this.sendNotification(NotificationType.SUCCESS, response.message);
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, error.error.message);
        }
      )
    );
  }

  // edit user profile
  // we can pass loggedInUser variable
  onUpdateUserProfile(user: User) {
    this.refreshing = true;
    this.currentUsername =
      this.authenticationService.getUserFromLocalCache().username;
    const formData = this.userService.createUserFormDate(
      this.currentUsername,
      user,
      this.profileImage
    );
    this.subscriptions.push(
      this.userService.updateUser(formData).subscribe(
        (response: User) => {
          this.authenticationService.addUserToLocalCache(response);
          // to update authorities on the template
          // this.loggedInUser = response;

          this.fileName = null;
          this.profileImage = null;
          this.sendNotification(
            NotificationType.SUCCESS,
            `${response.firstName} ${response.lastName} updated successfully`
          );
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
          this.refreshing = false;
          this.profileImage = null;
        }
      )
    );
  }
  // update profile image
  updateProfileImage() {
    this.clickButton('profile-image-input');
  }

  public get isAdmin(): boolean {
    return this.getUserRole() === Role.ADMIN;
    //|| this.getUserRole() === Role.SUPER_ADMIN
  }

  // private methods
  private clickButton(buttonId: string) {
    document.getElementById(buttonId).click();
  }

  private sendNotification(
    notificationType: NotificationType,
    message: string
  ): void {
    if (message) {
      this.notificationService.notify(notificationType, message);
    } else {
      this.notificationService.notify(
        notificationType,
        'An error occurred. Please try again.'
      );
    }
  }

  // private reportUploadProgress(event: HttpEvent<any>): void {
  //   switch (event.type) {
  //     case HttpEventType.UploadProgress:
  //       this.fileStatus.percentage = Math.round(
  //         (100 * event.loaded) / event.total
  //       );
  //       this.fileStatus.status = 'progress';
  //       break;
  //     case HttpEventType.Response:
  //       if (event.status === 200) {
  //         this.loggedInUser.profileImageUrl = `${
  //           event.body.profileImageUrl
  //         }?time=${new Date().getTime()}`;
  //         this.sendNotification(
  //           NotificationType.SUCCESS,
  //           `${event.body.firstName}\'s profile image updated successfully`
  //         );
  //         this.fileStatus.status = 'done';
  //         this.getUsers(false);
  //         break;
  //       } else {
  //         this.sendNotification(
  //           NotificationType.ERROR,
  //           `Unable to upload image. Please try again`
  //         );
  //         break;
  //       }
  //     default:
  //       `Finished all processes`;
  //   }
  // }

  private getUserRole(): string {
    return this.authenticationService.getUserFromLocalCache().role;
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }
}
