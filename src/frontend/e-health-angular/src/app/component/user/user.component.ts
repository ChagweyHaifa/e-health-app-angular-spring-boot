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
import { FileUploadStatus } from 'src/app/model/file-upload-status';
import { Speciality } from 'src/app/model/speciality';
import { State } from 'src/app/model/state';
import { User } from 'src/app/model/user';
import { AuthenticationService } from 'src/app/service/authentication.service';
import { FormService } from 'src/app/service/form.service';
import { NotificationService } from 'src/app/service/notification.service';
import { UserService } from 'src/app/service/user.service';
import { SubSink } from 'subsink';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css'],
})
export class UserComponent implements OnInit, OnDestroy {
  private subs = new SubSink();
  public loggedInUser: User;

  public selectedUser: User;

  public editUser = new User();
  private currentUsername: string;

  fileName: string;
  profileImage: File;
  public refreshing: boolean = false;

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
  showLoading: boolean;

  submitted = false;

  constructor(
    private userService: UserService,
    private notificationService: NotificationService,
    private authenticationService: AuthenticationService,
    private formService: FormService,
    private router: Router,
    private formBuilder: FormBuilder
  ) {
    // this.editDoctorAboutForm({

    // })

    this.editDoctorForm = this.formBuilder.group({
      about: this.formBuilder.group({
        firstName: ['', [Validators.required]],
        lastName: ['', [Validators.required]],
        username: ['', [Validators.required]],
        role: ['', [Validators.required]],
        speciality: this.formBuilder.group({
          id: new FormControl(''),
          name: new FormControl([Validators.required]),
        }),
        gender: ['', [Validators.required]],
      }),
      contact: this.formBuilder.group({
        email: ['', [Validators.required, Validators.email]],
        phoneNumber: ['', [Validators.required]],
        address: this.formBuilder.group({
          id: new FormControl(''),
          country: new FormControl('', [Validators.required]),
          state: new FormControl('', [Validators.required]),
          city: new FormControl('', [Validators.required]),
          street: new FormControl('', [Validators.required]),
        }),
      }),
      accountStatus: this.formBuilder.group({
        status: ['', [Validators.required]],
        active: [[Validators.required]],
        notLocked: [[Validators.required]],
      }),

      changePassword: this.formBuilder.group({
        currentPassword: ['', [Validators.required]],
        password: ['', [Validators.required, Validators.minLength(8)]],
        confirmPassword: ['', [Validators.required]],
      }),
    });
  }

  ngOnInit(): void {
    this.loggedInUser = this.authenticationService.getUserFromLocalCache();
    this.getDoctors();
    this.getSpecialities();
  }

  public getDoctors(): void {
    this.subs.add(
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
    this.subs.add(
      this.userService.getUsersByRole(role).subscribe(
        (response: User[]) => {
          this.users = response;
          console.log(this.users);
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
    this.subs.add(
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
    this.selectedDoctor = doctor;
    this.currentUserUsername = doctor.username;
    this.isEditAbout = false;
    this.isEditAccountStatus = false;
    this.isEditContact = false;
    this.isEditPassword = false;
    this.clickButton('edit-doctor-modal-trigger-btn');
  }

  getCountries() {
    this.subs.add(
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

  getStates(countryName: string) {
    this.subs.add(
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
    this.subs.add(
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

  onEditDoctor(section: string) {
    switch (section) {
      case 'about': {
        this.isEditAbout = true;
        this.editDoctorForm.controls['about'].patchValue(this.selectedDoctor);
        break;
      }
      case 'contact': {
        this.isEditContact = true;
        this.getCountries();
        this.getStates(this.selectedDoctor.address.country);
        this.getCities(this.selectedDoctor.address.state);
        this.editDoctorForm.controls['contact'].patchValue(this.selectedDoctor);
        break;
      }
      case 'accountStatus': {
        this.isEditAccountStatus = true;
        this.editDoctorForm.controls['accountStatus'].patchValue(
          this.selectedDoctor
        );
        break;
      }
      case 'changePassword': {
        this.isEditPassword = true;
        break;
      }
    }
  }

  public editProfileImage(event: Event): void {
    this.profileImage = (<HTMLInputElement>event.target).files[0];
    this.fileName = this.profileImage.name;
  }

  resetAddress(address: string) {
    switch (address) {
      case 'country': {
        this.editDoctorForm.controls['contact'].get('address').patchValue({
          state: '',
          city: '',
          street: '',
        });
        break;
      }
      case 'state': {
        this.editDoctorForm.controls['contact'].get('address').patchValue({
          city: '',
          street: '',
        });
        break;
      }
      case 'city': {
        this.editDoctorForm.controls['contact'].get('address').patchValue({
          street: '',
        });
        break;
      }
    }
  }

  changeLockoutStatus() {
    const lockoutStatus =
      this.editDoctorForm.controls['accountStatus'].get('status').value;
    if (lockoutStatus == 'VERIFIED') {
      this.editDoctorForm.controls['accountStatus']
        .get('notLocked')
        .setValue(true);
    } else {
      this.editDoctorForm.controls['accountStatus']
        .get('notLocked')
        .setValue(false);
    }
  }

  get f() {
    return this.editDoctorForm.controls;
  }

  editDoctor(section: string) {
    this.submitted = true;
    switch (section) {
      case 'about': {
        if (this.editDoctorForm.controls.about.invalid) {
          return;
        }
        break;
      }
      case 'contact': {
        if (this.editDoctorForm.controls.contact.invalid) {
          return;
        }
        break;
      }
      case 'accountStatus': {
        if (this.editDoctorForm.controls.accountStatus.invalid) {
          return;
        }
        break;
      }
      case 'changePassword': {
        if (this.editDoctorForm.controls.changePassword.invalid) {
          return;
        }
        break;
      }
    }

    // console.log(this.editDoctorForm.value);
    console.log(this.selectedDoctor);

    this.showLoading = true;
    switch (section) {
      case 'about': {
        this.selectedDoctor.firstName =
          this.editDoctorForm.controls['about'].value.firstName;
        this.selectedDoctor.lastName =
          this.editDoctorForm.controls['about'].value.lastName;
        this.selectedDoctor.username =
          this.editDoctorForm.controls['about'].value.username;
        this.selectedDoctor.speciality =
          this.editDoctorForm.controls['about'].get('speciality').value;
        this.selectedDoctor.role =
          this.editDoctorForm.controls['about'].value.role;

        this.selectedDoctor.gender =
          this.editDoctorForm.controls['about'].value.gender;
        break;
      }
      case 'contact': {
        this.selectedDoctor.email =
          this.editDoctorForm.controls['contact'].value.email;
        this.selectedDoctor.phoneNumber =
          this.editDoctorForm.controls['contact'].value.phoneNumber;
        this.selectedDoctor.address =
          this.editDoctorForm.controls['contact'].value.address;
        break;
      }
      case 'accountStatus': {
        this.selectedDoctor.status =
          this.editDoctorForm.controls['accountStatus'].value.status;
        this.selectedDoctor.active =
          this.editDoctorForm.controls['accountStatus'].value.active;
        this.selectedDoctor.notLocked =
          this.editDoctorForm.controls['accountStatus'].value.notLocked;
        break;
      }
      case 'changePassword': {
        this.selectedDoctor.password =
          this.editDoctorForm.controls['changePassword'].value.password;
      }
    }

    this.subs.add(
      this.userService
        .updateDoctor(this.selectedDoctor, this.currentUserUsername)
        .subscribe(
          (response: HttpResponse<Doctor>) => {
            this.selectedDoctor = response.body;
            this.getDoctors();
            this.showLoading = false;

            this.sendNotification(
              NotificationType.SUCCESS,
              'you have edited successfully'
            );
            switch (section) {
              case 'about': {
                this.isEditAbout = false;
                break;
              }
              case 'contact': {
                this.isEditContact = false;
                break;
              }
              case 'accountStatus': {
                this.isEditAccountStatus = false;
                break;
              }
              case 'changePassword': {
                this.isEditPassword = false;
                break;
              }
            }
          },
          (errorResponse: HttpErrorResponse) => {
            this.showLoading = false;
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
    // this.subs.add(
    //   this.userService.addUser(formData).subscribe(
    //     (response: User) => {
    //       this.clickButton('new-user-close');
    //       // make a call to the backend to get the new list of users

    //       this.fileName = null;
    //       this.profileImage = null;
    //       userForm.reset();
    //       this.sendNotification(
    //         NotificationType.SUCCESS,
    //         `${response.firstName} ${response.lastName} added successfully`
    //       );
    //     },
    //     (errorResponse: HttpErrorResponse) => {
    //       this.sendNotification(
    //         NotificationType.ERROR,
    //         errorResponse.error.message
    //       );
    //       this.profileImage = null;
    //     }
    //   )
    // );
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

    // this.subs.add(
    //   this.userService.updateUser(formData).subscribe(
    //     (response: User) => {
    //       this.clickButton('edit-user-close');

    //       this.fileName = null;
    //       this.profileImage = null;
    //       this.sendNotification(
    //         NotificationType.SUCCESS,
    //         `${response.firstName} ${response.lastName} updated successfully`
    //       );
    //     },
    //     (errorResponse: HttpErrorResponse) => {
    //       this.sendNotification(
    //         NotificationType.ERROR,
    //         errorResponse.error.message
    //       );
    //       this.profileImage = null;
    //     }
    //   )
    // );
  }
  // ****delete user****
  public onDeleteUser(username: string): void {
    this.subs.add(
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

  private getUserRole(): string {
    return this.authenticationService.getUserFromLocalCache().role;
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }
}
