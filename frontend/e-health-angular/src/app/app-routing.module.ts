import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DoctorProfileComponent } from './component/doctor-profile/doctor-profile.component';
import { DoctorsComponent } from './component/doctors/doctors.component';

import { LoginComponent } from './component/login/login.component';
import { QuestionsComponent } from './component/questions/questions.component';
import { RegisterComponent } from './component/register/register.component';
import { UserComponent } from './component/user/user.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'user/management', component: UserComponent },
  { path: 'find-a-doctor/:username', component: DoctorProfileComponent },
  { path: 'find-a-doctor', component: DoctorsComponent },
  { path: 'ask-a-question', component: QuestionsComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
